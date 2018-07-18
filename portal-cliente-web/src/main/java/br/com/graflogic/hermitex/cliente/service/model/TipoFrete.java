package br.com.graflogic.hermitex.cliente.service.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoFrete;

/**
 * 
 * @author gmazz
 *
 */
public class TipoFrete {

	private String codigoServico;

	private BigDecimal valor;

	private List<PedidoFrete> fretes;

	public String getCodigoServico() {
		return codigoServico;
	}

	public void setCodigoServico(String codigoServico) {
		this.codigoServico = codigoServico;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public List<PedidoFrete> getFretes() {
		return fretes;
	}

	public void setFretes(List<PedidoFrete> fretes) {
		this.fretes = fretes;
	}

	public String getDeCodigoServico() {
		return DomPedido.domServicoFrete.getDeValor(codigoServico);
	}

	public String getDescricao() {
		return getDeCodigoServico() + " - " + new DecimalFormat("R$ #,##0.00").format(valor);
	}
}