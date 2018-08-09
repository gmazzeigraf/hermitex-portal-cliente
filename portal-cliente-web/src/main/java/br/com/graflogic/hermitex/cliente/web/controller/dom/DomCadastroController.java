package br.com.graflogic.hermitex.cliente.web.controller.dom;

import java.io.Serializable;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class DomCadastroController implements Serializable {

	private static final long serialVersionUID = 2764220491026789880L;

	public Map<String, Object> getStatusClienteMap() {
		return DomCadastro.domStatusCliente.getMap();
	}

	public Map<String, Object> getStatusFilialMap() {
		return DomCadastro.domStatusFilial.getMap();
	}

	public Map<String, Object> getStatusRepresentanteMap() {
		return DomCadastro.domStatusRepresentante.getMap();
	}

	public Map<String, Object> getTipoFilialMap() {
		return DomCadastro.domTipoFilial.getMap();
	}
}