package br.com.graflogic.hermitex.cliente.web.controller.produto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomPermissaoAcesso;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.Usuario;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Estado;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Municipio;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.JanelaCompra;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoSimple;
import br.com.graflogic.hermitex.cliente.data.entity.produto.FormaPagamento;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.UsuarioService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.EstadoService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.MunicipioService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.JanelaCompraService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.PedidoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.FormaPagamentoService;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.presentationutil.controller.SearchBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class PedidoController extends SearchBaseController<PedidoSimple, Pedido> implements InitializingBean {

	private static final long serialVersionUID = 3702814175660127874L;

	@Autowired
	private PedidoService service;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private JanelaCompraService janelaCompraService;

	@Autowired
	private EstadoService estadoService;

	@Autowired
	private MunicipioService municipioService;

	@Autowired
	private FormaPagamentoService formaPagamentoService;

	@Autowired
	private UsuarioService usuarioService;

	private List<Cliente> clientes;

	private List<Filial> filiais;

	private List<JanelaCompra> janelasCompra;

	private List<Estado> estados;

	private List<Municipio> municipiosFaturamento;

	private List<Municipio> municipiosEntrega;

	private PedidoSimple entitySimple;

	private PedidoEndereco enderecoFaturamento;

	private PedidoEndereco enderecoEntrega;

	private FormaPagamento formaPagamento;

	private String observacao;

	private Cliente cliente;

	private Filial filial;

	private Usuario usuarioCadastro;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new PedidoSimple());

			filiais = new ArrayList<>();

			janelasCompra = new ArrayList<>();

			getFilterEntity().setIdCliente(SessionUtil.getIdCliente());

			if (SessionUtil.isUsuarioAdministrador()) {
				clientes = clienteService.consulta(new Cliente());

			} else if (SessionUtil.isUsuarioFilial() || SessionUtil.isUsuarioProprietario()) {
				getFilterEntity().setIdFilial(SessionUtil.getIdFilial());

			} else if (SessionUtil.isUsuarioRepresentante()) {
				clientes = clienteService.consultaPorRepresentante(SessionUtil.getIdRepresentante());

			}

			if (null != getFilterEntity().getIdCliente()) {
				changeCliente();
			}

			estados = estadoService.consulta();
			municipiosFaturamento = new ArrayList<Municipio>();
			municipiosEntrega = new ArrayList<>();

			if (SessionUtil.isUsuarioFilial() || SessionUtil.isUsuarioProprietario()) {
				search();
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected void executeSearch() {
		if (!isClienteSelecionado()) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar o cliente", null);
			return;
		}

		setEntities(service.consulta(getFilterEntity()));
	}

	@Override
	protected void select(PedidoSimple entity) {
		entitySimple = entity;

		select(entity.getId());

		// Consulta o usuario de cadastro
		usuarioCadastro = usuarioService.consultaPorId(entity.getIdUsuarioCadastro());
	}

	private void select(Long id) {
		setEntity(service.consultaCompletoPorId(id));

		municipiosFaturamento.clear();
		municipiosEntrega.clear();

		enderecoFaturamento = getEntity().getEnderecoFaturamento();
		enderecoEntrega = getEntity().getEnderecoEntrega();

		municipiosFaturamento.addAll(municipioService.consulta(enderecoFaturamento.getSiglaEstado()));
		municipiosEntrega.addAll(municipioService.consulta(enderecoEntrega.getSiglaEstado()));

		formaPagamento = formaPagamentoService.consultaPorId(getEntity().getIdFormaPagamento());

		cliente = clienteService.consultaPorId(getEntity().getIdCliente());

		if (null != getEntity().getIdFilial() && 0 != getEntity().getIdFilial()) {
			filial = filialService.consultaPorId(getEntity().getIdFilial());
		} else {
			filial = null;
		}
	}

	// Fluxo
	public void baixaPagamento() {
		try {
			service.paga(getEntity(), SessionUtil.getAuthenticatedUsuario().getId(), observacao);

			returnInfoDialogMessage(I18NUtil.getLabel("sucesso"), "Pedido marcado como pago com sucesso");

			afterOperacao();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Pedido com dados desatualizados, altere novamente", null);

			select(getEntity().getId());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao marcar pedido como pago, contate o administrador", t);
		}
	}

	public void envia() {
		try {
			service.envia(getEntity(), SessionUtil.getAuthenticatedUsuario().getId(), observacao);

			returnInfoDialogMessage(I18NUtil.getLabel("sucesso"), "Pedido marcado como enviado com sucesso");

			afterOperacao();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Pedido com dados desatualizados, altere novamente", null);

			select(getEntity().getId());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao marcar pedido como enviado, contate o administrador", t);
		}
	}

	public void finaliza() {
		try {
			service.finaliza(getEntity(), SessionUtil.getAuthenticatedUsuario().getId(), observacao);

			returnInfoDialogMessage(I18NUtil.getLabel("sucesso"), "Pedido marcado como finalizado com sucesso");

			afterOperacao();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Pedido com dados desatualizados, altere novamente", null);

			select(getEntity().getId());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao marcar pedido como finalizado, contate o administrador", t);
		}
	}

	public void cancela() {
		try {
			service.cancela(getEntity(), SessionUtil.getAuthenticatedUsuario().getId(), observacao);

			returnInfoDialogMessage(I18NUtil.getLabel("sucesso"), "Pedido marcado como cancelado com sucesso");

			afterOperacao();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Pedido com dados desatualizados, altere novamente", null);

			select(getEntity().getId());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao marcar pedido como cancelado, contate o administrador", t);
		}
	}

	private void afterOperacao() {
		select(getEntity().getId());
		executeSearch();

		updateComponent("editForm");
		updateComponent("searchForm");
		observacao = null;
	}

	// Util
	public void changeCliente() {
		try {
			setEntities(null);

			if (!SessionUtil.isUsuarioFilial() && !SessionUtil.isUsuarioProprietario()) {
				getFilterEntity().setIdFilial(null);
			}

			filiais.clear();
			janelasCompra.clear();

			if (isClienteSelecionado()) {
				if (!SessionUtil.isUsuarioFilial() && !SessionUtil.isUsuarioProprietario()) {
					filiais.addAll(filialService.consultaPorCliente(getFilterEntity().getIdCliente(), false));
				}

				if (SessionUtil.isUsuarioAdministrador()) {
					janelasCompra.addAll(janelaCompraService.consultaPorCliente(getFilterEntity().getIdCliente()));
				}
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do cliente, contate o administrador", t);
		}
	}

	// Condicoes
	public boolean isClienteSelecionado() {
		return null != getFilterEntity().getIdCliente() && 0 != getFilterEntity().getIdCliente();
	}

	public boolean isPagamentoBaixavel() {
		return null != getEntity() && getEntity().isPagamentoPendente()
				&& SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_PEDIDO_BAIXA_PAGAMENTO);
	}

	public boolean isEnviavel() {
		return null != getEntity() && getEntity().isPago() && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_PEDIDO_ENVIO);
	}

	public boolean isFinalizavel() {
		return null != getEntity() && getEntity().isEnviado() && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_PEDIDO_FINALIZACAO);
	}

	public boolean isCancelavel() {
		return null != getEntity() && (getEntity().isPagamentoPendente() || getEntity().isPago())
				&& SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_PEDIDO_CANCELAMENTO);
	}

	public boolean isPesquisavel() {
		return isClienteSelecionado();
	}

	// Getters e Setters
	public List<Cliente> getClientes() {
		return clientes;
	}

	public List<Filial> getFiliais() {
		return filiais;
	}

	public List<JanelaCompra> getJanelasCompra() {
		return janelasCompra;
	}

	public List<Estado> getEstados() {
		return estados;
	}

	public List<Municipio> getMunicipiosFaturamento() {
		return municipiosFaturamento;
	}

	public List<Municipio> getMunicipiosEntrega() {
		return municipiosEntrega;
	}

	public PedidoSimple getEntitySimple() {
		return entitySimple;
	}

	public PedidoEndereco getEnderecoFaturamento() {
		return enderecoFaturamento;
	}

	public PedidoEndereco getEnderecoEntrega() {
		return enderecoEntrega;
	}

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public Filial getFilial() {
		return filial;
	}

	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}
}