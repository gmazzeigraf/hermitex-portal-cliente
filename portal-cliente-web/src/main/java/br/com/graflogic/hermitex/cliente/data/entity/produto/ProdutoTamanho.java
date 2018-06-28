package br.com.graflogic.hermitex.cliente.data.entity.produto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_produto_tamanho")
public class ProdutoTamanho implements Serializable {

	private static final long serialVersionUID = -8972175880419947725L;

	@EmbeddedId
	private ProdutoTamanhoPK id;

	@Column(name = "fator", nullable = false)
	private BigDecimal fator;

	@MapsId("idProduto")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_produto", referencedColumnName = "id", insertable = false, updatable = false)
	private Produto produto;

	public ProdutoTamanhoPK getId() {
		return id;
	}

	public void setId(ProdutoTamanhoPK id) {
		this.id = id;
	}

	public BigDecimal getFator() {
		return fator;
	}

	public void setFator(BigDecimal fator) {
		this.fator = fator;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}
}