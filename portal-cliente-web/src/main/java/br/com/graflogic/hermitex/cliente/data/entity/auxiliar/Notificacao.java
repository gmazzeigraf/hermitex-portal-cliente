package br.com.graflogic.hermitex.cliente.data.entity.auxiliar;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.graflogic.hermitex.cliente.data.dom.DomNotificacao;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_notificacao")
public class Notificacao implements Serializable {

	private static final long serialVersionUID = 9156728856434654015L;

	@Id
	@Column(name = "id", nullable = false)
	private String id;

	@Column(name = "tipo", nullable = false)
	private String tipo;

	@Column(name = "titulo")
	private String titulo;

	@Column(name = "conteudo", nullable = false)
	private String conteudo;

	@Column(name = "destinatarios", nullable = false)
	private String destinatarios;

	@Column(name = "anexos")
	private String anexos;

	@Column(name = "dt_solicitacao", nullable = false)
	private Date dataSolicitacao;

	@Column(name = "dt_envio")
	private Date dataEnvio;

	@Column(name = "status", nullable = false)
	private String status;

	// Filtros
	@Transient
	private Date dataSolicitacaoDe;

	@Transient
	private Date dataSolicitacaoAte;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public String getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios(String destinatarios) {
		this.destinatarios = destinatarios;
	}

	public String getAnexos() {
		return anexos;
	}

	public void setAnexos(String anexos) {
		this.anexos = anexos;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDataSolicitacaoDe() {
		return dataSolicitacaoDe;
	}

	public void setDataSolicitacaoDe(Date dataSolicitacaoDe) {
		this.dataSolicitacaoDe = dataSolicitacaoDe;
	}

	public Date getDataSolicitacaoAte() {
		return dataSolicitacaoAte;
	}

	public void setDataSolicitacaoAte(Date dataSolicitacaoAte) {
		this.dataSolicitacaoAte = dataSolicitacaoAte;
	}

	public String getDeTipo() {
		return DomNotificacao.domTipo.getDeValor(tipo);
	}

	public String getDeStatus() {
		return DomNotificacao.domStatus.getDeValor(status);
	}
}