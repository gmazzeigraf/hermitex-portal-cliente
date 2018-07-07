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
public class RepresentanteEnderecoPK implements Serializable {

	private static final long serialVersionUID = 3201717275734568979L;

	public RepresentanteEnderecoPK() {

	}

	public RepresentanteEnderecoPK(Integer idRepresentante, String tipo) {
		super();
		this.idRepresentante = idRepresentante;
		this.tipo = tipo;
	}

	@Column(name = "id_representante", nullable = false)
	private Integer idRepresentante;

	@Column(name = "tipo", nullable = false)
	private String tipo;

	public Integer getIdRepresentante() {
		return idRepresentante;
	}

	public void setIdRepresentante(Integer idRepresentante) {
		this.idRepresentante = idRepresentante;
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
		result = prime * result + ((idRepresentante == null) ? 0 : idRepresentante.hashCode());
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
		RepresentanteEnderecoPK other = (RepresentanteEnderecoPK) obj;
		if (idRepresentante == null) {
			if (other.idRepresentante != null)
				return false;
		} else if (!idRepresentante.equals(other.idRepresentante))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		return true;
	}
}