package br.com.graflogic.hermitex.cliente.data.entity.cotacao;

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
@Table(name = "tb_cotacao_item")
public class CotacaoItem implements Serializable {

	private static final long serialVersionUID = -7125726839836185212L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_COTACAO_ITEM")
	@SequenceGenerator(name = "SQ_COTACAO_ITEM", sequenceName = "SQ_COTACAO_ITEM", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "id_cotacao", nullable = false)
	private Long idCotacao;

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

	@Column(name = "peso_total", nullable = false)
	private BigDecimal pesoTotal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cotacao", referencedColumnName = "id", insertable = false, updatable = false)
	private Cotacao cotacao;

	// Apresentacao
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

	public Long getIdCotacao() {
		return idCotacao;
	}

	public void setIdCotacao(Long idCotacao) {
		this.idCotacao = idCotacao;
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

	public BigDecimal getPesoTotal() {
		return pesoTotal;
	}

	public void setPesoTotal(BigDecimal pesoTotal) {
		this.pesoTotal = pesoTotal;
	}

	public Cotacao getCotacao() {
		return cotacao;
	}

	public void setCotacao(Cotacao cotacao) {
		this.cotacao = cotacao;
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