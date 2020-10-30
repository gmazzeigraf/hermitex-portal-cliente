package br.com.graflogic.hermitex.cliente.data.entity.produto;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_produto_cor")
public class ProdutoCor implements Serializable {

	private static final long serialVersionUID = -7765962388873061699L;

	@EmbeddedId
	private ProdutoCorPK id;

	@MapsId("idProduto")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_produto", referencedColumnName = "id", insertable = false, updatable = false)
	private Produto produto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cd_cor", referencedColumnName = "codigo", insertable = false, updatable = false)
	private CorProduto cor;

	@Transient
	private String nomeCor;

	@Transient
	private boolean possuiImagem;

	public ProdutoCorPK getId() {
		return id;
	}

	public void setId(ProdutoCorPK id) {
		this.id = id;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public CorProduto getCor() {
		return cor;
	}

	public void setCor(CorProduto cor) {
		this.cor = cor;
	}

	public String getNomeCor() {
		return nomeCor;
	}

	public void setNomeCor(String nomeCor) {
		this.nomeCor = nomeCor;
	}

	public boolean isPossuiImagem() {
		return possuiImagem;
	}

	public void setPossuiImagem(boolean possuiImagem) {
		this.possuiImagem = possuiImagem;
	}
}