package br.com.graflogic.hermitex.cliente.service.impl.pedido;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaJanelaCompra;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatusJanelaCompra;
import br.com.graflogic.hermitex.cliente.data.entity.aud.JanelaCompraAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.JanelaCompra;
import br.com.graflogic.hermitex.cliente.data.impl.aud.JanelaCompraAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.JanelaCompraRepository;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class JanelaCompraService {

	@Autowired
	private JanelaCompraRepository repository;

	@Autowired
	private JanelaCompraAuditoriaRepository auditoriaRepository;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(JanelaCompra entity) {
		entity.setStatus(DomStatusJanelaCompra.CADASTRADA);

		if (!repository.isJanelaComprasDisponivel(entity.getIdCliente(), entity.getDataAbertura(), entity.getDataFechamento())) {
			throw new DadosInvalidosException("Já existe uma janela cadastrada nesse período");
		}

		repository.store(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaJanelaCompra.CADASTRO, null);
	}

	public void fecha(JanelaCompra entity) {
		JanelaCompra entityAtual = consultaPorId(entity.getId());

		if (!DomStatusJanelaCompra.CADASTRADA.equals(entityAtual.getStatus())) {
			throw new DadosInvalidosException("A janela não está mais ativa e não pode ser fechada");
		}

		entity.setStatus(DomStatusJanelaCompra.FECHADA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaJanelaCompra.FECHAMENTO, null);
	}

	private void executaAtualiza(JanelaCompra entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<JanelaCompra> consulta(JanelaCompra entity) {
		return repository.consulta(entity);
	}

	public JanelaCompra consultaPorId(Integer id) {
		JanelaCompra entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	public JanelaCompra consultaPorClienteData(Integer idCliente, Date data) {
		try {
			return repository.consultaPorClienteData(idCliente, data);

		} catch (NoResultException e) {
			throw new ResultadoNaoEncontradoException();
		}
	}

	public JanelaCompra consultaAtiva(Integer idCliente) {
		try {
			JanelaCompra entity = consultaPorClienteData(idCliente, new Date());

			if (!DomStatusJanelaCompra.CADASTRADA.equals(entity.getStatus())) {
				throw new DadosInvalidosException("A janela de compras está fechada");
			}

			return entity;

		} catch (ResultadoNaoEncontradoException e) {
			throw new DadosInvalidosException("Não existe janela de compras cadastrada");
		}
	}

	// Util
	private String registraAuditoria(Integer id, JanelaCompra objeto, String codigoEvento, String observacao) {
		JanelaCompraAuditoria evento = new JanelaCompraAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (JanelaCompra) ObjectCopier.copy(objeto);

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}
}