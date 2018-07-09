package br.com.graflogic.hermitex.cliente.service.impl.acesso;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomStatusSenhaUsuario;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomStatusUsuario;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaUsuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.Usuario;
import br.com.graflogic.hermitex.cliente.data.entity.aud.UsuarioAuditoria;
import br.com.graflogic.hermitex.cliente.data.impl.acesso.UsuarioRepository;
import br.com.graflogic.hermitex.cliente.data.impl.aud.UsuarioAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.util.ConfiguracaoEnum;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.ConfiguracaoService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.NotificacaoService;
import br.com.graflogic.hermitex.cliente.service.util.EncryptHelper;
import br.com.graflogic.hermitex.cliente.service.util.Generator;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;

	@Autowired
	private UsuarioAuditoriaRepository auditoriaRepository;

	@Autowired
	private ConfiguracaoService configuracaoService;

	@Autowired
	private NotificacaoService notificacaoService;

	// Fluxo
	public void cadastra(Usuario entity) {
		String senha = Generator.geraSenhaUsuario();

		entity.setSenha(EncryptHelper.encrypt(senha));

		entity.setStatus(DomStatusUsuario.ATIVO);
		entity.setStatusSenha(DomStatusSenhaUsuario.TEMPORARIA);

		try {
			consultaPorEmail(entity.getEmail());

			throw new DadosInvalidosException("E-mail já cadastrado");

		} catch (ResultadoNaoEncontradoException e) {
		}

		repository.store(entity);

		//		 Envia a senha para o usuario
		String tituloNotificacao = configuracaoService.consulta(ConfiguracaoEnum.USUARIO_EMAIL_SENHA_TITULO);
		String conteudoNotificacao = configuracaoService.consulta(ConfiguracaoEnum.USUARIO_EMAIL_SENHA_CONTEUDO);

		conteudoNotificacao = conteudoNotificacao.replace("#NOME#", entity.getNome()).replace("#SENHA#", senha);

		notificacaoService.enviaEmail(tituloNotificacao, conteudoNotificacao, Arrays.asList(new String[] { entity.getEmail() }), null);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaUsuario.CADASTRO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void atualiza(Usuario entity) {
		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaUsuario.ATUALIZACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void inativa(Usuario entity) {
		entity.setStatus(DomStatusUsuario.INATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaUsuario.INATIVACAO, null);
	}
	
	@Transactional(rollbackFor = Throwable.class)
	public void ativa(Usuario entity) {
		entity.setStatus(DomStatusUsuario.ATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaUsuario.ATIVACAO, null);
	}

	private void executaAtualiza(Usuario entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<Usuario> consulta(Usuario entity) {
		return repository.consulta(entity);
	}

	public List<Usuario> consultaPorPermissao(String permissao) {
		return repository.consultaPorPermissao(permissao);
	}

	public Usuario consultaPorId(Integer id) {
		Usuario entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	public Usuario consultaPorEmail(String email) {
		try {
			return repository.consultaPorEmail(email);

		} catch (NoResultException e) {
			throw new ResultadoNaoEncontradoException();
		}
	}

	// Util
	public void geraNovaSenha(Usuario entity) {
		String senha = Generator.geraSenhaUsuario();

		entity.setSenha(EncryptHelper.encrypt(senha));
		entity.setStatusSenha(DomStatusSenhaUsuario.TEMPORARIA);

		executaAtualiza(entity);

		//  Envia a nova senha para o usuario
		String tituloNotificacao = configuracaoService.consulta(ConfiguracaoEnum.USUARIO_EMAIL_NOVA_SENHA_TITULO);
		String conteudoNotificacao = configuracaoService.consulta(ConfiguracaoEnum.USUARIO_EMAIL_NOVA_SENHA_CONTEUDO);

		conteudoNotificacao = conteudoNotificacao.replace("#NOME#", entity.getNome()).replace("#SENHA#", senha);

		notificacaoService.enviaEmail(tituloNotificacao, conteudoNotificacao, Arrays.asList(new String[] { entity.getEmail() }), null);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaUsuario.GERACAO_NOVA_SENHA, null);
	}

	public void alteraSenha(Integer id, String senhaAtual, String novaSenha) {
		Usuario entity = consultaPorId(id);

		String novaSenhaCriptografada = EncryptHelper.encrypt(novaSenha);

		if (!EncryptHelper.encrypt(senhaAtual).equals(entity.getSenha())) {
			throw new DadosInvalidosException("Senha atual inválida");
		}

		if (entity.getSenha().equals(novaSenhaCriptografada)) {
			throw new DadosInvalidosException("A nova senha deve ser diferente da atual");
		}

		entity.setSenha(novaSenhaCriptografada);
		entity.setStatusSenha(DomStatusSenhaUsuario.DEFINITIVA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaUsuario.ALTERACAO_SENHA, null);
	}

	private String registraAuditoria(Integer id, Usuario objeto, String codigoEvento, String observacao) {
		UsuarioAuditoria evento = new UsuarioAuditoria();
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