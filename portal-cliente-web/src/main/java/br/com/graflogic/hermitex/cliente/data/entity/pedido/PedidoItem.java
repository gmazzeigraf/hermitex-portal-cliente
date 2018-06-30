package br.com.graflogic.hermitex.cliente.data.entity.pedido;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_pedido_item")
public class PedidoItem implements Serializable {

	private static final long serialVersionUID = 2240256706780629741L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_PEDIDO_ITEM")
	@SequenceGenerator(name = "SQ_PEDIDO_ITEM", sequenceName = "SQ_PEDIDO_ITEM", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "id_pedido", nullable = false)
	private Long idPedido;

	@Column(name = "id_produto", nullable = false)
	private Integer idProduto;

	@Column(name = "cd_tamanho", nullable = false)
	private String codigoTamanho;

	@Column(name = "quantidade", nullable = false)
	private Integer quantidade;

	@Column(name = "vl_unitario", nullable = false)
	private BigDecimal valorUnitario;

	@Column(name = "vl_corrigido_tamanho", nullable = false)
	private BigDecimal valorCorrigidoTamanho;

	@Column(name = "vl_total", nullable = false)
	private BigDecimal valorTotal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pedido", referencedColumnName = "id", insertable = false, updatable = false)
	private Pedido pedido;

	@Transient
	private String codigoProduto;

	@Transient
	private String tituloProduto;

	@Transient
	private String idImagemCapaProduto;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Long idPedido) {
		this.idPedido = idPedido;
	}

	public Integer getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(Integer idProduto) {
		this.idProduto = idProduto;
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

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public String getCodigoProduto() {
		return codigoProduto;
	}

	public void setCodigoProduto(String codigoProduto) {
		this.codigoProduto = codigoProduto;
	}

	public String getTituloProduto() {
		return tituloProduto;
	}

	public void setTituloProduto(String tituloProduto) {
		this.tituloProduto = tituloProduto;
	}

	public String getIdImagemCapaProduto() {
		return idImagemCapaProduto;
	}

	public void setIdImagemCapaProduto(String idImagemCapaProduto) {
		this.idImagemCapaProduto = idImagemCapaProduto;
	}
}