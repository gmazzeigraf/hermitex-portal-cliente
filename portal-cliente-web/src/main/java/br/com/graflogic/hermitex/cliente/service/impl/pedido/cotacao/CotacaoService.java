package br.com.graflogic.hermitex.cliente.service.impl.pedido.cotacao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaCotacao;
import br.com.graflogic.hermitex.cliente.data.dom.DomCotacao.DomStatus;
import br.com.graflogic.hermitex.cliente.data.entity.aud.CotacaoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.Cotacao;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoFrete;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoItem;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoSimple;
import br.com.graflogic.hermitex.cliente.data.entity.produto.FormaPagamento;
import br.com.graflogic.hermitex.cliente.data.impl.aud.CotacaoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cotacao.CotacaoEnderecoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cotacao.CotacaoFreteRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cotacao.CotacaoItemRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cotacao.CotacaoRepository;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.model.pedido.DadosPagamentoCartaoCredito;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class CotacaoService {

	@Autowired
	private CotacaoRepository repository;

	@Autowired
	private CotacaoAuditoriaRepository auditoriaRepository;

	@Autowired
	private CotacaoItemRepository itemRepository;

	@Autowired
	private CotacaoEnderecoRepository enderecoRepository;

	@Autowired
	private CotacaoFreteRepository freteRepository;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Cotacao entity, FormaPagamento formaPagamento, DadosPagamentoCartaoCredito dadosPagamentoCartaoCredito, Integer idUsuario) {
		// Verifica se a compra esta bloqueada
		if (null != entity.getIdFilial() && filialService.isCompraBloqueada(entity.getIdFilial())) {
			throw new DadosInvalidosException("Não foi possível prosseguir com a cotação, contate o administrador");
		}

		// Verifica o status do cliente
		Cliente cliente = clienteService.consultaPorId(entity.getIdCliente());

		if (!cliente.isAtivo()) {
			throw new DadosInvalidosException("Cliente inativo, contate o administrador");
		}

		if (null != entity.getIdFilial() && 0 != entity.getIdFilial()) {
			// Caso seja filial, valida o status
			Filial filial = filialService.consultaPorId(entity.getIdFilial());

			if (!filial.isAtiva()) {
				throw new DadosInvalidosException("Filial inativa, contate o administrador");
			}
		}

		validaDados(entity);

		entity.setStatus(DomStatus.GERADA);

		List<CotacaoItem> itens = entity.getItens();
		entity.setItens(null);

		List<CotacaoFrete> fretes = entity.getFretes();
		entity.setFretes(null);

		try {
			// Cadastra
			repository.store(entity);

			// Atualiza
			for (CotacaoItem item : itens) {
				item.setIdCotacao(entity.getId());
			}

			entity.setItens(itens);

			for (CotacaoFrete frete : fretes) {
				frete.setIdCotacao(entity.getId());
			}

			entity.setFretes(fretes);

			registraAuditoria(entity.getId(), entity, DomEventoAuditoriaCotacao.CADASTRO, idUsuario, null);

			executaAtualiza(entity);

		} catch (Exception t) {
			entity.setItens(itens);
			entity.setFretes(fretes);
			entity.setId(null);

			throw t;
		}
	}

	@Transactional(rollbackFor = Throwable.class)
	public void finaliza(Cotacao entity, Integer idUsuario, String observacao) {
		if (!entity.isGerada()) {
			throw new DadosInvalidosException("Apenas cotações geradas podem ser marcados como finalizadas");
		}

		// TODO Gera pedido

		entity.setStatus(DomStatus.FINALIZADA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaCotacao.FINALIZACAO, idUsuario, observacao);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void cancela(Cotacao entity, Integer idUsuario, String observacao) {
		if (!entity.isGerada()) {
			throw new DadosInvalidosException("Apenas cotações geradas podem ser canceladas");
		}

		entity.setStatus(DomStatus.CANCELADA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaCotacao.CANCELAMENTO, idUsuario, observacao);
	}

	private void executaAtualiza(Cotacao entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<CotacaoSimple> consulta(CotacaoSimple entity) {
		return repository.consulta(entity);
	}

	public Cotacao consultaPorId(Long id) {
		Cotacao entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	@Transactional(readOnly = true)
	public synchronized Cotacao consultaCompletoPorId(Long id) {
		Cotacao entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		preencheRelacionados(entity);

		return entity;
	}

	public List<CotacaoItem> consultaItens(Long id) {
		return itemRepository.consultaPorCotacao(id);
	}

	public CotacaoItem consultaItemPorId(Long idItem) {
		return itemRepository.findById(idItem);
	}

	// Util
	private void validaDados(Cotacao entity) {
		if (entity.getItens().isEmpty()) {
			throw new DadosInvalidosException("Ao menos um item deve ser adicionado");
		}
	}

	private String registraAuditoria(Long id, Cotacao objeto, String codigoEvento, Integer idUsuario, String observacao) {
		CotacaoAuditoria evento = new CotacaoAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(idUsuario);
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (Cotacao) ObjectCopier.copy(objeto);
			// Remove as referencias recursivas
			for (CotacaoItem item : objeto.getItens()) {
				item.setCotacao(null);
			}
			for (CotacaoEndereco endereco : objeto.getEnderecos()) {
				endereco.setCotacao(null);
			}
			for (CotacaoFrete frete : objeto.getFretes()) {
				frete.setCotacao(null);
			}

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}

	private void preencheRelacionados(Cotacao entity) {
		entity.setItens(consultaItens(entity.getId()));
		entity.setEnderecos(enderecoRepository.consultaPorCotacao(entity.getId()));
		entity.setFretes(freteRepository.consultaPorCotacao(entity.getId()));
	}
}