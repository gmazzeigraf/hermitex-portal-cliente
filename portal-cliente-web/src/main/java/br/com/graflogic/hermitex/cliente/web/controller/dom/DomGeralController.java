package br.com.graflogic.hermitex.cliente.web.controller.dom;

import java.io.Serializable;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.hermitex.cliente.data.dom.DomGeral;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class DomGeralController implements Serializable {

	private static final long serialVersionUID = 1222013759019178861L;

	public Map<String, Object> getBooleanMap() {
		return DomGeral.domBoolean.getMap();
	}

	public Map<String, Object> getStatusSimpleMap() {
		return DomGeral.domStatusSimple.getMap();
	}
}