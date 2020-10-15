package br.com.graflogic.hermitex.cliente.service.model.frete;

import java.math.BigDecimal;

import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoFrete;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoFrete;

/**
 * 
 * @author gmazz
 *
 */
public class Frete {

	private Integer idEmbalagem;

	private BigDecimal pesoItens;

	private Integer quantidadeItens;

	private BigDecimal valor;

	private Integer prazoDias;

	private String codigoServico;

	private String nomeEmbalagem;

	public Integer getIdEmbalagem() {
		return idEmbalagem;
	}

	public void setIdEmbalagem(Integer idEmbalagem) {
		this.idEmbalagem = idEmbalagem;
	}

	public BigDecimal getPesoItens() {
		return pesoItens;
	}

	public void setPesoItens(BigDecimal pesoItens) {
		this.pesoItens = pesoItens;
	}

	public Integer getQuantidadeItens() {
		return quantidadeItens;
	}

	public void setQuantidadeItens(Integer quantidadeItens) {
		this.quantidadeItens = quantidadeItens;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Integer getPrazoDias() {
		return prazoDias;
	}

	public void setPrazoDias(Integer prazoDias) {
		this.prazoDias = prazoDias;
	}

	public String getCodigoServico() {
		return codigoServico;
	}

	public void setCodigoServico(String codigoServico) {
		this.codigoServico = codigoServico;
	}

	public String getNomeEmbalagem() {
		return nomeEmbalagem;
	}

	public void setNomeEmbalagem(String nomeEmbalagem) {
		this.nomeEmbalagem = nomeEmbalagem;
	}

	public PedidoFrete asPedidoFrete() {
		PedidoFrete entity = new PedidoFrete();
		entity.setIdEmbalagem(idEmbalagem);
		entity.setPesoItens(pesoItens);
		entity.setQuantidadeItens(quantidadeItens);
		entity.setValor(valor);
		entity.setPrazoDias(prazoDias);
		entity.setCodigoServico(codigoServico);
		entity.setNomeEmbalagem(nomeEmbalagem);

		return entity;
	}

	public CotacaoFrete asCotacaoFrete() {
		CotacaoFrete entity = new CotacaoFrete();
		entity.setIdEmbalagem(idEmbalagem);
		entity.setPesoItens(pesoItens);
		entity.setQuantidadeItens(quantidadeItens);
		entity.setValor(valor);
		entity.setPrazoDias(prazoDias);
		entity.setCodigoServico(codigoServico);
		entity.setNomeEmbalagem(nomeEmbalagem);

		return entity;
	}
}