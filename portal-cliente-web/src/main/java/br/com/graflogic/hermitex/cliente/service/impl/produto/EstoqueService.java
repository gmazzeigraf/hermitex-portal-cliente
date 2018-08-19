package br.com.graflogic.hermitex.cliente.service.impl.produto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaSolicitacaoEstoque;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatusSolicitacaoEstoque;
import br.com.graflogic.hermitex.cliente.data.entity.aud.SolicitacaoEstoqueAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoApresentacaoLista;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoTamanho;
import br.com.graflogic.hermitex.cliente.data.entity.produto.SolicitacaoEstoque;
import br.com.graflogic.hermitex.cliente.data.entity.produto.SolicitacaoEstoqueItem;
import br.com.graflogic.hermitex.cliente.data.impl.aud.SolicitacaoEstoqueAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.SolicitacaoEstoqueRepository;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.model.produto.ItemRelatorioEstoque;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class EstoqueService {

	@Autowired
	private SolicitacaoEstoqueRepository repository;

	@Autowired
	private SolicitacaoEstoqueAuditoriaRepository auditoriaRepository;

	@Autowired
	private ProdutoService produtoService;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastraSolicitacao(SolicitacaoEstoque entity) {
		if (entity.getItens().isEmpty()) {
			throw new DadosInvalidosException("Ao menos um item e tamanho deve ser solicitado");
		}

		entity.setStatus(DomStatusSolicitacaoEstoque.CADASTRADA);

		List<SolicitacaoEstoqueItem> itens = entity.getItens();
		entity.setItens(null);

		try {
			repository.store(entity);

			entity.setItens(itens);

			executaAtualiza(entity);

			registraAuditoria(entity.getId(), entity, DomEventoAuditoriaSolicitacaoEstoque.CADASTRO, null);

		} catch (Throwable t) {
			entity.setItens(itens);

			throw t;
		}
	}

	@Transactional(rollbackFor = Throwable.class)
	public void finaliza(SolicitacaoEstoque entity, String observacao) {
		if (!entity.isCadastrada()) {
			throw new DadosInvalidosException("Apenas solicitações cadastradas podem ser marcados como finalizadas");
		}

		entity.setStatus(DomStatusSolicitacaoEstoque.FINALIZADA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaSolicitacaoEstoque.FINALIZACAO, observacao);
	}

	private void executaAtualiza(SolicitacaoEstoque entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<ItemRelatorioEstoque> geraRelatorio(Integer idCliente, boolean geraSolicitacao) {
		List<ItemRelatorioEstoque> itens = new ArrayList<>();

		// Consulta os produtos pelo cliente
		ProdutoApresentacaoLista produtoFilter = new ProdutoApresentacaoLista();
		produtoFilter.setIdCliente(idCliente);

		List<ProdutoApresentacaoLista> produtos = produtoService.consultaApresentacaoLista(produtoFilter);

		for (ProdutoApresentacaoLista produto : produtos) {
			// Gera o item com o produto
			ItemRelatorioEstoque item = new ItemRelatorioEstoque();
			item.setIdProduto(produto.getId());
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

	// Util
	private String registraAuditoria(Long id, SolicitacaoEstoque objeto, String codigoEvento, String observacao) {
		SolicitacaoEstoqueAuditoria evento = new SolicitacaoEstoqueAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (SolicitacaoEstoque) ObjectCopier.copy(objeto);
			// Remove as referencias recursivas
			for (SolicitacaoEstoqueItem item : objeto.getItens()) {
				item.setSolicitacao(null);
			}

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}
}