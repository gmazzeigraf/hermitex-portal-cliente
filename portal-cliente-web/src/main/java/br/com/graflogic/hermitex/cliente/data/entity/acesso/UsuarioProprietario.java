package br.com.graflogic.hermitex.cliente.data.entity.acesso;

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
@Table(name = "tb_usuario_proprietario")
@PrimaryKeyJoinColumn(name = "id")
public class UsuarioProprietario extends Usuario {

	private static final long serialVersionUID = 2300225826360645971L;

	public static final String PERMISSAO = "ROLE_USUARIO_PROPRIETARIO";

	public UsuarioProprietario() {
		setTipo(DomTipoUsuario.PROPRIETARIO);
	}
}