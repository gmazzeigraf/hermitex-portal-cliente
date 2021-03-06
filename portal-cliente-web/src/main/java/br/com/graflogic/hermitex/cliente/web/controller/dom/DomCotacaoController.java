package br.com.graflogic.hermitex.cliente.web.controller.dom;

import java.io.Serializable;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.hermitex.cliente.data.dom.DomCotacao;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class DomCotacaoController implements Serializable {

	private static final long serialVersionUID = -404200920913591015L;

	public Map<String, Object> getStatusMap() {
		return DomCotacao.domStatus.getMap();
	}

	public Map<String, Object> getTipoFormaPagamentoMap() {
		return DomCotacao.domTipoFormaPagamento.getMap();
	}

	public Map<String, Object> getPedidoFaturadoMap() {
		return DomCotacao.domPedidoFaturado.getMap();
	}
}