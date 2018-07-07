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
@Table(name = "tb_perfil_usuario_representante")
@PrimaryKeyJoinColumn(name = "id")
public class PerfilUsuarioRepresentante extends PerfilUsuario {

	private static final long serialVersionUID = 3634325249075518869L;

	public PerfilUsuarioRepresentante() {
		setTipoUsuario(DomTipoUsuario.REPRESENTANTE);
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