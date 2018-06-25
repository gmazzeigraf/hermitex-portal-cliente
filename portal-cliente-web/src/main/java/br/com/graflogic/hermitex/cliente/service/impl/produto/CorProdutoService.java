package br.com.graflogic.hermitex.cliente.service.impl.produto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaCorProduto;
import br.com.graflogic.hermitex.cliente.data.entity.aud.CorProdutoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.produto.CorProduto;
import br.com.graflogic.hermitex.cliente.data.impl.aud.CorProdutoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.CorProdutoRepository;
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
public class CorProdutoService {

	@Autowired
	private CorProdutoRepository repository;

	@Autowired
	private CorProdutoAuditoriaRepository auditoriaRepository;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(CorProduto entity) {
		try {
			consultaPorHexa(entity.getHexa());

			throw new DadosInvalidosException("Hexa já cadastrado");

		} catch (ResultadoNaoEncontradoException e) {
		}

		repository.store(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaCorProduto.CADASTRO, null);
	}

	public void atualiza(CorProduto entity) {
		try {
			CorProduto entityExistente = consultaPorHexa(entity.getHexa());

			if (!entityExistente.getId().equals(entity.getId())) {
				throw new DadosInvalidosException("Hexa já cadastrado");
			}

		} catch (ResultadoNaoEncontradoException e) {
		}

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaCorProduto.ATUALIZACAO, null);
	}

	private void executaAtualiza(CorProduto entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<CorProduto> consulta(CorProduto entity) {
		return repository.consulta(entity);
	}

	public CorProduto consultaPorId(Integer id) {
		CorProduto entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	public CorProduto consultaPorHexa(String hexa) {
		try {
			return repository.consultaPorHexa(hexa);

		} catch (NoResultException e) {
			throw new ResultadoNaoEncontradoException();
		}
	}

	// Util
	private String registraAuditoria(Integer id, CorProduto objeto, String codigoEvento, String observacao) {
		CorProdutoAuditoria evento = new CorProdutoAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (CorProduto) ObjectCopier.copy(objeto);

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}
}