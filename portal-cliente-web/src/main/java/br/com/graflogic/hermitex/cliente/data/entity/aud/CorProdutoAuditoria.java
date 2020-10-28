package br.com.graflogic.hermitex.cliente.data.entity.aud;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_cor_produto_aud")
public class CorProdutoAuditoria implements Serializable {

	private static final long serialVersionUID = -1499947728387041944L;

	@Id
	@Column(name = "id", nullable = false)
	private String id;

	@Column(name = "cd_relacionado", nullable = false)
	private String codigoRelacionado;

	@Column(name = "data", nullable = false)
	private Date data;

	@Column(name = "id_responsavel", nullable = false)
	private Integer idResponsavel;

	@Column(name = "cd_evento", nullable = false)
	private String codigoEvento;

	@Column(name = "observacao")
	private String observacao;

	@Column(name = "objeto")
	private String objeto;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCodigoRelacionado() {
		return codigoRelacionado;
	}

	public void setCodigoRelacionado(String codigoRelacionado) {
		this.codigoRelacionado = codigoRelacionado;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Integer getIdResponsavel() {
		return idResponsavel;
	}

	public void setIdResponsavel(Integer idResponsavel) {
		this.idResponsavel = idResponsavel;
	}

	public String getCodigoEvento() {
		return codigoEvento;
	}

	public void setCodigoEvento(String codigoEvento) {
		this.codigoEvento = codigoEvento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getObjeto() {
		return objeto;
	}

	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}
}