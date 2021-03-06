package br.com.graflogic.hermitex.cliente.web.controller.dom;

import java.io.Serializable;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.hermitex.cliente.data.dom.DomProduto;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class DomProdutoController implements Serializable {

	private static final long serialVersionUID = 8011957357238708834L;

	public Map<String, Object> getStatusMap() {
		return DomProduto.domStatus.getMap();
	}

	public Map<String, Object> getStatusTamanhoMap() {
		return DomProduto.domStatusTamanho.getMap();
	}

	public Map<String, Object> getStatusCorMap() {
		return DomProduto.domStatusCor.getMap();
	}

	public Map<String, Object> getStatusEmbalagemMap() {
		return DomProduto.domStatusEmbalagem.getMap();
	}

	public Map<String, Object> getStatusCategoriaMap() {
		return DomProduto.domStatusCategoria.getMap();
	}

	public Map<String, Object> getStatusSetorMap() {
		return DomProduto.domStatusSetor.getMap();
	}

	public Map<String, Object> getGeneroMap() {
		return DomProduto.domGenero.getMap();
	}

	public Map<String, Object> getTipoMap() {
		return DomProduto.domTipo.getMap();
	}

	public Map<String, Object> getStatusSolicitacaoEstoqueMap() {
		return DomProduto.domStatusSolicitacaoEstoque.getMap();
	}
}