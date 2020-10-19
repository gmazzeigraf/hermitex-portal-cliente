package br.com.graflogic.hermitex.cliente.data.entity.cotacao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import br.com.graflogic.hermitex.cliente.data.dom.DomCotacao;

/**
 * 
 * @author gmazz
 *
 */
public class CotacaoSimple implements Serializable {

	private static final long serialVersionUID = -3546743256836236527L;

	private Long id;

	private BigDecimal valorTotal;

	private String status;

	private Integer quantidadeItens;

	private Integer quantidadeTotalItens;

	private Date dataCadastro;

	private Integer idUsuarioCadastro;

	// Filtros
	private Integer idCliente;

	private Integer idFilial;

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

	public Integer getQuantidadeTotalItens() {
		return quantidadeTotalItens;
	}

	public void setQuantidadeTotalItens(Integer quantidadeTotalItens) {
		this.quantidadeTotalItens = quantidadeTotalItens;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Integer getIdUsuarioCadastro() {
		return idUsuarioCadastro;
	}

	public void setIdUsuarioCadastro(Integer idUsuarioCadastro) {
		this.idUsuarioCadastro = idUsuarioCadastro;
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

	public String getDeStatus() {
		return DomCotacao.domStatus.getDeValor(status);
	}

	public String getFormattedId() {
		return StringUtils.leftPad(id.toString(), 10, "0");
	}
}