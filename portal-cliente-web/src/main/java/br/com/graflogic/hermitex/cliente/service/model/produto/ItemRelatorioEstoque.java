package br.com.graflogic.hermitex.cliente.service.model.produto;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author gmazz
 *
 */
public class ItemRelatorioEstoque {

	private String codigoProduto;

	private String tituloProduto;

	private String idImagemCapaProduto;

	private List<String> tamanhos;

	private List<Map<String, Integer>> linhas;

	public String getCodigoProduto() {
		return codigoProduto;
	}

	public void setCodigoProduto(String codigoProduto) {
		this.codigoProduto = codigoProduto;
	}

	public String getTituloProduto() {
		return tituloProduto;
	}

	public void setTituloProduto(String tituloProduto) {
		this.tituloProduto = tituloProduto;
	}

	public String getIdImagemCapaProduto() {
		return idImagemCapaProduto;
	}

	public void setIdImagemCapaProduto(String idImagemCapaProduto) {
		this.idImagemCapaProduto = idImagemCapaProduto;
	}

	public List<String> getTamanhos() {
		return tamanhos;
	}

	public void setTamanhos(List<String> tamanhos) {
		this.tamanhos = tamanhos;
	}

	public List<Map<String, Integer>> getLinhas() {
		return linhas;
	}

	public void setLinhas(List<Map<String, Integer>> linhas) {
		this.linhas = linhas;
	}
}