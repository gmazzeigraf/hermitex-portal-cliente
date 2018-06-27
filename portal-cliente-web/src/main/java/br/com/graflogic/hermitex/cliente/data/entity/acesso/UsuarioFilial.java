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
@Table(name = "tb_usuario_filial")
@PrimaryKeyJoinColumn(name = "id")
public class UsuarioFilial extends Usuario {

	private static final long serialVersionUID = -7102418825036311074L;

	public static final String PERMISSAO = "ROLE_USUARIO_FILIAL";

	public UsuarioFilial() {
		setTipo(DomTipoUsuario.FILIAL);
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