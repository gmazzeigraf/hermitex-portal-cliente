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
@Table(name = "tb_estado")
public class Estado implements Serializable {

	private static final long serialVersionUID = -1931916736242330057L;

	@Id
	@Column(name = "sigla", nullable = false)
	private String sigla;

	@Column(name = "nome", nullable = false)
	private String nome;

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}