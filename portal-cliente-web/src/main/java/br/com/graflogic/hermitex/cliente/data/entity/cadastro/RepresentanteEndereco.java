package br.com.graflogic.hermitex.cliente.data.entity.cadastro;

import java.io.Serializable;

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
@Table(name = "tb_representante_endereco")
public class RepresentanteEndereco implements Serializable {

	private static final long serialVersionUID = -4181752287993139467L;

	public RepresentanteEndereco() {

	}

	public RepresentanteEndereco(RepresentanteEnderecoPK id) {
		super();
		this.id = id;
	}

	@EmbeddedId
	private RepresentanteEnderecoPK id;

	@Column(name = "sg_estado", nullable = false)
	private String siglaEstado;

	@Column(name = "id_municipio", nullable = false)
	private Integer idMunicipio;

	@Column(name = "cep", nullable = false)
	private String cep;

	@Column(name = "bairro", nullable = false)
	private String bairro;

	@Column(name = "logradouro", nullable = false)
	private String logradouro;

	@Column(name = "numero", nullable = false)
	private String numero;

	@Column(name = "complemento")
	private String complemento;

	@MapsId("idRepresentante")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_representante", referencedColumnName = "id", insertable = false, updatable = false)
	private Representante representante;

	public RepresentanteEnderecoPK getId() {
		return id;
	}

	public void setId(RepresentanteEnderecoPK id) {
		this.id = id;
	}

	public String getSiglaEstado() {
		return siglaEstado;
	}

	public void setSiglaEstado(String siglaEstado) {
		this.siglaEstado = siglaEstado;
	}

	public Integer getIdMunicipio() {
		return idMunicipio;
	}

	public void setIdMunicipio(Integer idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public Representante getRepresentante() {
		return representante;
	}

	public void setRepresentante(Representante representante) {
		this.representante = representante;
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
		RepresentanteEndereco other = (RepresentanteEndereco) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}