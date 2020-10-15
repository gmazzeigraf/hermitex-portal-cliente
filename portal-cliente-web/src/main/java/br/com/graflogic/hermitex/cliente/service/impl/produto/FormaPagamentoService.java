package br.com.graflogic.hermitex.cliente.service.impl.produto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.base.service.util.CacheUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaFormaPagamento;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoFilial;
import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatusFormaPagamento;
import br.com.graflogic.hermitex.cliente.data.entity.aud.FormaPagamentoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.Cotacao;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.produto.FormaPagamento;
import br.com.graflogic.hermitex.cliente.data.impl.aud.FormaPagamentoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.FormaPagamentoRepository;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class FormaPagamentoService {

	private static final String CACHE_NAME = "formasPagamento";

	@Autowired
	private FormaPagamentoRepository repository;

	@Autowired
	private FormaPagamentoAuditoriaRepository auditoriaRepository;

	@Autowired
	private FilialService filialService;

	@Autowired
	private CacheUtil cacheUtil;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(FormaPagamento entity) {
		entity.setStatus(DomStatusFormaPagamento.ATIVA);

		repository.store(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaFormaPagamento.CADASTRO, null);

		limpaCache(entity.getIdCliente());
	}

	@Transactional(rollbackFor = Throwable.class)
	public void atualiza(FormaPagamento entity) {
		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaFormaPagamento.ATUALIZACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void inativa(FormaPagamento entity) {
		entity.setStatus(DomStatusFormaPagamento.INATIVA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaFormaPagamento.INATIVACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void ativa(FormaPagamento entity) {
		entity.setStatus(DomStatusFormaPagamento.ATIVA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaFormaPagamento.ATIVACAO, null);
	}

	private void executaAtualiza(FormaPagamento entity) {
		try {
			repository.update(entity);

			limpaCache(entity.getIdCliente());

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<FormaPagamento> consulta(FormaPagamento entity) {
		return repository.consulta(entity);
	}

	@SuppressWarnings("unchecked")
	public List<FormaPagamento> consultaPorCliente(Integer idCliente, boolean somenteAtivas) {
		String key = idCliente.toString();

		if (somenteAtivas) {
			key += "ativas";
		}

		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);

		if (null == cacheObj) {
			// Consulta
			FormaPagamento filter = new FormaPagamento();
			filter.setIdCliente(idCliente);
			if (somenteAtivas) {
				filter.setStatus(DomStatusFormaPagamento.ATIVA);
			}

			List<FormaPagamento> objetos = consulta(filter);

			// Atualiza o cache
			cacheUtil.putOnCache(CACHE_NAME, key, ObjectCopier.copy(objetos));

			cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);
		}

		return (List<FormaPagamento>) ObjectCopier.copy(cacheObj);
	}

	public FormaPagamento consultaPorId(Integer id) {
		FormaPagamento entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	public List<FormaPagamento> gera(Pedido pedido) {
		List<FormaPagamento> entities = new ArrayList<>();

		// Consulta as ativas
		List<FormaPagamento> entitiesAtivas = consultaPorCliente(pedido.getIdCliente(), true);

		Filial filial = null;
		if (null != pedido.getIdFilial()) {
			// Consulta a filial
			filial = filialService.consultaPorId(pedido.getIdFilial());

			// Verifica se a compra esta bloqueada 
			if (filialService.isCompraBloqueada(pedido.getIdFilial())) {
				return entities;
			}
		}

		// Separa as formas de acordo com as regras
		for (FormaPagamento entity : entitiesAtivas) {
			// Verifica se o pedido tem o valor minimo
			if (pedido.getValorProdutos().compareTo(entity.getValorPedidoMinimo()) < 0) {
				continue;
			}

			// Caso seja cliente, verifica
			if (null == pedido.getIdFilial()) {
				if (DomBoolean.SIM.equals(entity.getCliente())) {
					entities.add(entity);
				}
			} else {
				// Caso seja filial, verifica o credito e tipo
				if (DomBoolean.SIM.equals(entity.getCreditoObrigatorio()) && DomBoolean.SIM.equals(filial.getSemCredito())) {
					continue;
				}

				if ((DomTipoFilial.FILIAL.equals(filial.getTipo()) && DomBoolean.NAO.equals(entity.getFilial()))
						|| (DomTipoFilial.FRANQUIA.equals(filial.getTipo()) && DomBoolean.NAO.equals(entity.getFranquia()))
						|| (DomTipoFilial.LOJA_PROPRIA.equals(filial.getTipo()) && DomBoolean.NAO.equals(entity.getLojaPropria()))
						|| (DomTipoFilial.UNIDADE.equals(filial.getTipo()) && DomBoolean.NAO.equals(entity.getUnidade()))) {
					continue;
				}

				entities.add(entity);
			}
		}

		// Gera as descricoes e descontos
		for (FormaPagamento entity : entities) {
			String descricao = entity.getDeTipo();

			BigDecimal valorTotal = pedido.getValorTotal();

			String descricaoDesconto = "";

			if (BigDecimal.ZERO.compareTo(entity.getPorcentagemDesconto()) < 0) {
				// Calcula o desconto
				entity.setValorDesconto(pedido.getValorProdutos().divide(new BigDecimal("100")).multiply(entity.getPorcentagemDesconto()).setScale(2,
						RoundingMode.HALF_EVEN));

				valorTotal = valorTotal.subtract(entity.getValorDesconto()).setScale(2, RoundingMode.HALF_EVEN);

				descricaoDesconto = " (com " + entity.getPorcentagemDesconto().setScale(2, RoundingMode.HALF_EVEN).toString().replace(".", ",")
						+ "% de desconto)";

			} else {
				entity.setValorDesconto(BigDecimal.ZERO);
			}

			BigDecimal valorParcela = valorTotal.divide(new BigDecimal(entity.getQuantidadeParcelas()), 2, RoundingMode.HALF_EVEN);

			if (entity.isBoleto() || entity.isCartaoCredito()) {
				descricao += " " + entity.getQuantidadeParcelas() + "x de R$ " + valorParcela.toString().replace(".", ",") + descricaoDesconto;

				if (entity.isBoleto()) {
					descricao += " para " + entity.getConfiguracao().replace(";", "/") + " dias";
				}

			} else if (entity.isFaturamento()) {
				descricao = entity.getConfiguracao() + descricaoDesconto;
			}

			entity.setDescricao(descricao);
			entity.setValorParcela(valorParcela);
		}

		return entities;
	}
	
	public List<FormaPagamento> gera(Cotacao cotacao) {
		List<FormaPagamento> entities = new ArrayList<>();

		// Consulta as ativas
		List<FormaPagamento> entitiesAtivas = consultaPorCliente(cotacao.getIdCliente(), true);

		Filial filial = null;
		if (null != cotacao.getIdFilial()) {
			// Consulta a filial
			filial = filialService.consultaPorId(cotacao.getIdFilial());

			// Verifica se a compra esta bloqueada 
			if (filialService.isCompraBloqueada(cotacao.getIdFilial())) {
				return entities;
			}
		}

		// Separa as formas de acordo com as regras
		for (FormaPagamento entity : entitiesAtivas) {
			// Verifica se o pedido tem o valor minimo
			if (cotacao.getValorProdutos().compareTo(entity.getValorPedidoMinimo()) < 0) {
				continue;
			}

			// Caso seja cliente, verifica
			if (null == cotacao.getIdFilial()) {
				if (DomBoolean.SIM.equals(entity.getCliente())) {
					entities.add(entity);
				}
			} else {
				// Caso seja filial, verifica o credito e tipo
				if (DomBoolean.SIM.equals(entity.getCreditoObrigatorio()) && DomBoolean.SIM.equals(filial.getSemCredito())) {
					continue;
				}

				if ((DomTipoFilial.FILIAL.equals(filial.getTipo()) && DomBoolean.NAO.equals(entity.getFilial()))
						|| (DomTipoFilial.FRANQUIA.equals(filial.getTipo()) && DomBoolean.NAO.equals(entity.getFranquia()))
						|| (DomTipoFilial.LOJA_PROPRIA.equals(filial.getTipo()) && DomBoolean.NAO.equals(entity.getLojaPropria()))
						|| (DomTipoFilial.UNIDADE.equals(filial.getTipo()) && DomBoolean.NAO.equals(entity.getUnidade()))) {
					continue;
				}

				entities.add(entity);
			}
		}

		// Gera as descricoes e descontos
		for (FormaPagamento entity : entities) {
			String descricao = entity.getDeTipo();

			BigDecimal valorTotal = cotacao.getValorTotal();

			String descricaoDesconto = "";

			if (BigDecimal.ZERO.compareTo(entity.getPorcentagemDesconto()) < 0) {
				// Calcula o desconto
				entity.setValorDesconto(cotacao.getValorProdutos().divide(new BigDecimal("100")).multiply(entity.getPorcentagemDesconto()).setScale(2,
						RoundingMode.HALF_EVEN));

				valorTotal = valorTotal.subtract(entity.getValorDesconto()).setScale(2, RoundingMode.HALF_EVEN);

				descricaoDesconto = " (com " + entity.getPorcentagemDesconto().setScale(2, RoundingMode.HALF_EVEN).toString().replace(".", ",")
						+ "% de desconto)";

			} else {
				entity.setValorDesconto(BigDecimal.ZERO);
			}

			BigDecimal valorParcela = valorTotal.divide(new BigDecimal(entity.getQuantidadeParcelas()), 2, RoundingMode.HALF_EVEN);

			if (entity.isBoleto() || entity.isCartaoCredito()) {
				descricao += " " + entity.getQuantidadeParcelas() + "x de R$ " + valorParcela.toString().replace(".", ",") + descricaoDesconto;

				if (entity.isBoleto()) {
					descricao += " para " + entity.getConfiguracao().replace(";", "/") + " dias";
				}

			} else if (entity.isFaturamento()) {
				descricao = entity.getConfiguracao() + descricaoDesconto;
			}

			entity.setDescricao(descricao);
			entity.setValorParcela(valorParcela);
		}

		return entities;
	}

	// Util
	private String registraAuditoria(Integer id, FormaPagamento objeto, String codigoEvento, String observacao) {
		FormaPagamentoAuditoria evento = new FormaPagamentoAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (FormaPagamento) ObjectCopier.copy(objeto);

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}

	private void limpaCache(Integer idCliente) {
		cacheUtil.putOnCache(CACHE_NAME, idCliente.toString(), null);
		cacheUtil.putOnCache(CACHE_NAME, idCliente.toString() + "ativas", null);
	}
}