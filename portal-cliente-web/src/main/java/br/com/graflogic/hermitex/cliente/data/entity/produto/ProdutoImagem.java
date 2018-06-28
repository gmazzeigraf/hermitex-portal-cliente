package br.com.graflogic.hermitex.cliente.data.entity.produto;

import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_produto_imagem")
public class ProdutoImagem implements Serializable {

	private static final long serialVersionUID = 6854847839601418792L;

	@Id
	@Column(name = "id", nullable = false)
	private String id;

	@Column(name = "id_produto", nullable = false)
	private Integer idProduto;

	@Column(name = "in_capa", nullable = false)
	private String capa;

	@Column(name = "conteudo", nullable = false)
	private String conteudo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_produto", referencedColumnName = "id", insertable = false, updatable = false)
	private Produto produto;

	@Transient
	private boolean temporaria;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(Integer idProduto) {
		this.idProduto = idProduto;
	}

	public boolean isCapa() {
		return capa.equals(DomBoolean.SIM);
	}

	public void setCapa(boolean capa) {
		this.capa = capa ? DomBoolean.SIM : DomBoolean.NAO;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public boolean isTemporaria() {
		return temporaria;
	}

	public void setTemporaria(boolean temporaria) {
		this.temporaria = temporaria;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProdutoImagem other = (ProdutoImagem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}