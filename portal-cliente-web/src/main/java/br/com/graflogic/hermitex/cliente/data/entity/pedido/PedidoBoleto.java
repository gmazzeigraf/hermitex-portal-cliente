package br.com.graflogic.hermitex.cliente.data.entity.pedido;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_pedido_boleto")
public class PedidoBoleto implements Serializable {

	private static final long serialVersionUID = -5247827684217475897L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_PEDIDO_BOLETO")
	@SequenceGenerator(name = "SQ_PEDIDO_BOLETO", sequenceName = "SQ_PEDIDO_BOLETO", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "id_pedido", nullable = false)
	private Long idPedido;

	@Column(name = "valor", nullable = false)
	private BigDecimal valor;

	@Column(name = "dt_vencimento", nullable = false)
	private Date dataVencimento;

	@Column(name = "url", nullable = false)
	private String url;

	@Column(name = "id_transacao_pagamento", nullable = false)
	private String idTransacaoPagamento;

	@Column(name = "nosso_numero")
	private String nossoNumero;

	@Column(name = "dt_notificacao_pagamento")
	private Date dataNotificacaoPagamento;

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_pagamento")
	private Date dataPagamento;

	@Column(name = "status", nullable = false)
	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pedido", referencedColumnName = "id", insertable = false, updatable = false)
	private Pedido pedido;

	// Filtros
	@Transient
	private Integer idCliente;

	@Transient
	private Integer idFilial;

	@Transient
	private Date dataVencimentoDe;

	@Transient
	private Date dataVencimentoAte;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Long idPedido) {
		this.idPedido = idPedido;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIdTransacaoPagamento() {
		return idTransacaoPagamento;
	}

	public void setIdTransacaoPagamento(String idTransacaoPagamento) {
		this.idTransacaoPagamento = idTransacaoPagamento;
	}

	public String getNossoNumero() {
		return nossoNumero;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public Date getDataNotificacaoPagamento() {
		return dataNotificacaoPagamento;
	}

	public void setDataNotificacaoPagamento(Date dataNotificacaoPagamento) {
		this.dataNotificacaoPagamento = dataNotificacaoPagamento;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public Integer getIdFilial() {
		return idFilial;
	}

	public void setIdFilial(Integer idFilial) {
		this.idFilial = idFilial;
	}

	public Date getDataVencimentoDe() {
		return dataVencimentoDe;
	}

	public void setDataVencimentoDe(Date dataVencimentoDe) {
		this.dataVencimentoDe = dataVencimentoDe;
	}

	public Date getDataVencimentoAte() {
		return dataVencimentoAte;
	}

	public void setDataVencimentoAte(Date dataVencimentoAte) {
		this.dataVencimentoAte = dataVencimentoAte;
	}

	public String getDeStatus() {
		return DomPedido.domStatusBoleto.getDeValor(status);
	}

	public String getFormattedIdPedido() {
		return StringUtils.leftPad(idPedido.toString(), 10, "0");
	}

	public String getFormattedValorTotal() {
		return new DecimalFormat("R$ #,##0.00").format(valor);
	}
}