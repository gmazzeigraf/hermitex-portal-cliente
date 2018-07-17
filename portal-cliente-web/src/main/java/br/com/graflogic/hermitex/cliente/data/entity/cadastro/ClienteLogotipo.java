package br.com.graflogic.hermitex.cliente.data.entity.cadastro;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
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
@Table(name = "tb_cliente_logotipo")
public class ClienteLogotipo implements Serializable {

	private static final long serialVersionUID = 5926640573469169043L;

	@Id
	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

	@Column(name = "conteudo", nullable = false)
	private String conteudo;

	@MapsId
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente", referencedColumnName = "id", insertable = false, updatable = false)
	private Cliente cliente;

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
}