package br.com.graflogic.hermitex.cliente.web.controller.pedido;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomTipoFormaPagamento;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.produto.FormaPagamento;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.FormaPagamentoService;
import br.com.graflogic.utilities.presentationutil.controller.CrudBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class FormaPagamentoController extends CrudBaseController<FormaPagamento, FormaPagamento> implements InitializingBean {

	private static final long serialVersionUID = -8708890694781742592L;

	@Autowired
	private FormaPagamentoService service;

	@Autowired
	private ClienteService clienteService;

	private List<Cliente> clientes;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new FormaPagamento());

			clientes = clienteService.consulta(new Cliente());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected boolean executeSave() {
		try {
			if (DomTipoFormaPagamento.BOLETO.equals(getEntity().getTipo())) {
				String[] dias = getEntity().getConfiguracao().split(";");

				if (dias.length != getEntity().getQuantidadeParcelas()) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "A quantidade de dias do(s) boleto(s) deve ser a mesma de parcelas", null);
					return false;
				}

				for (String dia : dias) {
					if (!StringUtils.isNumeric(dia)) {
						returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "O dia " + dia + " deve ser numérico", null);
						return false;

					} else {
						Long quantidadeDias = Long.parseLong(dia);

						if (120 < quantidadeDias) {
							returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "A quantidade de dias " + dia + " deve ser no máximo 120", null);
							return false;
						}
					}
				}
			}

			if (isEditing()) {
				try {
					service.atualiza(getEntity());

					returnInfoMessage("Forma Pagamento atualizada com sucesso", null);

				} catch (DadosDesatualizadosException e) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

				}

			} else {
				service.cadastra(getEntity());

				returnInfoMessage("Forma Pagamento cadastrada com sucesso", null);
			}
		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);
			return false;
		}

		return true;
	}

	@Override
	protected void afterSave() {
		edit(getEntity());
	}

	@Override
	protected void beforeAdd() {
		if (!isClienteSelecionado()) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar a coleção", null);
			return;
		}

		setEntity(new FormaPagamento());
		getEntity().setIdCliente(getFilterEntity().getIdCliente());

		getEntity().setQuantidadeParcelas(1);
		getEntity().setPorcentagemDesconto(BigDecimal.ZERO);
		getEntity().setValorPedidoMinimo(BigDecimal.ZERO);
	}

	@Override
	protected void executeEdit(FormaPagamento entity) {
		setEntity(service.consultaPorId(entity.getId()));
	}

	@Override
	protected void executeSearch() {
		if (!isClienteSelecionado()) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar a coleção", null);
			return;
		}

		setEntities(service.consulta(getFilterEntity()));
	}

	// Util
	public void changeCliente() {
		try {
			setEntities(null);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados da coleção, contate o administrador", t);
		}
	}

	public void changeTipo() {
		try {
			getEntity().setConfiguracao("");
			getEntity().setQuantidadeParcelas(1);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar tipo, contate o administrador", t);
		}
	}

	public void inativa() {
		try {
			service.inativa(getEntity());

			returnInfoMessage("Forma Pagamento inativada com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inativar forma pagamento, contate o administrador", t);
		}
	}

	public void ativa() {
		try {
			service.ativa(getEntity());

			returnInfoMessage("Forma Pagamento ativada com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao ativar forma pagamento, contate o administrador", t);
		}
	}

	// Condicoes
	public boolean isClienteSelecionado() {
		return null != getFilterEntity().getIdCliente() && 0 != getFilterEntity().getIdCliente();
	}

	public boolean isQuantidadeParcelasHabilitada() {
		if (getEntity().isBoleto() || getEntity().isCartaoCredito()) {
			return true;
		}

		return false;
	}

	// Getters e Setters
	public List<Cliente> getClientes() {
		return clientes;
	}
}