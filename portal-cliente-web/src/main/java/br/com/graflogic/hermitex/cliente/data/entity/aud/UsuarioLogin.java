package br.com.graflogic.hermitex.cliente.data.entity.aud;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso;
import br.com.graflogic.utilities.datautil.util.FormatUtil;

/**
 * 
 * @author gmazz
 * 
 */
@Entity
@Table(name = "tb_usuario_login")
public class UsuarioLogin implements Serializable {

	private static final long serialVersionUID = -8347413728922985806L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "id_usuario", nullable = false)
	private Integer idUsuario;

	@Column(name = "data", nullable = false)
	private Date data;

	@Column(name = "ip_origem", nullable = false)
	private String ipOrigem;

	// Filtros
	@Transient
	private Date dataDe;

	@Transient
	private Date dataAte;

	@Transient
	private String cpfUsuario;

	// Exibicao
	@Transient
	private String nomeUsuario;

	@Transient
	private String tipoUsuario;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getIpOrigem() {
		return ipOrigem;
	}

	public void setIpOrigem(String ipOrigem) {
		this.ipOrigem = ipOrigem;
	}

	public Date getDataDe() {
		return dataDe;
	}

	public void setDataDe(Date dataDe) {
		this.dataDe = dataDe;
	}

	public Date getDataAte() {
		return dataAte;
	}

	public void setDataAte(Date dataAte) {
		this.dataAte = dataAte;
	}

	public String getCpfUsuario() {
		return cpfUsuario;
	}

	public void setCpfUsuario(String cpfUsuario) {
		this.cpfUsuario = cpfUsuario;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(String tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public String getFormattedCpfUsuario() {
		return FormatUtil.formatCPF(cpfUsuario);
	}

	public String getDeTipoUsuario() {
		return DomAcesso.domTipoUsuario.getDeValor(tipoUsuario);
	}
}