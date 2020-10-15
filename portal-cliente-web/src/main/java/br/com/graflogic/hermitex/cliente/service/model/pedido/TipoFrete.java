package br.com.graflogic.hermitex.cliente.service.model.pedido;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoFrete;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoFrete;
import br.com.graflogic.hermitex.cliente.service.model.frete.Frete;

/**
 * 
 * @author gmazz
 *
 */
public class TipoFrete {

	private String codigoServico;

	private BigDecimal valor;

	private List<Frete> fretes;

	public String getCodigoServico() {
		return codigoServico;
	}

	public void setCodigoServico(String codigoServico) {
		this.codigoServico = codigoServico;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public List<Frete> getFretes() {
		return fretes;
	}

	public void setFretes(List<Frete> fretes) {
		this.fretes = fretes;
	}

	public String getDeCodigoServico() {
		return DomPedido.domServicoFrete.getDeValor(codigoServico);
	}

	public String getDescricao() {
		return getDeCodigoServico() + " - " + new DecimalFormat("R$ #,##0.00").format(valor);
	}

	public List<PedidoFrete> getFretesPedido() {
		return getFretes().stream().map(Frete::asPedidoFrete).collect(Collectors.toList());
	}

	public List<CotacaoFrete> getFretesCotacao() {
		return getFretes().stream().map(Frete::asCotacaoFrete).collect(Collectors.toList());
	}
}