package br.com.graflogic.hermitex.cliente.data.entity.acesso;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomStatusUsuario;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario implements Serializable {

	private static final long serialVersionUID = -6006497790049297498L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_USUARIO")
	@SequenceGenerator(name = "SQ_USUARIO", sequenceName = "SQ_USUARIO", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "telefone")
	private String telefone;

	@Column(name = "senha", nullable = false)
	private String senha;

	@Column(name = "id_perfil", nullable = false)
	private Integer idPerfil;

	@Column(name = "tipo", nullable = false)
	private String tipo;

	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "status_senha", nullable = false)
	private String statusSenha;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Integer getIdPerfil() {
		return idPerfil;
	}

	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusSenha() {
		return statusSenha;
	}

	public void setStatusSenha(String statusSenha) {
		this.statusSenha = statusSenha;
	}

	public Long getVersao() {
		return versao;
	}

	public void setVersao(Long versao) {
		this.versao = versao;
	}

	public boolean isAtivo() {
		return null != status && DomStatusUsuario.ATIVO.equals(status);
	}

	public boolean isInativo() {
		if (DomStatusUsuario.INATIVO.equals(status)) {
			return true;
		}
		return false;
	}

	public String getDeStatus() {
		return DomAcesso.domStatusUsuario.getDeValor(status);
	}

	public String getDeTipo() {
		return DomAcesso.domTipoUsuario.getDeValor(tipo);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}