package br.com.graflogic.hermitex.cliente.web.controller.cadastro;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.commonutil.util.StringUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomPermissaoAcesso;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomStatusFilial;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoEndereco;
import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.Usuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioProprietario;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Estado;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Municipio;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.FilialContato;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.FilialEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.FilialEnderecoPK;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.UsuarioService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.EstadoService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.MunicipioService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.web.controller.SessionController;
import br.com.graflogic.hermitex.cliente.web.controller.pedido.CarrinhoController;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.cep.CEPClient;
import br.com.graflogic.utilities.cep.exception.CEPNotFoundException;
import br.com.graflogic.utilities.cep.exception.InvalidCEPException;
import br.com.graflogic.utilities.cep.model.Endereco;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;
import br.com.graflogic.utilities.presentationutil.controller.CrudBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class FilialController extends CrudBaseController<Filial, Filial> implements InitializingBean {

	private static final long serialVersionUID = 7729256747491771236L;

	@Autowired
	private FilialService service;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EstadoService estadoService;

	@Autowired
	private MunicipioService municipioService;

	@Autowired
	private CEPClient cepClient;

	@Autowired
	private SessionController sessionController;

	@Autowired
	private CarrinhoController carrinhoController;

	private List<Cliente> clientes;

	private List<Usuario> usuariosProprietarios;

	private List<Estado> estados;

	private List<Municipio> municipiosFaturamento;

	private List<Municipio> municipiosEntrega;

	private FilialContato contato;

	private FilialEndereco enderecoFaturamento;

	private FilialEndereco enderecoEntrega;

	private int indexRelacionado;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new Filial());

			// Verifica se e proprietario e precisa selecionar a filial
			if (SessionUtil.isUsuarioProprietario() && isView("pages/filial/seleciona")) {
				getFilterEntity().setIdUsuarioProprietario(SessionUtil.getAuthenticatedUsuario().getId());
				getFilterEntity().setStatus(DomStatusFilial.ATIVO);

				search();

				setEntity(new Filial());

			} else {
				estados = estadoService.consulta();
				municipiosFaturamento = new ArrayList<Municipio>();
				municipiosEntrega = new ArrayList<>();

				if (SessionUtil.isUsuarioAdministrador()) {
					clientes = clienteService.consulta(new Cliente());

				} else if (SessionUtil.isUsuarioCliente()) {
					getFilterEntity().setIdCliente(SessionUtil.getIdCliente());

				}

				usuariosProprietarios = usuarioService.consulta(new UsuarioProprietario());
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected boolean executeSave() {
		try {
			getEntity().getEnderecos().clear();
			getEntity().getEnderecos().add(enderecoFaturamento);
			getEntity().getEnderecos().add(enderecoEntrega);

			if (isEditing()) {
				try {
					service.atualiza(getEntity());

					returnInfoMessage("Filial atualizada com sucesso", null);

				} catch (DadosDesatualizadosException e) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

				}

			} else {
				enderecoFaturamento.setFilial(getEntity());
				enderecoEntrega.setFilial(getEntity());

				service.cadastra(getEntity());

				returnInfoMessage("Filial cadastrada com sucesso", null);

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
		setEntity(new Filial());
		getEntity().setContatos(new ArrayList<FilialContato>());
		getEntity().setEnderecos(new ArrayList<FilialEndereco>());
		getEntity().setIdCliente(getFilterEntity().getIdCliente());

		contato = new FilialContato();

		enderecoFaturamento = new FilialEndereco(new FilialEnderecoPK(null, DomTipoEndereco.FATURAMENTO));
		enderecoEntrega = new FilialEndereco(new FilialEnderecoPK(null, DomTipoEndereco.ENTREGA));

		changeEstadoFaturamento();
		changeEstadoEntrega();

		getEntity().setCompraBloqueada(DomBoolean.NAO);
		getEntity().setSemCredito(DomBoolean.NAO);
	}

	@Override
	protected void executeEdit(Filial entity) {
		setEntity(service.consultaPorId(entity.getId()));

		enderecoFaturamento = getEntity().getEnderecoFaturamento();
		enderecoEntrega = getEntity().getEnderecoEntrega();

		changeEstadoFaturamento();
		changeEstadoEntrega();
	}

	@Override
	protected void executeSearch() {
		setEntities(service.consulta(getFilterEntity()));
	}

	@Override
	protected void executeEditRelated(Object relatedEntity) throws Exception {
		if (relatedEntity instanceof FilialContato) {
			contato = (FilialContato) ObjectCopier.copy(relatedEntity);

		}
	}

	// Enderecos
	// Faturamento
	public void changeCepFaturamento() {
		try {
			Endereco endereco = cepClient.consulta(enderecoFaturamento.getCep());

			try {
				Municipio municipio = municipioService.consultaPorNomeEstado(StringUtil.removerAcentos(endereco.getCidade()).toUpperCase(),
						endereco.getUf().toUpperCase());

				enderecoFaturamento.setSiglaEstado(municipio.getSiglaEstado());

				changeEstadoFaturamento();

				enderecoFaturamento.setIdMunicipio(municipio.getId());

			} catch (ResultadoNaoEncontradoException e) {
				limpaEnderecoFaturamento();

				enderecoFaturamento.setSiglaEstado(estadoService.consultaPorSigla(endereco.getUf().toUpperCase()).getSigla());

				changeEstadoFaturamento();
			}

			enderecoFaturamento
					.setLogradouro(endereco.getLogradouro().length() > 100 ? endereco.getLogradouro().substring(0, 100) : endereco.getLogradouro());
			enderecoFaturamento.setBairro(endereco.getBairro().length() > 100 ? endereco.getBairro().substring(0, 100) : endereco.getBairro());

		} catch (InvalidCEPException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "CEP inválido", null);

			limpaEnderecoFaturamento();

			enderecoFaturamento.setCep(null);

		} catch (CEPNotFoundException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "CEP não encontrado", null);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar CEP, contate o administrador", t);
		}
	}

	private void limpaEnderecoFaturamento() {
		enderecoFaturamento.setIdMunicipio(null);
		enderecoFaturamento.setBairro(null);
		enderecoFaturamento.setLogradouro(null);
		enderecoFaturamento.setNumero(null);
		enderecoFaturamento.setComplemento(null);
	}

	public void changeEstadoFaturamento() {
		try {
			municipiosFaturamento.clear();

			if (StringUtils.isNotEmpty(enderecoFaturamento.getSiglaEstado())) {
				municipiosFaturamento.addAll(municipioService.consulta(enderecoFaturamento.getSiglaEstado()));
			}
		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do estado, contate o administrador", t);
		}
	}

	public void copiaEnderecoFaturamento() {
		try {
			enderecoEntrega = (FilialEndereco) ObjectCopier.copy(enderecoFaturamento);
			enderecoEntrega.getId().setTipo(DomTipoEndereco.ENTREGA);

			changeEstadoEntrega();

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao copiar endereço, contate o administrador", t);
		}
	}

	// Entrega
	public void changeCepEntrega() {
		try {
			Endereco endereco = cepClient.consulta(enderecoEntrega.getCep());

			try {
				Municipio municipio = municipioService.consultaPorNomeEstado(StringUtil.removerAcentos(endereco.getCidade()).toUpperCase(),
						endereco.getUf().toUpperCase());

				enderecoEntrega.setSiglaEstado(municipio.getSiglaEstado());

				changeEstadoEntrega();

				enderecoEntrega.setIdMunicipio(municipio.getId());

			} catch (ResultadoNaoEncontradoException e) {
				limpaEnderecoEntrega();

				enderecoEntrega.setSiglaEstado(estadoService.consultaPorSigla(endereco.getUf().toUpperCase()).getSigla());

				changeEstadoEntrega();
			}

			enderecoEntrega
					.setLogradouro(endereco.getLogradouro().length() > 100 ? endereco.getLogradouro().substring(0, 100) : endereco.getLogradouro());
			enderecoEntrega.setBairro(endereco.getBairro().length() > 100 ? endereco.getBairro().substring(0, 100) : endereco.getBairro());

		} catch (InvalidCEPException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "CEP inválido", null);

			limpaEnderecoEntrega();

			enderecoEntrega.setCep(null);

		} catch (CEPNotFoundException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "CEP não encontrado", null);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar CEP, contate o administrador", t);
		}
	}

	private void limpaEnderecoEntrega() {
		enderecoEntrega.setIdMunicipio(null);
		enderecoEntrega.setBairro(null);
		enderecoEntrega.setLogradouro(null);
		enderecoEntrega.setNumero(null);
		enderecoEntrega.setComplemento(null);
	}

	public void changeEstadoEntrega() {
		try {
			municipiosEntrega.clear();

			if (StringUtils.isNotEmpty(enderecoEntrega.getSiglaEstado())) {
				municipiosEntrega.addAll(municipioService.consulta(enderecoEntrega.getSiglaEstado()));
			}
		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do estado, contate o administrador", t);
		}
	}

	// Contato
	public void prepareAddContato() {
		try {
			setEditingRelated(false);

			contato = new FilialContato();

			contato.setIdFilial(getEntity().getId());

			showDialog("contatoDialog");
		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao adicionar contato, contate o administrador", t);
		}
	}

	public void saveContato() {
		try {
			if (isEditingRelated()) {
				getEntity().getContatos().set(indexRelacionado, contato);

			} else {
				getEntity().getContatos().add(contato);
				setEditingRelated(true);
			}

			updateComponent("editForm:dtbContatos");
			hideDialog("contatoDialog");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao salvar contato, contate o administrador", t);
		}
	}

	public void excluiContato() {
		try {
			getEntity().getContatos().remove(indexRelacionado);

			updateComponent("editForm:dtbContatos");
			hideDialog("contatoDialog");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao excluir contato, contate o administrador", t);
		}
	}

	// Util
	public void changeCliente() {
		try {
			setEntities(null);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do cliente, contate o administrador", t);
		}
	}

	public void inativa() {
		try {
			service.inativa(getEntity());

			returnInfoMessage("Filial inativada com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inativar filial, contate o administrador", t);
		}
	}

	public void ativa() {
		try {
			service.ativa(getEntity());

			returnInfoMessage("Filial ativada com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao ativar filial, contate o administrador", t);
		}
	}

	public void bloqueiaCompra() {
		try {
			service.bloqueiaCompra(getEntity());

			returnInfoMessage("Compra bloqueada com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao bloquear compra, contate o administrador", t);
		}
	}

	public void desbloqueiaCompra() {
		try {
			service.desbloqueiaCompra(getEntity());

			returnInfoMessage("Compra desbloqueada com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao desbloquear compra, contate o administrador", t);
		}
	}

	public void seleciona() {
		try {
			if (null == getEntity().getId() || 0 == getEntity().getId()) {
				returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar a filial", null);
				return;
			}

			Filial filial = service.consultaPorId(getEntity().getId());

			SessionUtil.getAuthenticatedUser().setEmpresa(filial);

			sessionController.carregaPermissoes();

			carrinhoController.afterPropertiesSet();

			redirectView(getApplicationUrl() + "/pages/home.jsf");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao selecionar filial, contate o administrador", t);
		}
	}

	// Condicoes
	public boolean isClienteSelecionado() {
		return null != getFilterEntity().getIdCliente() && 0 != getFilterEntity().getIdCliente();
	}

	public boolean isCompraBloqueavel() {
		return isEditing() && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_FIL_CADASTRO_BLOQUEIO_COMPRA)
				&& DomBoolean.NAO.equals(getEntity().getCompraBloqueada());
	}

	public boolean isCompraDesbloqueavel() {
		return isEditing() && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_FIL_CADASTRO_BLOQUEIO_COMPRA)
				&& DomBoolean.SIM.equals(getEntity().getCompraBloqueada());
	}

	// Getters e Setters
	public int getIndexRelacionado() {
		return indexRelacionado;
	}

	public void setIndexRelacionado(int indexRelacionado) {
		this.indexRelacionado = indexRelacionado;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public List<Usuario> getUsuariosProprietarios() {
		return usuariosProprietarios;
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

	public FilialContato getContato() {
		return contato;
	}

	public FilialEndereco getEnderecoFaturamento() {
		return enderecoFaturamento;
	}

	public FilialEndereco getEnderecoEntrega() {
		return enderecoEntrega;
	}
}