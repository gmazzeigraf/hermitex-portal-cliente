package br.com.graflogic.hermitex.cliente.data.entity.produto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author gmazz
 *
 */
@Embeddable
public class ProdutoCorPK implements Serializable {

	private static final long serialVersionUID = -1853951883521040324L;

	@Column(name = "id_produto", nullable = false)
	private Integer idProduto;

	@Column(name = "cd_cor", nullable = false)
	private String codigoCor;

	public Integer getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(Integer idProduto) {
		this.idProduto = idProduto;
	}

	public String getCodigoCor() {
		return codigoCor;
	}

	public void setCodigoCor(String codigoCor) {
		this.codigoCor = codigoCor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigoCor == null) ? 0 : codigoCor.hashCode());
		result = prime * result + ((idProduto == null) ? 0 : idProduto.hashCode());
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
		ProdutoCorPK other = (ProdutoCorPK) obj;
		if (codigoCor == null) {
			if (other.codigoCor != null)
				return false;
		} else if (!codigoCor.equals(other.codigoCor))
			return false;
		if (idProduto == null) {
			if (other.idProduto != null)
				return false;
		} else if (!idProduto.equals(other.idProduto))
			return false;
		return true;
	}
}