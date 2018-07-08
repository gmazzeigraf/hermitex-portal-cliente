package br.com.graflogic.hermitex.cliente.data.entity.pedido;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoEndereco;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_pedido")
public class Pedido implements Serializable {

	private static final long serialVersionUID = 5500007211642134415L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_PEDIDO")
	@SequenceGenerator(name = "SQ_PEDIDO", sequenceName = "SQ_PEDIDO", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

	@Column(name = "id_filial")
	private Integer idFilial;

	@Column(name = "vl_produtos", nullable = false)
	private BigDecimal valorProdutos;

	@Column(name = "vl_frete", nullable = false)
	private BigDecimal valorFrete;

	@Column(name = "vl_total", nullable = false)
	private BigDecimal valorTotal;

	@Column(name = "cd_forma_pagamento", nullable = false)
	private String codigoFormaPagamento;

	@Column(name = "ds_forma_pagamento", nullable = false)
	private String formaPagamento;

	@Column(name = "id_pagamento")
	private String idPagamento;

	@Column(name = "id_janela_compra", nullable = false)
	private Integer idJanelaCompra;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pedido", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<PedidoItem> itens;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pedido", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<PedidoEndereco> enderecos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public Integer getIdFilial() {
		return idFilial;
	}

	public void setIdFilial(Integer idFilial) {
		this.idFilial = idFilial;
	}

	public BigDecimal getValorProdutos() {
		return valorProdutos;
	}

	public void setValorProdutos(BigDecimal valorProdutos) {
		this.valorProdutos = valorProdutos;
	}

	public BigDecimal getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getCodigoFormaPagamento() {
		return codigoFormaPagamento;
	}

	public void setCodigoFormaPagamento(String codigoFormaPagamento) {
		this.codigoFormaPagamento = codigoFormaPagamento;
	}

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public String getIdPagamento() {
		return idPagamento;
	}

	public void setIdPagamento(String idPagamento) {
		this.idPagamento = idPagamento;
	}

	public Integer getIdJanelaCompra() {
		return idJanelaCompra;
	}

	public void setIdJanelaCompra(Integer idJanelaCompra) {
		this.idJanelaCompra = idJanelaCompra;
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

	public List<PedidoItem> getItens() {
		return itens;
	}

	public void setItens(List<PedidoItem> itens) {
		this.itens = itens;
	}

	public List<PedidoEndereco> getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(List<PedidoEndereco> enderecos) {
		this.enderecos = enderecos;
	}

	public String getDeStatus() {
		return DomPedido.domStatus.getDeValor(status);
	}

	public PedidoEndereco getEnderecoFaturamento() {
		return enderecos.get(enderecos.indexOf(new PedidoEndereco(new PedidoEnderecoPK(id, DomTipoEndereco.FATURAMENTO))));
	}

	public PedidoEndereco getEnderecoEntrega() {
		return enderecos.get(enderecos.indexOf(new PedidoEndereco(new PedidoEnderecoPK(id, DomTipoEndereco.ENTREGA))));
	}
}