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
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaFilial;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomStatusFilial;
import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;
import br.com.graflogic.hermitex.cliente.data.entity.aud.FilialAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.FilialContato;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.FilialEndereco;
import br.com.graflogic.hermitex.cliente.data.impl.aud.FilialAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cadastro.FilialContatoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cadastro.FilialEnderecoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cadastro.FilialRepository;
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
public class FilialService {

	private static final String CACHE_NAME = "filiais";

	@Autowired
	private FilialRepository repository;

	@Autowired
	private FilialAuditoriaRepository auditoriaRepository;

	@Autowired
	private FilialEnderecoRepository enderecoRepository;

	@Autowired
	private FilialContatoRepository contatoRepository;

	@Autowired
	private PerfilUsuarioService perfilUsuarioService;

	@Autowired
	private CacheUtil cacheUtil;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Filial entity) {
		entity.setStatus(DomStatusFilial.ATIVO);

		List<FilialContato> contatos = entity.getContatos();
		entity.setContatos(null);

		try {
			try {
				repository.consultaPorCnpj(entity.getCnpj());

				throw new DadosInvalidosException("CNPJ já cadastrado");

			} catch (NoResultException e) {
			}

			repository.store(entity);

			for (FilialContato contato : contatos) {
				contato.setIdFilial(entity.getId());
			}

			entity.setContatos(contatos);

			repository.update(entity);

			registraAuditoria(entity.getId(), entity, DomEventoAuditoriaFilial.CADASTRO, null);

			// Cadastra o perfil de usuario padrao
			perfilUsuarioService.cadastraPadrao(DomTipoUsuario.FILIAL, entity.getId());

		} catch (Throwable t) {
			entity.setContatos(contatos);

			throw t;
		}
	}

	@Transactional(rollbackFor = Throwable.class)
	public void atualiza(Filial entity) {
		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaFilial.ATUALIZACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void inativa(Filial entity) {
		entity.setStatus(DomStatusFilial.INATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaFilial.INATIVACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void ativa(Filial entity) {
		entity.setStatus(DomStatusFilial.ATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaFilial.ATIVACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void bloqueiaCompra(Filial entity) {
		entity.setCompraBloqueada(DomBoolean.SIM);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaFilial.BLOQUEIO_COMPRA, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void desbloqueiaCompra(Filial entity) {
		entity.setCompraBloqueada(DomBoolean.NAO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaFilial.DESBLOQUEIO_COMPRA, null);
	}

	private void executaAtualiza(Filial entity) {
		try {
			repository.update(entity);

			cacheUtil.putOnCache(CACHE_NAME, entity.getId().toString(), null);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<Filial> consultaPorCliente(Integer idCliente, boolean somenteAtivos) {
		Filial filter = new Filial();
		filter.setIdCliente(idCliente);
		if (somenteAtivos) {
			filter.setStatus(DomStatusFilial.ATIVO);
		}

		return consulta(filter);
	}

	public List<Filial> consulta(Filial entity) {
		return repository.consulta(entity);
	}

	@Transactional(readOnly = true)
	public Filial consultaPorId(Integer id) {
		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, id.toString());

		if (null == cacheObj) {
			Filial entity = repository.findById(id);

			if (null == entity) {
				throw new ResultadoNaoEncontradoException();
			}

			preencheRelacionados(entity);

			// Atualiza o cache
			cacheUtil.putOnCache(CACHE_NAME, id.toString(), ObjectCopier.copy(entity));

			cacheObj = cacheUtil.findOnCache(CACHE_NAME, id.toString());
		}

		return (Filial) ObjectCopier.copy(cacheObj);
	}

	// Validacao
	public boolean isCompraBloqueada(Integer id) {
		Filial entity = consultaPorId(id);

		return DomBoolean.SIM.equals(entity.getCompraBloqueada());
	}

	// Util
	private String registraAuditoria(Integer id, Filial objeto, String codigoEvento, String observacao) {
		FilialAuditoria evento = new FilialAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (Filial) ObjectCopier.copy(objeto);
			// Remove as referencias recursivas
			for (FilialEndereco endereco : objeto.getEnderecos()) {
				endereco.setFilial(null);
			}
			if (null != objeto.getContatos()) {
				for (FilialContato contato : objeto.getContatos()) {
					contato.setFilial(null);
				}
			}

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}

	private void preencheRelacionados(Filial entity) {
		entity.setContatos(contatoRepository.consultaPorFilial(entity.getId()));
		entity.setEnderecos(enderecoRepository.consultaPorFilial(entity.getId()));
	}
}