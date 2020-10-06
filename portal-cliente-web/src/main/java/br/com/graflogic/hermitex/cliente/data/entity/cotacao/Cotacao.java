package br.com.graflogic.hermitex.cliente.data.entity.cotacao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoEndereco;
import br.com.graflogic.hermitex.cliente.data.dom.DomCotacao;
import br.com.graflogic.hermitex.cliente.data.dom.DomCotacao.DomStatus;
import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_cotacao")
public class Cotacao implements Serializable {

	private static final long serialVersionUID = -3435030857444054047L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_COTACAO")
	@SequenceGenerator(name = "SQ_COTACAO", sequenceName = "SQ_COTACAO", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

	@Column(name = "id_filial")
	private Integer idFilial;

	@Column(name = "vl_produtos", nullable = false)
	private BigDecimal valorProdutos;

	@Column(name = "vl_frete", nullable = false)
	private BigDecimal valorFrete;

	@Column(name = "vl_desconto_pagamento", nullable = false)
	private BigDecimal valorDescontoPagamento;

	@Column(name = "vl_desconto_livre", nullable = false)
	private BigDecimal valorDescontoLivre;

	@Column(name = "vl_desconto_especial", nullable = false)
	private BigDecimal valorDescontoEspecial;

	@Column(name = "vl_total", nullable = false)
	private BigDecimal valorTotal;

	@Column(name = "peso_total", nullable = false)
	private BigDecimal pesoTotal;

	@Column(name = "qt_total_itens", nullable = false)
	private Integer quantidadeTotalItens;

	@Column(name = "id_forma_pagamento", nullable = false)
	private Integer idFormaPagamento;

	@Column(name = "ds_forma_pagamento", nullable = false)
	private String descricaoFormaPagamento;

	@Column(name = "in_pedido_faturado", nullable = false)
	private String pedidoFaturado;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cotacao", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<CotacaoItem> itens;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cotacao", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<CotacaoEndereco> enderecos;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cotacao", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<CotacaoFrete> fretes;

	@Transient
	private String tipoFormaPagamento;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public BigDecimal getValorProdutos() {
		return valorProdutos;
	}

	public void setValorProdutos(BigDecimal valorProdutos) {
		this.valorProdutos = valorProdutos;
	}

	public BigDecimal getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}

	public BigDecimal getValorDescontoPagamento() {
		return valorDescontoPagamento;
	}

	public void setValorDescontoPagamento(BigDecimal valorDescontoPagamento) {
		this.valorDescontoPagamento = valorDescontoPagamento;
	}

	public BigDecimal getValorDescontoLivre() {
		return valorDescontoLivre;
	}

	public void setValorDescontoLivre(BigDecimal valorDescontoLivre) {
		this.valorDescontoLivre = valorDescontoLivre;
	}

	public BigDecimal getValorDescontoEspecial() {
		return valorDescontoEspecial;
	}

	public void setValorDescontoEspecial(BigDecimal valorDescontoEspecial) {
		this.valorDescontoEspecial = valorDescontoEspecial;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public BigDecimal getPesoTotal() {
		return pesoTotal;
	}

	public void setPesoTotal(BigDecimal pesoTotal) {
		this.pesoTotal = pesoTotal;
	}

	public Integer getQuantidadeTotalItens() {
		return quantidadeTotalItens;
	}

	public void setQuantidadeTotalItens(Integer quantidadeTotalItens) {
		this.quantidadeTotalItens = quantidadeTotalItens;
	}

	public Integer getIdFormaPagamento() {
		return idFormaPagamento;
	}

	public void setIdFormaPagamento(Integer idFormaPagamento) {
		this.idFormaPagamento = idFormaPagamento;
	}

	public String getDescricaoFormaPagamento() {
		return descricaoFormaPagamento;
	}

	public void setDescricaoFormaPagamento(String descricaoFormaPagamento) {
		this.descricaoFormaPagamento = descricaoFormaPagamento;
	}

	public boolean isPedidoFaturado() {
		return pedidoFaturado.equals(DomBoolean.SIM);
	}

	public void setPedidoFaturado(boolean pedidoFaturado) {
		this.pedidoFaturado = pedidoFaturado ? DomBoolean.SIM : DomBoolean.NAO;
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

	public List<CotacaoItem> getItens() {
		return itens;
	}

	public void setItens(List<CotacaoItem> itens) {
		this.itens = itens;
	}

	public List<CotacaoEndereco> getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(List<CotacaoEndereco> enderecos) {
		this.enderecos = enderecos;
	}

	public List<CotacaoFrete> getFretes() {
		return fretes;
	}

	public void setFretes(List<CotacaoFrete> fretes) {
		this.fretes = fretes;
	}

	public String getTipoFormaPagamento() {
		return tipoFormaPagamento;
	}

	public void setTipoFormaPagamento(String tipoFormaPagamento) {
		this.tipoFormaPagamento = tipoFormaPagamento;
	}

	public String getDeStatus() {
		return DomCotacao.domStatus.getDeValor(status);
	}

	public CotacaoEndereco getEnderecoFaturamento() {
		return getEndereco(DomTipoEndereco.FATURAMENTO);
	}

	public CotacaoEndereco getEnderecoEntrega() {
		return getEndereco(DomTipoEndereco.ENTREGA);
	}

	private CotacaoEndereco getEndereco(String tipo) {
		for (CotacaoEndereco endereco : enderecos) {
			if (tipo.equals(endereco.getId().getTipo())) {
				return endereco;
			}
		}

		return null;
	}

	public boolean isGerada() {
		return null != status && DomStatus.GERADA.equals(status);
	}

	public String getFormattedId() {
		return StringUtils.leftPad(id.toString(), 10, "0");
	}

	public String getFormattedValorTotal() {
		return new DecimalFormat("R$ #,##0.00").format(valorTotal);
	}
}