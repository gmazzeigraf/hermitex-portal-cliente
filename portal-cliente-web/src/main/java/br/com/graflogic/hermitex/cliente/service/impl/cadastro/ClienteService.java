package br.com.graflogic.hermitex.cliente.service.impl.cadastro;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.base.service.util.CacheUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaCliente;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomStatusCliente;
import br.com.graflogic.hermitex.cliente.data.entity.aud.ClienteAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteContato;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteLogotipo;
import br.com.graflogic.hermitex.cliente.data.impl.aud.ClienteAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cadastro.ClienteContatoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cadastro.ClienteEnderecoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cadastro.ClienteRepository;
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
public class ClienteService {

	private static final String CACHE_NAME = "clientes";

	@Autowired
	private ClienteRepository repository;

	@Autowired
	private ClienteAuditoriaRepository auditoriaRepository;

	@Autowired
	private ClienteEnderecoRepository enderecoRepository;

	@Autowired
	private ClienteContatoRepository contatoRepository;

	@Autowired
	private CacheUtil cacheUtil;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Cliente entity) {
		validaDados(entity);

		entity.setStatus(DomStatusCliente.ATIVO);

		List<ClienteContato> contatos = entity.getContatos();
		entity.setContatos(null);

		entity.getLogotipo().setCliente(entity);

		try {
			try {
				repository.consultaPorCnpj(entity.getCnpj());

				throw new DadosInvalidosException("CNPJ já cadastrado");

			} catch (NoResultException e) {
			}

			repository.store(entity);

			for (ClienteContato contato : contatos) {
				contato.setIdCliente(entity.getId());
			}

			entity.setContatos(contatos);

			repository.update(entity);

			registraAuditoria(entity.getId(), entity, DomEventoAuditoriaCliente.CADASTRO, null);

		} catch (Throwable t) {
			entity.setContatos(contatos);

			throw t;
		}
	}

	@Transactional(rollbackFor = Throwable.class)
	public void atualiza(Cliente entity) {
		validaDados(entity);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaCliente.ATUALIZACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void inativa(Cliente entity) {
		entity.setStatus(DomStatusCliente.INATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaCliente.INATIVACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void ativa(Cliente entity) {
		entity.setStatus(DomStatusCliente.ATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaCliente.ATIVACAO, null);
	}

	private void executaAtualiza(Cliente entity) {
		try {
			repository.update(entity);

			cacheUtil.putOnCache(CACHE_NAME, entity.getId().toString(), null);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<Cliente> consultaAtivos() {
		Cliente filter = new Cliente();
		filter.setStatus(DomStatusCliente.ATIVO);

		return consulta(filter);
	}

	public List<Cliente> consulta(Cliente entity) {
		return repository.consulta(entity);
	}

	public List<Cliente> consultaPorRepresentante(Integer idRepresentante) {
		Cliente filter = new Cliente();
		filter.setIdRepresentante(idRepresentante);

		return consulta(filter);
	}

	public Cliente consultaPorId(Integer id) {
		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, id.toString());

		if (null == cacheObj) {
			Cliente entity = repository.findById(id);

			if (null == entity) {
				throw new ResultadoNaoEncontradoException();
			}

			preencheRelacionados(entity);

			// Atualiza o cache
			cacheUtil.putOnCache(CACHE_NAME, id.toString(), ObjectCopier.copy(entity));

			cacheObj = cacheUtil.findOnCache(CACHE_NAME, id.toString());
		}

		return (Cliente) ObjectCopier.copy(cacheObj);
	}

	// Logotipo
	public byte[] downloadLogotipo(Integer idCliente) throws IOException {
		try {
			Cliente cliente = consultaPorId(idCliente);

			return downloadLogotipo(cliente.getLogotipo());

		} catch (ResultadoNaoEncontradoException e) {
			return null;
		}
	}

	public byte[] downloadLogotipo(ClienteLogotipo logotipo) {
		return Base64.decodeBase64(logotipo.getConteudo());
	}

	// Util
	private void validaDados(Cliente entity) {
		if (null != entity.getIdRepresentante() && 0 == entity.getIdRepresentante()) {
			entity.setIdRepresentante(null);
		}
	}

	private String registraAuditoria(Integer id, Cliente objeto, String codigoEvento, String observacao) {
		ClienteAuditoria evento = new ClienteAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (Cliente) ObjectCopier.copy(objeto);
			// Remove as referencias recursivas
			objeto.setLogotipo(null);
			for (ClienteEndereco endereco : objeto.getEnderecos()) {
				endereco.setCliente(null);
			}
			if (null != objeto.getContatos()) {
				for (ClienteContato contato : objeto.getContatos()) {
					contato.setCliente(null);
				}
			}

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}

	private void preencheRelacionados(Cliente entity) {
		entity.setContatos(contatoRepository.consultaPorCliente(entity.getId()));
		entity.setEnderecos(enderecoRepository.consultaPorCliente(entity.getId()));
	}
}