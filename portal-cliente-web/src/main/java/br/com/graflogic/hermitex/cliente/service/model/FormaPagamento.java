package br.com.graflogic.hermitex.cliente.service.model;

import java.math.BigDecimal;

import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomTipoPagamento;

/**
 * 
 * @author gmazz
 *
 */
public class FormaPagamento {

	public FormaPagamento(String tipoPagamento) {
		this.tipoPagamento = tipoPagamento;

		if (DomTipoPagamento.BOLETO.equals(tipoPagamento)) {
			setParcelas(1);

		} else if (DomTipoPagamento.CARTAO_CREDITO_1.equals(tipoPagamento)) {
			setParcelas(1);

		} else if (DomTipoPagamento.CARTAO_CREDITO_2.equals(tipoPagamento)) {
			setParcelas(2);
		}
	}

	private String tipoPagamento;

	private Integer parcelas;

	private BigDecimal valor;

	public String getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(String tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public Integer getParcelas() {
		return parcelas;
	}

	public void setParcelas(Integer parcelas) {
		this.parcelas = parcelas;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return DomPedido.domTipoPagamento.getDeValor(tipoPagamento) + " R$ " + valor.toString().replace(".", ",");
	}
}