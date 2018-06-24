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
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaCliente;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomStatusCliente;
import br.com.graflogic.hermitex.cliente.data.entity.aud.ClienteAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteContato;
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
 * @author ggraf
 *
 */
@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repository;

	@Autowired
	private ClienteAuditoriaRepository auditoriaRepository;

	@Autowired
	private ClienteEnderecoRepository enderecoRepository;

	@Autowired
	private ClienteContatoRepository contatoRepository;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Cliente entity) {
		entity.setStatus(DomStatusCliente.ATIVO);

		try {
			consultaPorCnpj(entity.getCnpj());

			throw new DadosInvalidosException("CNPJ já cadastrado");

		} catch (ResultadoNaoEncontradoException e) {
		}

		List<ClienteContato> contatos = entity.getContatos();
		entity.setContatos(null);

		entity.getEndereco().setCliente(entity);

		repository.store(entity);

		if (null != contatos && !contatos.isEmpty()) {
			for (ClienteContato contato : contatos) {
				contato.setIdCliente(entity.getId());
			}

			entity.setContatos(contatos);
			repository.update(entity);
		}

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaCliente.CADASTRO, null);
	}

	public void atualiza(Cliente entity) {
		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaCliente.ATUALIZACAO, null);
	}

	public void inativa(Cliente entity) {
		entity.setStatus(DomStatusCliente.INATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaCliente.INATIVACAO, null);
	}

	public void ativa(Cliente entity) {
		entity.setStatus(DomStatusCliente.ATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaCliente.ATIVACAO, null);
	}

	private void executaAtualiza(Cliente entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<Cliente> consulta(Cliente entity) {
		return repository.consulta(entity);
	}

	public Cliente consultaPorCnpj(String cnpj) {
		try {
			Cliente entity = repository.consultaPorCnpj(cnpj);

			preencheRelacionados(entity);

			return entity;

		} catch (NoResultException e) {
			throw new ResultadoNaoEncontradoException();
		}
	}

	public Cliente consultaPorId(Integer id) {
		Cliente entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		preencheRelacionados(entity);

		return entity;
	}

	// Util
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
			objeto.getEndereco().setCliente(null);
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
		entity.setContatos(contatoRepository.consultaContatos(entity.getId()));
		entity.setEndereco(enderecoRepository.findById(entity.getId()));
	}
}