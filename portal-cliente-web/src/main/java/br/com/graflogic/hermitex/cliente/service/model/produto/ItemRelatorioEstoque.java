package br.com.graflogic.hermitex.cliente.service.model.produto;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author gmazz
 *
 */
public class ItemRelatorioEstoque {

	private Integer idProduto;

	private String codigoProduto;

	private String tituloProduto;

	private String idImagemCapaProduto;

	private List<String> tamanhos;

	private List<Map<String, Object>> linhas;

	public Integer getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(Integer idProduto) {
		this.idProduto = idProduto;
	}

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

	public List<Map<String, Object>> getLinhas() {
		return linhas;
	}

	public void setLinhas(List<Map<String, Object>> linhas) {
		this.linhas = linhas;
	}
}