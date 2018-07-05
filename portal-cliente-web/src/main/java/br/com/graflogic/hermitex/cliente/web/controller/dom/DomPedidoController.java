package br.com.graflogic.hermitex.cliente.web.controller.dom;

import java.io.Serializable;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class DomPedidoController implements Serializable {

	private static final long serialVersionUID = -404200920913591015L;

	public Map<String, Object> getStatusMap() {
		return DomPedido.domStatus.getMap();
	}

	public Map<String, Object> getTipoPagamentoMap() {
		return DomPedido.domTipoPagamento.getMap();
	}

	public Map<String, Object> getBandeiraCartaoCreditoMap() {
		return DomPedido.domBandeiraCartaoCredito.getMap();
	}
}