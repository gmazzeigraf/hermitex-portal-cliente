package br.com.graflogic.hermitex.cliente.service.model;

import java.math.BigDecimal;

/**
 * 
 * @author gmazz
 *
 */
public class FormaPagamento {

	public FormaPagamento(String codigo, Integer parcelas) {
		this.codigo = codigo;
		this.parcelas = parcelas;
	}

	private String codigo;

	private String descricao;

	private Integer parcelas;

	private BigDecimal valor;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
}