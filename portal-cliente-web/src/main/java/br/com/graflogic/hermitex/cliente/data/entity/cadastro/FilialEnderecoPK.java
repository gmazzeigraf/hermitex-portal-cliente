package br.com.graflogic.hermitex.cliente.data.entity.cadastro;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author gmazz
 *
 */
@Embeddable
public class FilialEnderecoPK implements Serializable {

	private static final long serialVersionUID = 7080517132637225034L;

	public FilialEnderecoPK() {

	}

	public FilialEnderecoPK(Integer idFilial, String tipo) {
		super();
		this.idFilial = idFilial;
		this.tipo = tipo;
	}

	@Column(name = "id_filial", nullable = false)
	private Integer idFilial;

	@Column(name = "tipo", nullable = false)
	private String tipo;

	public Integer getIdFilial() {
		return idFilial;
	}

	public void setIdFilial(Integer idFilial) {
		this.idFilial = idFilial;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idFilial == null) ? 0 : idFilial.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
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
		FilialEnderecoPK other = (FilialEnderecoPK) obj;
		if (idFilial == null) {
			if (other.idFilial != null)
				return false;
		} else if (!idFilial.equals(other.idFilial))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		return true;
	}
}