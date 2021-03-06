package br.com.graflogic.hermitex.cliente.web.controller.produto;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.produto.CategoriaProduto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.CorProduto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoCor;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoCorPK;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoImagem;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoTamanho;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoTamanhoPK;
import br.com.graflogic.hermitex.cliente.data.entity.produto.SetorProduto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.TamanhoProduto;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.model.LinhaTabelaMedidas;
import br.com.graflogic.hermitex.cliente.service.impl.produto.CategoriaProdutoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.CorProdutoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.ProdutoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.SetorProdutoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.TamanhoProdutoService;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;
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

	@Autowired
	private TamanhoProdutoService tamanhoService;

	@Autowired
	private CorProdutoService corService;

	private List<Cliente> clientes;

	private List<CategoriaProduto> categorias;

	private List<SetorProduto> setores;

	private List<TamanhoProduto> tamanhos;

	private List<CorProduto> cores;

	private ProdutoTamanho tamanho;

	private ProdutoCor cor;

	private int indexRelacionado;

	private List<LinhaTabelaMedidas> conteudoTabelaMedidas;

	private String tabelaMedidas;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new Produto());

			clientes = clienteService.consulta(new Cliente());
			categorias = new ArrayList<>();
			setores = new ArrayList<>();

			tamanhos = tamanhoService.consulta(true);
			cores = corService.consulta(true);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected boolean executeSave() {
		try {
			if (!imagensValidas()) {
				return false;
			}

			getEntity().setConteudoTabelaMedidas(GsonUtil.gson.toJson(conteudoTabelaMedidas));

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
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar a coleção", null);
			return;
		}

		setEntity(new Produto());
		getEntity().setTamanhos(new ArrayList<>());
		getEntity().setCores(new ArrayList<>());
		getEntity().setImagens(new ArrayList<>());
		getEntity().setIdCliente(getFilterEntity().getIdCliente());

		conteudoTabelaMedidas = new ArrayList<>();
		tabelaMedidas = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void executeEdit(Produto entity) {
		setEntity(service.consultaCompletoPorId(entity.getId()));

		conteudoTabelaMedidas = GsonUtil.gson.fromJson(getEntity().getConteudoTabelaMedidas(), List.class);

		if (null == conteudoTabelaMedidas) {
			conteudoTabelaMedidas = new ArrayList<>();
		} else {
			tabelaMedidas = service.geraTabelaMedidas(getEntity().getConteudoTabelaMedidas());
		}
	}

	@Override
	protected void executeSearch() {
		if (!isClienteSelecionado()) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar a coleção", null);
			return;
		}

		setEntities(service.consulta(getFilterEntity()));
	}

	@Override
	protected void executeEditRelated(Object relatedEntity) throws Exception {
		if (relatedEntity instanceof ProdutoTamanho) {
			tamanho = (ProdutoTamanho) ObjectCopier.copy(relatedEntity);

		} else if (relatedEntity instanceof ProdutoCor) {
			cor = (ProdutoCor) ObjectCopier.copy(relatedEntity);

		}
	}

	// Tamanho
	public void prepareAddTamanho() {
		try {
			setEditingRelated(false);

			tamanho = new ProdutoTamanho();
			tamanho.setId(new ProdutoTamanhoPK());
			tamanho.setProduto(getEntity());

			showDialog("tamanhoDialog");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao adicionar tamanho, contate o administrador", t);
		}
	}

	public void saveTamanho() {
		try {
			if (isEditingRelated()) {
				getEntity().getTamanhos().set(indexRelacionado, tamanho);

			} else {
				for (ProdutoTamanho produtoTamanho : getEntity().getTamanhos()) {
					if (produtoTamanho.getId().getCodigoTamanho().equals(tamanho.getId().getCodigoTamanho())) {
						returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Tamanho já cadastrado", null);
						return;
					}
				}

				getEntity().getTamanhos().add(tamanho);
			}

			updateComponent("editForm:dtbTamanhos");
			hideDialog("tamanhoDialog");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao salvar tamanho, contate o administrador", t);
		}
	}

	public void excluiTamanho() {
		try {
			getEntity().getTamanhos().remove(indexRelacionado);

			updateComponent("editForm:dtbTamanhos");
			hideDialog("tamanhoDialog");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao excluir tamanho, contate o administrador", t);
		}
	}

	// Cor
	public void prepareAddCor() {
		try {
			setEditingRelated(false);

			cor = new ProdutoCor();
			cor.setId(new ProdutoCorPK());
			cor.setProduto(getEntity());

			showDialog("corDialog");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao adicionar cor, contate o administrador", t);
		}
	}

	public void saveCor() {
		try {
			if (isEditingRelated()) {
				getEntity().getCores().set(indexRelacionado, cor);

			} else {
				for (ProdutoCor produtoCor : getEntity().getCores()) {
					if (produtoCor.getId().getCodigoCor().equals(cor.getId().getCodigoCor())) {
						returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Cor já cadastrada", null);
						return;
					}
				}

				for (CorProduto corProduto : cores) {
					if (corProduto.getCodigo().equals(cor.getId().getCodigoCor())) {
						cor.setNomeCor(corProduto.getNome());
					}
				}

				getEntity().getCores().add(cor);
			}

			updateComponent("editForm:dtbCores");
			hideDialog("corDialog");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao salvar cor, contate o administrador", t);
		}
	}

	public void excluiCor() {
		try {
			getEntity().getCores().remove(indexRelacionado);

			updateComponent("editForm:dtbCores");
			hideDialog("corDialog");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao excluir cor, contate o administrador", t);
		}
	}

	// Tabela Medidas
	public void changeLinhasTabelaMedidas() {
		try {
			conteudoTabelaMedidas.clear();

			for (int i = 0; i < getEntity().getLinhasTabelaMedidas(); i++) {
				conteudoTabelaMedidas.add(new LinhaTabelaMedidas());
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar tabela de medidas, contate o administrador", t);
		}

	}

	// Util
	public void changeCliente() {
		try {
			setEntities(null);
			categorias.clear();
			setores.clear();

			if (isClienteSelecionado()) {
				categorias.addAll(categoriaService.consultaPorCliente(getFilterEntity().getIdCliente(), false));
				setores.addAll(setorService.consultaPorCliente(getFilterEntity().getIdCliente(), false));
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados da coleção, contate o administrador", t);
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
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inativar produto, contate o administrador", t);
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
	public void uploadImagem(FileUploadEvent event) {
		try {
			byte[] conteudoImagem = IOUtils.toByteArray(event.getFile().getInputstream());

			ProdutoImagem imagem = service.geraImagem(conteudoImagem);
			imagem.setIdProduto(getEntity().getId());

			getEntity().getImagens().add(imagem);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao realizar upload da imagem, contate o administrador", t);
		}
	}

	public StreamedContent getImagem() {
		try {
			int index = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("index"));

			ProdutoImagem imagem = getEntity().getImagens().get(index);

			byte[] conteudoArquivo = service.downloadImagem(imagem);

			return new DefaultStreamedContent(new ByteArrayInputStream(conteudoArquivo), "", imagem.getId() + ".jpg");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao realizar download da imagem, contate o administrador", t);
			return null;
		}
	}

	public void excluiImagem() {
		try {
			int index = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("index"));

			getEntity().getImagens().remove(index);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao realizar exclusão da imagem, contate o administrador", t);
		}
	}

	public void selecionaImagemCapa(Integer index, String codigoCor) {
		try {
			for (int i = 0; i < getEntity().getImagens().size(); i++) {
				ProdutoImagem imagem = getEntity().getImagens().get(i);

				if (i != index && ((StringUtils.isBlank(codigoCor) && StringUtils.isBlank(imagem.getCodigoCor()))
						|| (StringUtils.isNotBlank(codigoCor) && codigoCor.equals(imagem.getCodigoCor())))) {
					imagem.setCapa(false);
				}
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao selecionar a imagem de capa, contate o administrador", t);
		}
	}

	private boolean imagensValidas() {
		Map<String, List<ProdutoImagem>> imagensProdutoCor = getEntity().getImagens().stream()
				.collect(Collectors.groupingBy(imagem -> StringUtils.defaultString(imagem.getCodigoCor())));

		for (Entry<String, List<ProdutoImagem>> imagensCor : imagensProdutoCor.entrySet()) {
			if (imagensCor.getValue().size() > 4) {
				returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Apenas 4 imagens podem ser enviadas por produto e cor", null);
				return false;
			}
		}

		return true;
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

	public List<TamanhoProduto> getTamanhos() {
		return tamanhos;
	}

	public List<CorProduto> getCores() {
		return cores;
	}

	public ProdutoTamanho getTamanho() {
		return tamanho;
	}

	public ProdutoCor getCor() {
		return cor;
	}

	public int getIndexRelacionado() {
		return indexRelacionado;
	}

	public void setIndexRelacionado(int indexRelacionado) {
		this.indexRelacionado = indexRelacionado;
	}

	public List<LinhaTabelaMedidas> getConteudoTabelaMedidas() {
		return conteudoTabelaMedidas;
	}

	public String getTabelaMedidas() {
		return tabelaMedidas;
	}
}