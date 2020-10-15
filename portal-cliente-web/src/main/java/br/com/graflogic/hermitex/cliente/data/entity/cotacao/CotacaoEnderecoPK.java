package br.com.graflogic.hermitex.cliente.data.entity.cotacao;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author gmazz
 *
 */
@Embeddable
public class CotacaoEnderecoPK implements Serializable {

	private static final long serialVersionUID = 5154457394401760933L;

	public CotacaoEnderecoPK() {

	}

	public CotacaoEnderecoPK(Long idCotacao, String tipo) {
		super();
		this.idCotacao = idCotacao;
		this.tipo = tipo;
	}

	@Column(name = "id_cotacao", nullable = false)
	private Long idCotacao;

	@Column(name = "tipo", nullable = false)
	private String tipo;

	public Long getIdCotacao() {
		return idCotacao;
	}

	public void setIdCotacao(Long idCotacao) {
		this.idCotacao = idCotacao;
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
		result = prime * result + ((idCotacao == null) ? 0 : idCotacao.hashCode());
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
		CotacaoEnderecoPK other = (CotacaoEnderecoPK) obj;
		if (idCotacao == null) {
			if (other.idCotacao != null)
				return false;
		} else if (!idCotacao.equals(other.idCotacao))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		return true;
	}
}