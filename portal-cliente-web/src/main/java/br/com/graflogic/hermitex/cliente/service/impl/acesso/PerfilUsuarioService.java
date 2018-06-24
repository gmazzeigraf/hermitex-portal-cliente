package br.com.graflogic.hermitex.cliente.service.impl.acesso;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.base.service.util.CacheUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomStatusPerfilUsuario;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaPerfilUsuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuario;
import br.com.graflogic.hermitex.cliente.data.entity.aud.PerfilUsuarioAuditoria;
import br.com.graflogic.hermitex.cliente.data.impl.acesso.PerfilUsuarioRepository;
import br.com.graflogic.hermitex.cliente.data.impl.aud.PerfilUsuarioAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class PerfilUsuarioService {

	private static final String CACHE_NAME = "perfisUsuario";

	@Autowired
	private PerfilUsuarioRepository repository;

	@Autowired
	private PerfilUsuarioAuditoriaRepository auditoriaRepository;

	@Autowired
	private CacheUtil cacheUtil;

	// Fluxo
	public void cadastra(PerfilUsuario entity) {
		entity.setStatus(DomStatusPerfilUsuario.ATIVO);

		repository.store(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaPerfilUsuario.CADASTRO, null);

		cacheUtil.clearCache(CACHE_NAME);
	}

	public void atualiza(PerfilUsuario entity) {
		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaPerfilUsuario.ATUALIZACAO, null);

		cacheUtil.clearCache(CACHE_NAME);
	}

	public void inativa(PerfilUsuario entity) {
		entity.setStatus(DomStatusPerfilUsuario.INATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaPerfilUsuario.INATIVACAO, null);
	}

	private void executaAtualiza(PerfilUsuario entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	@SuppressWarnings("unchecked")
	public List<PerfilUsuario> consulta(String tipoUsuario) {
		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, tipoUsuario);

		if (null == cacheObj) {
			PerfilUsuario filter = new PerfilUsuario();
			filter.setTipoUsuario(tipoUsuario);

			List<PerfilUsuario> objetos = repository.consulta(filter);
			cacheUtil.putOnCache(CACHE_NAME, tipoUsuario, ObjectCopier.copy(objetos));

			cacheObj = cacheUtil.findOnCache(CACHE_NAME, tipoUsuario);
		}

		return (List<PerfilUsuario>) ObjectCopier.copy(cacheObj);
	}

	public List<PerfilUsuario> consulta(PerfilUsuario entity) {
		return repository.consulta(entity);
	}

	public PerfilUsuario consultaPorId(Integer id) {
		PerfilUsuario entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	// Util
	private String registraAuditoria(Integer id, PerfilUsuario objeto, String codigoEvento, String observacao) {
		PerfilUsuarioAuditoria evento = new PerfilUsuarioAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}
}