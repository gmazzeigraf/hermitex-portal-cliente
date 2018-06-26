package br.com.graflogic.hermitex.cliente.web.controller.produto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.produto.CategoriaProduto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.SetorProduto;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.CategoriaProdutoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.ProdutoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.SetorProdutoService;
import br.com.graflogic.utilities.presentationutil.controller.CrudBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class ProdutoController extends CrudBaseController<Produto, Produto> implements InitializingBean {

	private static final long serialVersionUID = 3702814175660127874L;

	@Autowired
	private ProdutoService service;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private CategoriaProdutoService categoriaService;

	@Autowired
	private SetorProdutoService setorService;

	private List<Cliente> clientes;

	private List<CategoriaProduto> categorias;

	private List<SetorProduto> setores;

	private List<File> imagens;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new Produto());

			clientes = clienteService.consulta(new Cliente());
			categorias = new ArrayList<>();
			setores = new ArrayList<>();

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected boolean executeSave() {
		try {
			if (isEditing()) {
				try {
					service.atualiza(getEntity());

					returnInfoMessage("Produto atualizado com sucesso", null);

				} catch (DadosDesatualizadosException e) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

				}

			} else {
				service.cadastra(getEntity());

				returnInfoMessage("Produto cadastrado com sucesso", null);
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
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar o cliente", null);
			return;
		}

		imagens = null;

		setEntity(new Produto());
		getEntity().setIdCliente(getFilterEntity().getIdCliente());
	}

	@Override
	protected void executeEdit(Produto entity) {
		setEntity(service.consultaPorId(entity.getId()));

		consultaImagens();
	}

	@Override
	protected void executeSearch() {
		if (!isClienteSelecionado()) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar o cliente", null);
			return;
		}

		setEntities(service.consulta(getFilterEntity()));
	}

	// Util
	public void changeCliente() {
		try {
			setEntities(null);
			categorias.clear();
			setores.clear();

			if (isClienteSelecionado()) {
				categorias = categoriaService.consultaPorCliente(getFilterEntity().getIdCliente(), false);
				setores = setorService.consultaPorCliente(getFilterEntity().getIdCliente(), false);
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do cliente, contate o administrador", t);
		}
	}

	public void inativa() {
		try {
			service.inativa(getEntity());

			returnInfoMessage("Produto inativado com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao invativar produto, contate o administrador", t);
		}
	}

	public void ativa() {
		try {
			service.ativa(getEntity());

			returnInfoMessage("Produto ativado com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao ativar produto, contate o administrador", t);
		}
	}

	// Imagem
	private void consultaImagens() {
		try {
			imagens = service.getImagens(getEntity().getId());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar imagens, contate o administrador", t);
		}
	}

	public void uploadImagem(FileUploadEvent event) {
		try {
			if (imagens.size() >= 4) {
				returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Apenas 4 imagens podem ser enviadas por produto", null);
				return;
			}

			UploadedFile imagem = event.getFile();

			byte[] conteudoArquivo = IOUtils.toByteArray(imagem.getInputstream());

			String nomeArquivo = FilenameUtils.getName(imagem.getFileName());

			service.uploadImagem(getEntity().getId(), nomeArquivo, conteudoArquivo);

			consultaImagens();

			returnInfoDialogMessage(I18NUtil.getLabel("sucesso"), "Imagem enviada com sucesso");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao realizar upload da imagem, contate o administrador", t);
		}
	}

	public StreamedContent getImagem() {
		try {
			File imagem = imagens.get(Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("index")));

			byte[] conteudoArquivo = IOUtils.toByteArray(new FileInputStream(imagem));

			return new DefaultStreamedContent(new ByteArrayInputStream(conteudoArquivo), "", imagem.getName());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao realizar download da imagem, contate o administrador", t);
			return null;
		}
	}

	public void excluiImagem() {
		try {
			File imagem = imagens.get(Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("index")));

			service.excluiImagem(getEntity().getId(), imagem.getName());

			consultaImagens();

			returnInfoDialogMessage(I18NUtil.getLabel("sucesso"), "Imagem excluída com sucesso");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao realizar exclusão da imagem, contate o administrador", t);
		}
	}

	// Condicoes
	public boolean isClienteSelecionado() {
		return null != getFilterEntity().getIdCliente() && 0 != getFilterEntity().getIdCliente();
	}

	// Getters e Setters
	public List<Cliente> getClientes() {
		return clientes;
	}

	public List<CategoriaProduto> getCategorias() {
		return categorias;
	}

	public List<SetorProduto> getSetores() {
		return setores;
	}

	public List<File> getImagens() {
		return imagens;
	}
}