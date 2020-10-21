package br.com.graflogic.hermitex.cliente.data.entity.produto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatusFormaPagamento;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomTipoFormaPagamento;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_forma_pagamento")
public class FormaPagamento implements Serializable {

	private static final long serialVersionUID = -8323342298741147500L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_FORMA_PAGAMENTO")
	@SequenceGenerator(name = "SQ_FORMA_PAGAMENTO", sequenceName = "SQ_FORMA_PAGAMENTO", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

	@Column(name = "tipo", nullable = false)
	private String tipo;

	@Column(name = "qt_parcelas", nullable = false)
	private Integer quantidadeParcelas;

	@Column(name = "configuracao", nullable = false)
	private String configuracao;

	@Column(name = "pc_desconto", nullable = false)
	private BigDecimal porcentagemDesconto;

	@Column(name = "vl_pedido_minimo", nullable = false)
	private BigDecimal valorPedidoMinimo;

	@Column(name = "in_credito_obrigatorio", nullable = false)
	private String creditoObrigatorio;

	@Column(name = "in_desconto_especial", nullable = false)
	private String descontoEspecial;

	@Column(name = "in_loja_propria", nullable = false)
	private String lojaPropria;

	@Column(name = "in_franquia", nullable = false)
	private String franquia;

	@Column(name = "in_filial", nullable = false)
	private String filial;

	@Column(name = "in_unidade", nullable = false)
	private String unidade;

	@Column(name = "in_cliente", nullable = false)
	private String cliente;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	@Transient
	private String descricao;

	@Transient
	private BigDecimal valorDesconto;

	@Transient
	private BigDecimal valorParcela;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getQuantidadeParcelas() {
		return quantidadeParcelas;
	}

	public void setQuantidadeParcelas(Integer quantidadeParcelas) {
		this.quantidadeParcelas = quantidadeParcelas;
	}

	public String getConfiguracao() {
		return configuracao;
	}

	public void setConfiguracao(String configuracao) {
		this.configuracao = configuracao;
	}

	public BigDecimal getPorcentagemDesconto() {
		return porcentagemDesconto;
	}

	public void setPorcentagemDesconto(BigDecimal porcentagemDesconto) {
		this.porcentagemDesconto = porcentagemDesconto;
	}

	public BigDecimal getValorPedidoMinimo() {
		return valorPedidoMinimo;
	}

	public void setValorPedidoMinimo(BigDecimal valorPedidoMinimo) {
		this.valorPedidoMinimo = valorPedidoMinimo;
	}

	public String getCreditoObrigatorio() {
		return creditoObrigatorio;
	}

	public void setCreditoObrigatorio(String creditoObrigatorio) {
		this.creditoObrigatorio = creditoObrigatorio;
	}

	public String getDescontoEspecial() {
		return descontoEspecial;
	}

	public void setDescontoEspecial(String descontoEspecial) {
		this.descontoEspecial = descontoEspecial;
	}

	public String getLojaPropria() {
		return lojaPropria;
	}

	public void setLojaPropria(String lojaPropria) {
		this.lojaPropria = lojaPropria;
	}

	public String getFranquia() {
		return franquia;
	}

	public void setFranquia(String franquia) {
		this.franquia = franquia;
	}

	public String getFilial() {
		return filial;
	}

	public void setFilial(String filial) {
		this.filial = filial;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getVersao() {
		return versao;
	}

	public void setVersao(Long versao) {
		this.versao = versao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(BigDecimal valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	public boolean isAtiva() {
		return null != status && DomStatusFormaPagamento.ATIVA.equals(status);
	}

	public boolean isInativa() {
		if (DomStatusFormaPagamento.INATIVA.equals(status)) {
			return true;
		}
		return false;
	}

	public String getDeTipo() {
		return DomPedido.domTipoFormaPagamento.getDeValor(tipo);
	}

	public String getDeStatus() {
		return DomPedido.domStatusFormaPagamento.getDeValor(status);
	}

	public boolean isCartaoCredito() {
		return null != tipo && DomTipoFormaPagamento.CARTAO_CREDITO.equals(tipo);
	}

	public boolean isBoleto() {
		return null != tipo && DomTipoFormaPagamento.BOLETO.equals(tipo);
	}

	public boolean isFaturamento() {
		return null != tipo && DomTipoFormaPagamento.FATURAMENTO.equals(tipo);
	}
}