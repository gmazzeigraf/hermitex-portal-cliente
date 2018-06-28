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
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Estado;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Municipio;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteContato;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteEnderecoPK;
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

	private List<Municipio> municipiosFaturamento;

	private List<Municipio> municipiosEntrega;

	private ClienteContato contato;

	private ClienteEndereco enderecoFaturamento;

	private ClienteEndereco enderecoEntrega;

	private int indexRelacionado;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new Cliente());

			estados = estadoService.consulta();
			municipiosFaturamento = new ArrayList<Municipio>();
			municipiosEntrega = new ArrayList<>();

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected boolean executeSave() {
		try {
			// TODO Enviar logotipo
			getEntity().getEnderecos().clear();
			getEntity().getEnderecos().add(enderecoFaturamento);
			getEntity().getEnderecos().add(enderecoEntrega);

			if (isEditing()) {
				try {
					service.atualiza(getEntity());

					returnInfoMessage("Cliente atualizado com sucesso", null);

				} catch (DadosDesatualizadosException e) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

				}

			} else {
				enderecoFaturamento.setCliente(getEntity());
				enderecoEntrega.setCliente(getEntity());

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
		getEntity().setContatos(new ArrayList<ClienteContato>());
		getEntity().setEnderecos(new ArrayList<ClienteEndereco>());

		contato = new ClienteContato();

		enderecoFaturamento = new ClienteEndereco(new ClienteEnderecoPK(null, DomTipoEndereco.FATURAMENTO));
		enderecoEntrega = new ClienteEndereco(new ClienteEnderecoPK(null, DomTipoEndereco.ENTREGA));
	}

	@Override
	protected void executeEdit(Cliente entity) {
		setEntity(service.consultaCompletoPorId(entity.getId()));

		for (ClienteEndereco endereco : getEntity().getEnderecos()) {
			endereco.setSiglaEstado(municipioService.consultaPorId(endereco.getIdMunicipio()).getSiglaEstado());
		}

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
		if (relatedEntity instanceof ClienteContato) {
			contato = (ClienteContato) ObjectCopier.copy(relatedEntity);

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
			enderecoEntrega = (ClienteEndereco) ObjectCopier.copy(enderecoFaturamento);
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

	public List<Estado> getEstados() {
		return estados;
	}

	public List<Municipio> getMunicipiosFaturamento() {
		return municipiosFaturamento;
	}

	public List<Municipio> getMunicipiosEntrega() {
		return municipiosEntrega;
	}

	public ClienteContato getContato() {
		return contato;
	}

	public ClienteEndereco getEnderecoFaturamento() {
		return enderecoFaturamento;
	}

	public ClienteEndereco getEnderecoEntrega() {
		return enderecoEntrega;
	}
}