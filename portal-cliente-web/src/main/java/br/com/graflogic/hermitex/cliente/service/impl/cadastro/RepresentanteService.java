package br.com.graflogic.hermitex.cliente.service.impl.cadastro;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.base.service.util.CacheUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomTipoUsuario;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaRepresentante;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomStatusRepresentante;
import br.com.graflogic.hermitex.cliente.data.entity.aud.RepresentanteAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Representante;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.RepresentanteContato;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.RepresentanteEndereco;
import br.com.graflogic.hermitex.cliente.data.impl.aud.RepresentanteAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cadastro.RepresentanteContatoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cadastro.RepresentanteEnderecoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cadastro.RepresentanteRepository;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.PerfilUsuarioService;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class RepresentanteService {

	private static final String CACHE_NAME = "representantes";

	@Autowired
	private RepresentanteRepository repository;

	@Autowired
	private RepresentanteAuditoriaRepository auditoriaRepository;

	@Autowired
	private RepresentanteEnderecoRepository enderecoRepository;

	@Autowired
	private RepresentanteContatoRepository contatoRepository;

	@Autowired
	private PerfilUsuarioService perfilUsuarioService;

	@Autowired
	private CacheUtil cacheUtil;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Representante entity) {
		validaDados(entity);

		entity.setStatus(DomStatusRepresentante.ATIVO);

		List<RepresentanteContato> contatos = entity.getContatos();
		entity.setContatos(null);

		try {
			try {
				repository.consultaPorCnpj(entity.getCnpj());

				throw new DadosInvalidosException("CNPJ já cadastrado");

			} catch (NoResultException e) {
			}

			repository.store(entity);

			for (RepresentanteContato contato : contatos) {
				contato.setIdRepresentante(entity.getId());
			}

			entity.setContatos(contatos);

			repository.update(entity);

			registraAuditoria(entity.getId(), entity, DomEventoAuditoriaRepresentante.CADASTRO, null);

			// Cadastra o perfil de usuario padrao
			perfilUsuarioService.cadastraPadrao(DomTipoUsuario.REPRESENTANTE, entity.getId());

		} catch (Throwable t) {
			entity.setContatos(contatos);

			throw t;
		}
	}

	@Transactional(rollbackFor = Throwable.class)
	public void atualiza(Representante entity) {
		validaDados(entity);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaRepresentante.ATUALIZACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void inativa(Representante entity) {
		entity.setStatus(DomStatusRepresentante.INATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaRepresentante.INATIVACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void ativa(Representante entity) {
		entity.setStatus(DomStatusRepresentante.ATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaRepresentante.ATIVACAO, null);
	}

	private void executaAtualiza(Representante entity) {
		try {
			repository.update(entity);

			cacheUtil.putOnCache(CACHE_NAME, entity.getId().toString(), null);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<Representante> consultaAtivos() {
		Representante filter = new Representante();
		filter.setStatus(DomStatusRepresentante.ATIVO);

		return consulta(filter);
	}

	public List<Representante> consulta(Representante entity) {
		return repository.consulta(entity);
	}

	public Representante consultaPorId(Integer id) {
		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, id.toString());

		if (null == cacheObj) {
			Representante entity = repository.findById(id);

			if (null == entity) {
				throw new ResultadoNaoEncontradoException();
			}

			preencheRelacionados(entity);

			// Atualiza o cache
			cacheUtil.putOnCache(CACHE_NAME, id.toString(), ObjectCopier.copy(entity));

			cacheObj = cacheUtil.findOnCache(CACHE_NAME, id.toString());
		}

		return (Representante) ObjectCopier.copy(cacheObj);
	}

	// Util
	private void validaDados(Representante entity) {

	}

	private String registraAuditoria(Integer id, Representante objeto, String codigoEvento, String observacao) {
		RepresentanteAuditoria evento = new RepresentanteAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (Representante) ObjectCopier.copy(objeto);
			// Remove as referencias recursivas
			for (RepresentanteEndereco endereco : objeto.getEnderecos()) {
				endereco.setRepresentante(null);
			}
			if (null != objeto.getContatos()) {
				for (RepresentanteContato contato : objeto.getContatos()) {
					contato.setRepresentante(null);
				}
			}

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}

	private void preencheRelacionados(Representante entity) {
		entity.setContatos(contatoRepository.consultaPorRepresentante(entity.getId()));
		entity.setEnderecos(enderecoRepository.consultaPorRepresentante(entity.getId()));
	}
}