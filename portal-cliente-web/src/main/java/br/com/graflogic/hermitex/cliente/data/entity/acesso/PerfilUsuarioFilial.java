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
@Table(name = "tb_perfil_usuario_filial")
@PrimaryKeyJoinColumn(name = "id")
public class PerfilUsuarioFilial extends PerfilUsuario {

	private static final long serialVersionUID = 5112274571939155585L;

	public PerfilUsuarioFilial() {
		setTipoUsuario(DomTipoUsuario.FILIAL);
	}

	@Column(name = "id_filial", nullable = false)
	private Integer idFilial;

	public Integer getIdFilial() {
		return idFilial;
	}

	public void setIdFilial(Integer idFilial) {
		this.idFilial = idFilial;
	}
}