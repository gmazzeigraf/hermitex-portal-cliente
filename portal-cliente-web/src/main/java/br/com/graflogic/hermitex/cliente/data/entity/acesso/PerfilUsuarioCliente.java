package br.com.graflogic.hermitex.cliente.data.entity.acesso;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomTipoUsuario;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_perfil_usuario_cliente")
@PrimaryKeyJoinColumn(name = "id")
public class PerfilUsuarioCliente extends PerfilUsuario {

	private static final long serialVersionUID = -6717640063562503639L;

	public PerfilUsuarioCliente() {
		setTipoUsuario(DomTipoUsuario.CLIENTE);
	}

	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}
}