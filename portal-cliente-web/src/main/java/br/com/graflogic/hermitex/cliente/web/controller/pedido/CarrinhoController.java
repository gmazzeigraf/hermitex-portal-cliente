package br.com.graflogic.hermitex.cliente.web.controller.pedido;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.utilities.presentationutil.controller.BaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("session")
public class CarrinhoController extends BaseController implements InitializingBean {

	private static final long serialVersionUID = 1141659331755233586L;

	private Pedido pedido;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			pedido = new Pedido();
			pedido.setItens(new ArrayList<>());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao iniciar a sessão, contate o administrador", t);
		}
	}

	public void adicionaItem(PedidoItem item) {
		// Verifica se o pedido e tamanho ja estao no carrinho
		for (PedidoItem itemPedido : pedido.getItens()) {
			if (itemPedido.getIdProduto().equals(item.getIdProduto()) && itemPedido.getCodigoTamanho().equals(item.getCodigoTamanho())) {
				throw new DadosInvalidosException("Item e tamanho já adicionados no carrinho");
			}
		}

		pedido.getItens().add(item);

		atualizaPedido();
	}

	public void excluiItem(Integer index) {
		try {
			pedido.getItens().remove(index.intValue());

			atualizaPedido();

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao remover item, contate o administrador", t);
		}
	}

	private void atualizaPedido() {
		pedido.setValorTotal(BigDecimal.ZERO);

		for (PedidoItem item : pedido.getItens()) {
			pedido.setValorTotal(pedido.getValorTotal().add(item.getValorTotal()));
		}
	}

	// Getters e Setters
	public Pedido getPedido() {
		return pedido;
	}
}