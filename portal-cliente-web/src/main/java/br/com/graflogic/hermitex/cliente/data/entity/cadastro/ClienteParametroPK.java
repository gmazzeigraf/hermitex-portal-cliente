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
public class ClienteParametroPK implements Serializable {

	private static final long serialVersionUID = -4241268326454868285L;

	public ClienteParametroPK() {

	}

	public ClienteParametroPK(Integer idCliente, String codigoParametro) {
		super();
		this.idCliente = idCliente;
		this.codigoParametro = codigoParametro;
	}

	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

	@Column(name = "cd_parametro", nullable = false)
	private String codigoParametro;

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public String getCodigoParametro() {
		return codigoParametro;
	}

	public void setCodigoParametro(String codigoParametro) {
		this.codigoParametro = codigoParametro;
	}
}