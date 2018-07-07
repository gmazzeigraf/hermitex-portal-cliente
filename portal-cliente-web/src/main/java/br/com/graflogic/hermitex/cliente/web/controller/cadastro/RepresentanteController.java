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
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Representante;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.RepresentanteContato;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.RepresentanteEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.RepresentanteEnderecoPK;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.EstadoService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.MunicipioService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.RepresentanteService;
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
public class RepresentanteController extends CrudBaseController<Representante, Representante> implements InitializingBean {

	private static final long serialVersionUID = 2988142578530497646L;

	@Autowired
	private RepresentanteService service;

	@Autowired
	private EstadoService estadoService;

	@Autowired
	private MunicipioService municipioService;

	@Autowired
	private CEPClient cepClient;

	private List<Estado> estadosCadastro;

	private List<Municipio> municipiosCadastro;

	private RepresentanteContato contato;

	private RepresentanteEndereco enderecoCadastro;

	private int indexRelacionado;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new Representante());

			estadosCadastro = estadoService.consulta();
			municipiosCadastro = new ArrayList<Municipio>();

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected boolean executeSave() {
		try {
			getEntity().getEnderecos().clear();
			getEntity().getEnderecos().add(enderecoCadastro);

			if (isEditing()) {
				try {
					service.atualiza(getEntity());

					returnInfoMessage("Representante atualizado com sucesso", null);

				} catch (DadosDesatualizadosException e) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

				}

			} else {
				enderecoCadastro.setRepresentante(getEntity());

				service.cadastra(getEntity());

				returnInfoMessage("Representante cadastrado com sucesso", null);

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
		setEntity(new Representante());
		getEntity().setContatos(new ArrayList<RepresentanteContato>());
		getEntity().setEnderecos(new ArrayList<RepresentanteEndereco>());

		contato = new RepresentanteContato();

		enderecoCadastro = new RepresentanteEndereco(new RepresentanteEnderecoPK(null, DomTipoEndereco.CADASTRO));

		changeEstadoCadastro();
	}

	@Override
	protected void executeEdit(Representante entity) {
		setEntity(service.consultaCompletoPorId(entity.getId()));

		enderecoCadastro = getEntity().getEnderecoCadastro();

		changeEstadoCadastro();
	}

	@Override
	protected void executeSearch() {
		setEntities(service.consulta(getFilterEntity()));
	}

	@Override
	protected void executeEditRelated(Object relatedEntity) throws Exception {
		if (relatedEntity instanceof RepresentanteContato) {
			contato = (RepresentanteContato) ObjectCopier.copy(relatedEntity);

		}
	}

	// Enderecos
	public void changeCepCadastro() {
		try {
			Endereco endereco = cepClient.consulta(enderecoCadastro.getCep());

			try {
				Municipio municipio = municipioService.consultaPorNomeEstado(StringUtil.removerAcentos(endereco.getCidade()).toUpperCase(),
						endereco.getUf().toUpperCase());

				enderecoCadastro.setSiglaEstado(municipio.getSiglaEstado());

				changeEstadoCadastro();

				enderecoCadastro.setIdMunicipio(municipio.getId());

			} catch (ResultadoNaoEncontradoException e) {
				limpaEnderecoCadastro();

				enderecoCadastro.setSiglaEstado(estadoService.consultaPorSigla(endereco.getUf().toUpperCase()).getSigla());

				changeEstadoCadastro();
			}

			enderecoCadastro
					.setLogradouro(endereco.getLogradouro().length() > 100 ? endereco.getLogradouro().substring(0, 100) : endereco.getLogradouro());
			enderecoCadastro.setBairro(endereco.getBairro().length() > 100 ? endereco.getBairro().substring(0, 100) : endereco.getBairro());

		} catch (InvalidCEPException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "CEP inválido", null);

			limpaEnderecoCadastro();

			enderecoCadastro.setCep(null);

		} catch (CEPNotFoundException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "CEP não encontrado", null);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar CEP, contate o administrador", t);
		}
	}

	private void limpaEnderecoCadastro() {
		enderecoCadastro.setIdMunicipio(null);
		enderecoCadastro.setBairro(null);
		enderecoCadastro.setLogradouro(null);
		enderecoCadastro.setNumero(null);
		enderecoCadastro.setComplemento(null);
	}

	public void changeEstadoCadastro() {
		try {
			municipiosCadastro.clear();

			if (StringUtils.isNotEmpty(enderecoCadastro.getSiglaEstado())) {
				municipiosCadastro.addAll(municipioService.consulta(enderecoCadastro.getSiglaEstado()));
			}
		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do estado, contate o administrador", t);
		}
	}

	// Contato
	public void prepareAddContato() {
		try {
			setEditingRelated(false);

			contato = new RepresentanteContato();

			contato.setIdRepresentante(getEntity().getId());

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

			returnInfoMessage("Representante inativado com sucesso", null);

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

			returnInfoMessage("Representante ativado com sucesso", null);

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

	public List<Estado> getEstadosCadastro() {
		return estadosCadastro;
	}

	public List<Municipio> getMunicipiosCadastro() {
		return municipiosCadastro;
	}

	public RepresentanteContato getContato() {
		return contato;
	}

	public RepresentanteEndereco getEnderecoCadastro() {
		return enderecoCadastro;
	}
}