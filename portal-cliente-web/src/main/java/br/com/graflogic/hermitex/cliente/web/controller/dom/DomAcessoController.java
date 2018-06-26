package br.com.graflogic.hermitex.cliente.web.controller.dom;

import java.io.Serializable;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class DomAcessoController implements Serializable {

	private static final long serialVersionUID = 1475209242916622018L;

	public Map<String, Object> getStatusUsuarioMap() {
		return DomAcesso.domStatusUsuario.getMap();
	}

	public Map<String, Object> getStatusPerfilUsuarioMap() {
		return DomAcesso.domStatusPerfilUsuario.getMap();
	}
}