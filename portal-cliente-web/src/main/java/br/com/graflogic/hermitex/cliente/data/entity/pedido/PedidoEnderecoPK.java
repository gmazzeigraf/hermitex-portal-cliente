package br.com.graflogic.hermitex.cliente.data.entity.pedido;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author gmazz
 *
 */
@Embeddable
public class PedidoEnderecoPK implements Serializable {

	private static final long serialVersionUID = -8580283902585283156L;

	public PedidoEnderecoPK() {

	}

	public PedidoEnderecoPK(Long idPedido, String tipo) {
		super();
		this.idPedido = idPedido;
		this.tipo = tipo;
	}

	@Column(name = "id_pedido", nullable = false)
	private Long idPedido;

	@Column(name = "tipo", nullable = false)
	private String tipo;

	public Long getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Long idPedido) {
		this.idPedido = idPedido;
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
		result = prime * result + ((idPedido == null) ? 0 : idPedido.hashCode());
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
		PedidoEnderecoPK other = (PedidoEnderecoPK) obj;
		if (idPedido == null) {
			if (other.idPedido != null)
				return false;
		} else if (!idPedido.equals(other.idPedido))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		return true;
	}
}