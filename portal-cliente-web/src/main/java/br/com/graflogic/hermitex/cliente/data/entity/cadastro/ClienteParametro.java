package br.com.graflogic.hermitex.cliente.data.entity.cadastro;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_cliente_parametro")
public class ClienteParametro implements Serializable {

	private static final long serialVersionUID = -3260937595485503587L;

	@EmbeddedId
	private ClienteParametroPK id;

	@Column(name = "valor", nullable = false)
	private String valor;

	public ClienteParametroPK getId() {
		return id;
	}

	public void setId(ClienteParametroPK id) {
		this.id = id;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}