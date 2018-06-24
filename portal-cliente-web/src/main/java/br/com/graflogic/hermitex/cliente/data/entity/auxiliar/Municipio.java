package br.com.graflogic.hermitex.cliente.data.entity.auxiliar;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_municipio")
public class Municipio implements Serializable {

	private static final long serialVersionUID = -989602533191974191L;

	@Id
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "sg_estado", nullable = false)
	private String siglaEstado;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSiglaEstado() {
		return siglaEstado;
	}

	public void setSiglaEstado(String siglaEstado) {
		this.siglaEstado = siglaEstado;
	}
}