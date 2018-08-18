package br.com.graflogic.hermitex.cliente.service.impl.produto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoApresentacaoLista;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoTamanho;
import br.com.graflogic.hermitex.cliente.service.model.produto.ItemRelatorioEstoque;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class EstoqueService {

	@Autowired
	private ProdutoService produtoService;

	public List<ItemRelatorioEstoque> geraRelatorio(Integer idCliente, boolean geraSolicitacao) {
		List<ItemRelatorioEstoque> itens = new ArrayList<>();

		// Consulta os produtos pelo cliente
		ProdutoApresentacaoLista produtoFilter = new ProdutoApresentacaoLista();
		produtoFilter.setIdCliente(idCliente);

		List<ProdutoApresentacaoLista> produtos = produtoService.consultaApresentacaoLista(produtoFilter);

		for (ProdutoApresentacaoLista produto : produtos) {
			// Gera o item com o produto
			ItemRelatorioEstoque item = new ItemRelatorioEstoque();
			item.setCodigoProduto(produto.getCodigo());
			item.setTituloProduto(produto.getTitulo());
			item.setIdImagemCapaProduto(produto.getIdImagemCapa());
			item.setTamanhos(new ArrayList<>());
			item.setLinhas(new ArrayList<>());
			// Adiciona a linha de estoque
			item.getLinhas().add(new HashMap<>());
			if (geraSolicitacao) {
				// Adiciona a linha de solicitacao
				item.getLinhas().add(new HashMap<>());
			}

			// Consulta o produto completo
			Produto produtoCompleto = produtoService.consultaCompletoPorId(produto.getId());

			// Gera os tamanhos
			for (ProdutoTamanho tamanho : produtoCompleto.getTamanhos()) {
				item.getTamanhos().add(tamanho.getId().getCodigoTamanho());
				item.getLinhas().get(0).put(tamanho.getId().getCodigoTamanho(), tamanho.getQuantidadeEstoque());
				if (geraSolicitacao) {
					item.getLinhas().get(1).put(tamanho.getId().getCodigoTamanho(), null);
				}
			}

			itens.add(item);
		}

		return itens;
	}
}