package br.com.graflogic.hermitex.cliente.data.entity.pedido;

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
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatus;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_pedido")
public class Pedido implements Serializable {

	private static final long serialVersionUID = 5500007211642134415L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_PEDIDO")
	@SequenceGenerator(name = "SQ_PEDIDO", sequenceName = "SQ_PEDIDO", allocationSize = 1)
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

	@Column(name = "vl_desconto", nullable = false)
	private BigDecimal valorDesconto;

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

	@Column(name = "id_ordem_pagamento")
	private String idOrdemPagamento;

	@Column(name = "id_transacao_pagamento")
	private String idTransacaoPagamento;

	@Column(name = "id_janela_compra", nullable = false)
	private Integer idJanelaCompra;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pedido", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<PedidoItem> itens;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pedido", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<PedidoEndereco> enderecos;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pedido", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<PedidoFrete> fretes;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pedido", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<PedidoBoleto> boletos;

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

	public BigDecimal getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(BigDecimal valorDesconto) {
		this.valorDesconto = valorDesconto;
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

	public String getIdOrdemPagamento() {
		return idOrdemPagamento;
	}

	public void setIdOrdemPagamento(String idOrdemPagamento) {
		this.idOrdemPagamento = idOrdemPagamento;
	}

	public String getIdTransacaoPagamento() {
		return idTransacaoPagamento;
	}

	public void setIdTransacaoPagamento(String idTransacaoPagamento) {
		this.idTransacaoPagamento = idTransacaoPagamento;
	}

	public Integer getIdJanelaCompra() {
		return idJanelaCompra;
	}

	public void setIdJanelaCompra(Integer idJanelaCompra) {
		this.idJanelaCompra = idJanelaCompra;
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

	public List<PedidoItem> getItens() {
		return itens;
	}

	public void setItens(List<PedidoItem> itens) {
		this.itens = itens;
	}

	public List<PedidoEndereco> getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(List<PedidoEndereco> enderecos) {
		this.enderecos = enderecos;
	}

	public List<PedidoFrete> getFretes() {
		return fretes;
	}

	public void setFretes(List<PedidoFrete> fretes) {
		this.fretes = fretes;
	}

	public List<PedidoBoleto> getBoletos() {
		return boletos;
	}

	public void setBoletos(List<PedidoBoleto> boletos) {
		this.boletos = boletos;
	}

	public String getTipoFormaPagamento() {
		return tipoFormaPagamento;
	}

	public void setTipoFormaPagamento(String tipoFormaPagamento) {
		this.tipoFormaPagamento = tipoFormaPagamento;
	}

	public String getDeStatus() {
		return DomPedido.domStatus.getDeValor(status);
	}

	public PedidoEndereco getEnderecoFaturamento() {
		return getEndereco(DomTipoEndereco.FATURAMENTO);
	}

	public PedidoEndereco getEnderecoEntrega() {
		return getEndereco(DomTipoEndereco.ENTREGA);
	}

	private PedidoEndereco getEndereco(String tipo) {
		for (PedidoEndereco endereco : enderecos) {
			if (tipo.equals(endereco.getId().getTipo())) {
				return endereco;
			}
		}

		return null;
	}

	public boolean isPagamentoPendente() {
		return null != status && DomStatus.PAGAMENTO_PENDENTE.equals(status);
	}

	public boolean isPago() {
		return null != status && DomStatus.PAGO.equals(status);
	}

	public boolean isEnviado() {
		return null != status && DomStatus.ENVIADO.equals(status);
	}

	public String getFormattedId() {
		return StringUtils.leftPad(id.toString(), 10, "0");
	}

	public String getFormattedValorTotal() {
		return new DecimalFormat("R$ #,##0.00").format(valorTotal);
	}
}