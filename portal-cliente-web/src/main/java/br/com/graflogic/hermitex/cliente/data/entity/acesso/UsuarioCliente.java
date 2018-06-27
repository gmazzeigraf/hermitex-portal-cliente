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
@Table(name = "tb_usuario_cliente")
@PrimaryKeyJoinColumn(name = "id")
public class UsuarioCliente extends Usuario {

	private static final long serialVersionUID = 4476232527626862662L;

	public static final String PERMISSAO = "ROLE_USUARIO_CLIENTE";

	public UsuarioCliente() {
		setTipo(DomTipoUsuario.CLIENTE);
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