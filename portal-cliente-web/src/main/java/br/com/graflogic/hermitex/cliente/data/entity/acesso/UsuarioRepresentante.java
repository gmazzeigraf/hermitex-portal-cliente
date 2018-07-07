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
@Table(name = "tb_usuario_representante")
@PrimaryKeyJoinColumn(name = "id")
public class UsuarioRepresentante extends Usuario {

	private static final long serialVersionUID = 6633373805565866073L;

	public static final String PERMISSAO = "ROLE_USUARIO_REPRESENTANTE";

	public UsuarioRepresentante() {
		setTipo(DomTipoUsuario.REPRESENTANTE);
	}

	@Column(name = "id_representante", nullable = false)
	private Integer idRepresentante;

	public Integer getIdRepresentante() {
		return idRepresentante;
	}

	public void setIdRepresentante(Integer idRepresentante) {
		this.idRepresentante = idRepresentante;
	}
}