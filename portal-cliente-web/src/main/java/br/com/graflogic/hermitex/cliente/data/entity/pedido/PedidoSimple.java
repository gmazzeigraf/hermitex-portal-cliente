package br.com.graflogic.hermitex.cliente.data.entity.pedido;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;

/**
 * 
 * @author gmazz
 *
 */
public class PedidoSimple implements Serializable {

	private static final long serialVersionUID = -5750731995004783525L;

	private Long id;

	private BigDecimal valorTotal;

	private String codigoFormaPagamento;

	private String status;

	private Integer quantidadeItens;

	private Date dataCadastro;

	// Filtros
	private Integer idCliente;

	private Integer idFilial;

	private Integer idJanelaCompra;

	@Transient
	private Date dataCadastroDe;

	@Transient
	private Date dataCadastroAte;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getCodigoFormaPagamento() {
		return codigoFormaPagamento;
	}

	public void setCodigoFormaPagamento(String codigoFormaPagamento) {
		this.codigoFormaPagamento = codigoFormaPagamento;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getQuantidadeItens() {
		return quantidadeItens;
	}

	public void setQuantidadeItens(Integer quantidadeItens) {
		this.quantidadeItens = quantidadeItens;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
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

	public Integer getIdJanelaCompra() {
		return idJanelaCompra;
	}

	public void setIdJanelaCompra(Integer idJanelaCompra) {
		this.idJanelaCompra = idJanelaCompra;
	}

	public Date getDataCadastroDe() {
		return dataCadastroDe;
	}

	public void setDataCadastroDe(Date dataCadastroDe) {
		this.dataCadastroDe = dataCadastroDe;
	}

	public Date getDataCadastroAte() {
		return dataCadastroAte;
	}

	public void setDataCadastroAte(Date dataCadastroAte) {
		this.dataCadastroAte = dataCadastroAte;
	}

	public String getDeTipoPagamento() {
		return DomPedido.domFormaPagamento.getDeValor(codigoFormaPagamento);
	}

	public String getDeStatus() {
		return DomPedido.domStatus.getDeValor(status);
	}

	public String getFormattedId() {
		return StringUtils.leftPad(id.toString(), 10, "0");
	}
}