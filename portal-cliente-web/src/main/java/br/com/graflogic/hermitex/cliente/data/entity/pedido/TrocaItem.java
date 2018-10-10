package br.com.graflogic.hermitex.cliente.data.entity.pedido;

import java.io.Serializable;

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
@Table(name = "tb_troca_item")
public class TrocaItem implements Serializable {

	private static final long serialVersionUID = -7841950610193535549L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_TROCA_ITEM")
	@SequenceGenerator(name = "SQ_TROCA_ITEM", sequenceName = "SQ_TROCA_ITEM", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "id_troca", nullable = false)
	private Long idTroca;

	@Column(name = "id_pedido_item", nullable = false)
	private Long idPedidoItem;

	@Column(name = "id_produto", nullable = false)
	private Integer idProduto;

	@Column(name = "cd_tamanho", nullable = false)
	private String codigoTamanho;

	@Column(name = "quantidade", nullable = false)
	private Integer quantidade;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_troca", referencedColumnName = "id", insertable = false, updatable = false)
	private Troca troca;

	// Apresentacao
	@Transient
	private String codigoProduto;

	@Transient
	private String tituloProduto;

	@Transient
	private String idImagemCapaProduto;

	@Transient
	private Integer quantidadePedido;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdTroca() {
		return idTroca;
	}

	public void setIdTroca(Long idTroca) {
		this.idTroca = idTroca;
	}

	public Long getIdPedidoItem() {
		return idPedidoItem;
	}

	public void setIdPedidoItem(Long idPedidoItem) {
		this.idPedidoItem = idPedidoItem;
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

	public Troca getTroca() {
		return troca;
	}

	public void setTroca(Troca troca) {
		this.troca = troca;
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

	public Integer getQuantidadePedido() {
		return quantidadePedido;
	}

	public void setQuantidadePedido(Integer quantidadePedido) {
		this.quantidadePedido = quantidadePedido;
	}
}