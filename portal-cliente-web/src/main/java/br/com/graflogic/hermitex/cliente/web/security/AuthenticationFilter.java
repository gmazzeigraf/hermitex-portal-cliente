package br.com.graflogic.hermitex.cliente.web.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.TextEscapeUtils;

import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomStatusSenhaUsuario;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomStatusCliente;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomStatusFilial;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomStatusRepresentante;
import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.Usuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioAdministrador;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioCliente;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioFilial;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioRepresentante;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Representante;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.UsuarioService;
import br.com.graflogic.hermitex.cliente.service.impl.aud.AuditoriaService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.RepresentanteService;

/**
 * 
 * @author gmazz
 *
 */
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private static final String SPRING_SECURITY_FORM_USERNAME_KEY = "j_username";
	private static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "j_password";

	public static final String SPRING_SECURITY_LAST_USERNAME_KEY = "SPRING_SECURITY_LAST_USERNAME";

	private boolean postOnly = true;

	private static Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private AuditoriaService auditoriaService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private RepresentanteService representanteService;

	public AuthenticationFilter() {
		super("/j_spring_security_check");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {
			if (postOnly && !request.getMethod().equals("POST")) {
				throw new AuthenticationServiceException("");
			}

			String email = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
			String senha = request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);

			String cabecalhoIpOrigem = request.getHeader("X-Forwarded-For");

			String ipOrigem = null;
			if (StringUtils.isNotEmpty(cabecalhoIpOrigem)) {
				ipOrigem = cabecalhoIpOrigem;

			} else {
				ipOrigem = request.getRemoteAddr();

			}

			if (StringUtils.isEmpty(email)) {
				throw new AuthenticationServiceException("Favor informar o e-mail");
			}

			if (StringUtils.isEmpty(senha)) {
				throw new AuthenticationServiceException("Favor informar a senha");
			}

			UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(email, senha);

			HttpSession session = request.getSession(false);

			if (session != null || getAllowSessionCreation()) {
				request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextEscapeUtils.escapeEntities(email));
			}

			autenticacao.setDetails(authenticationDetailsSource.buildDetails(request));

			// Autentica o usuario
			autenticacao = (UsernamePasswordAuthenticationToken) this.getAuthenticationManager().authenticate(autenticacao);

			// Consulta o usuario	
			Usuario usuario = usuarioService.consultaPorEmail(email);

			Object empresa = null;
			if (usuario instanceof UsuarioCliente) {
				empresa = clienteService.consultaPorId(((UsuarioCliente) usuario).getIdCliente());

				if (!DomStatusCliente.ATIVO.equals(((Cliente) empresa).getStatus())) {
					throw new AuthenticationServiceException("Cliente inativo, contate o administrador");
				}

			} else if (usuario instanceof UsuarioFilial) {
				empresa = filialService.consultaPorId(((UsuarioFilial) usuario).getIdFilial());

				((UsuarioFilial) usuario).setIdCliente(((Filial) empresa).getIdCliente());

				if (!DomStatusFilial.ATIVO.equals(((Filial) empresa).getStatus())) {
					throw new AuthenticationServiceException("Filial inativa, contate o administrador");
				}

				if (DomBoolean.SIM.equals(((Filial) empresa).getCompraBloqueada())) {
					throw new AuthenticationServiceException("Compras indisponíveis, contate o administrador");
				}

			} else if (usuario instanceof UsuarioRepresentante) {
				empresa = representanteService.consultaPorId(((UsuarioRepresentante) usuario).getIdRepresentante());

				if (!DomStatusRepresentante.ATIVO.equals(((Representante) empresa).getStatus())) {
					throw new AuthenticationServiceException("Representante inativo, contate o administrador");
				}
			}

			// Prepara o objeto autenticado
			UserInfo principal = new UserInfo(email, "", autenticacao.getAuthorities(), usuario, empresa);

			List<GrantedAuthority> authorities = new ArrayList<>();

			if (DomStatusSenhaUsuario.DEFINITIVA.equals(usuario.getStatusSenha())) {
				// Caso seja a tenha definita, adiciona as permissoes
				authorities.addAll(autenticacao.getAuthorities());
			}

			if (usuario instanceof UsuarioAdministrador) {
				authorities.add(new SimpleGrantedAuthority(UsuarioAdministrador.PERMISSAO));

			} else if (usuario instanceof UsuarioCliente) {
				authorities.add(new SimpleGrantedAuthority(UsuarioCliente.PERMISSAO));

			} else if (usuario instanceof UsuarioFilial) {
				authorities.add(new SimpleGrantedAuthority(UsuarioFilial.PERMISSAO));

			}

			autenticacao = new UsernamePasswordAuthenticationToken(principal, autenticacao.getCredentials(), authorities);

			String idLogin = auditoriaService.registraLogin(usuario.getId(), ipOrigem);
			principal.setIdLogin(idLogin);
			principal.setIpOrigem(ipOrigem);

			// Remove o cookie do menu selecionado
			if (null != request.getCookies()) {
				for (Cookie cookie : request.getCookies()) {
					if ("poseidon_expandeditems".equals(cookie.getName())) {
						cookie.setValue("");
						response.addCookie(cookie);
					}
				}
			}

			return autenticacao;

		} catch (BadCredentialsException e) {
			throw new AuthenticationServiceException("E-mail e/ou senha incorreto(s)");

		} catch (DisabledException e) {
			throw new AuthenticationServiceException("Usuário com acesso desabilitado");

		} catch (AuthenticationServiceException e) {
			throw e;

		} catch (Throwable t) {
			LOGGER.error("Erro ao autenticar usuario", t);
			throw new AuthenticationServiceException("Erro ao realizar a autenticação, contate o administrador");
		}
	}
}