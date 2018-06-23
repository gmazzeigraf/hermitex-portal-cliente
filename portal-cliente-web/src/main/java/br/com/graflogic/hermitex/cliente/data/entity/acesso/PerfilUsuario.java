package br.com.graflogic.hermitex.cliente.data.entity.acesso;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomStatusPerfilUsuario;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_perfil_usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public class PerfilUsuario implements Serializable {

	private static final long serialVersionUID = 8034554295308314201L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_PERFIL_USUARIO")
	@SequenceGenerator(name = "SQ_PERFIL_USUARIO", sequenceName = "SQ_PERFIL_USUARIO", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "descricao")
	private String descricao;

	@Column(name = "tp_usuario", nullable = false)
	private String tipoUsuario;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tb_perfil_usuario_permissao_acesso", joinColumns = {
			@JoinColumn(name = "id_perfil", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "cd_permissao", referencedColumnName = "codigo") })
	private List<PermissaoAcesso> permissoes;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(String tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getVersao() {
		return versao;
	}

	public void setVersao(Long versao) {
		this.versao = versao;
	}

	public List<PermissaoAcesso> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<PermissaoAcesso> permissoes) {
		this.permissoes = permissoes;
	}
	

	public boolean isAtivo() {
		return null != status && DomStatusPerfilUsuario.ATIVO.equals(status);
	}

	public boolean isInativo() {
		if (DomStatusPerfilUsuario.INATIVO.equals(status)) {
			return true;
		}
		return false;
	}

	public String getDeStatus() {
		return DomAcesso.domStatusPerfilUsuario.getDeValor(status);
	}
}