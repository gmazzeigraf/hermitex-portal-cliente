package br.com.graflogic.hermitex.cliente.web.controller.produto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @author gmazz
 *
 */
public class ProdutoPedido implements Serializable {

	private static final long serialVersionUID = 2240256706780629741L;

	private Integer id;

	private String codigoTamanho;

	private Integer quantidade;

	private BigDecimal valorUnitario;

	private BigDecimal valorCorrigidoTamanho;

	private BigDecimal valorTotal;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigoTamanho() {
		return codigoTamanho;
	}

	public void setCodigoTamanho(String codigoTamanho) {
		this.codigoTamanho = codigoTamanho;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getValorCorrigidoTamanho() {
		return valorCorrigidoTamanho;
	}

	public void setValorCorrigidoTamanho(BigDecimal valorCorrigidoTamanho) {
		this.valorCorrigidoTamanho = valorCorrigidoTamanho;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
}