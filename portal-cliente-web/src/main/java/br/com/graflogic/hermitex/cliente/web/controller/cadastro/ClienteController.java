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
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Estado;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Municipio;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteContato;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteEndereco;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.EstadoService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.MunicipioService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
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
public class ClienteController extends CrudBaseController<Cliente, Cliente> implements InitializingBean {

	private static final long serialVersionUID = 2988142578530497646L;

	@Autowired
	private ClienteService service;

	@Autowired
	private EstadoService estadoService;

	@Autowired
	private MunicipioService municipioService;

	@Autowired
	private CEPClient cepClient;

	private List<Estado> estados;

	private List<Municipio> municipios;

	private String siglaEstado;

	private ClienteContato contato;

	private int indexRelacionado;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new Cliente());

			estados = estadoService.consulta();
			municipios = new ArrayList<Municipio>();

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected boolean executeSave() {
		try {
			// TODO Enviar logotipo

			if (isEditing()) {
				try {
					service.atualiza(getEntity());

					returnInfoMessage("Cliente atualizado com sucesso", null);

				} catch (DadosDesatualizadosException e) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

				}

			} else {
				service.cadastra(getEntity());

				returnInfoMessage("Cliente cadastrado com sucesso", null);

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
		setEntity(new Cliente());
		getEntity().setEndereco(new ClienteEndereco());
		getEntity().setContatos(new ArrayList<ClienteContato>());

		contato = new ClienteContato();

		siglaEstado = "";
	}

	@Override
	protected void executeEdit(Cliente entity) {
		setEntity(service.consultaPorId(entity.getId()));

		siglaEstado = municipioService.consultaPorId(getEntity().getEndereco().getIdMunicipio()).getSiglaEstado();

		changeEstado();
	}

	@Override
	protected void executeSearch() {
		setEntities(service.consulta(getFilterEntity()));
	}

	@Override
	protected void executeEditRelated(Object relatedEntity) throws Exception {
		if (relatedEntity instanceof ClienteContato) {
			contato = (ClienteContato) ObjectCopier.copy(relatedEntity);

		}
	}

	// Endereco
	public void changeCep() {
		try {
			Endereco endereco = cepClient.consulta(getEntity().getEndereco().getCep());

			try {
				Municipio municipio = municipioService.consultaPorNomeEstado(StringUtil.removerAcentos(endereco.getCidade()).toUpperCase(),
						endereco.getUf().toUpperCase());

				siglaEstado = municipio.getSiglaEstado();
				changeEstado();

				getEntity().getEndereco().setIdMunicipio(municipio.getId());

			} catch (ResultadoNaoEncontradoException e) {
				limpaEndereco();
				siglaEstado = estadoService.consultaPorSigla(endereco.getUf().toUpperCase()).getSigla();
				changeEstado();
			}

			getEntity().getEndereco()
					.setLogradouro(endereco.getLogradouro().length() > 100 ? endereco.getLogradouro().substring(0, 100) : endereco.getLogradouro());
			getEntity().getEndereco().setBairro(endereco.getBairro().length() > 100 ? endereco.getBairro().substring(0, 100) : endereco.getBairro());

		} catch (InvalidCEPException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "CEP inválido", null);
			limpaEndereco();
			getEntity().getEndereco().setCep(null);

		} catch (CEPNotFoundException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "CEP não encontrado", null);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar CEP, contate o administrador", t);
		}
	}

	private void limpaEndereco() {
		siglaEstado = null;
		getEntity().getEndereco().setIdMunicipio(null);
		getEntity().getEndereco().setBairro(null);
		getEntity().getEndereco().setLogradouro(null);
		getEntity().getEndereco().setNumero(null);
		getEntity().getEndereco().setComplemento(null);
	}

	public void changeEstado() {
		try {
			municipios.clear();

			if (StringUtils.isNotEmpty(siglaEstado)) {
				municipios.addAll(municipioService.consulta(siglaEstado));
			}
		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do estado, contate o administrador", t);
		}
	}

	// Contato
	public void prepareAddContato() {
		try {
			setEditingRelated(false);

			contato = new ClienteContato();

			contato.setIdCliente(getEntity().getId());

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
	public void inativa() {
		try {
			service.inativa(getEntity());

			returnInfoMessage("Cliente inativado com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao invativar cliente, contate o administrador", t);
		}
	}

	public void ativa() {
		try {
			service.ativa(getEntity());

			returnInfoMessage("Cliente ativado com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao ativar cliente, contate o administrador", t);
		}
	}

	// Getters e Setters
	public int getIndexRelacionado() {
		return indexRelacionado;
	}

	public void setIndexRelacionado(int indexRelacionado) {
		this.indexRelacionado = indexRelacionado;
	}

	public String getSiglaEstado() {
		return siglaEstado;
	}

	public void setSiglaEstado(String siglaEstado) {
		this.siglaEstado = siglaEstado;
	}

	public ClienteContato getContato() {
		return contato;
	}

	public List<Estado> getEstados() {
		return estados;
	}

	public List<Municipio> getMunicipios() {
		return municipios;
	}
}