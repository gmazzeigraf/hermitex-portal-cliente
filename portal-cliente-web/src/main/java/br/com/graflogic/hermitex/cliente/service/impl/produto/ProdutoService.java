package br.com.graflogic.hermitex.cliente.service.impl.produto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.OptimisticLockException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaProduto;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatus;
import br.com.graflogic.hermitex.cliente.data.entity.aud.ProdutoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.hermitex.cliente.data.impl.aud.ProdutoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.ProdutoRepository;
import br.com.graflogic.hermitex.cliente.data.util.ConfiguracaoEnum;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.ConfiguracaoService;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository repository;

	@Autowired
	private ProdutoAuditoriaRepository auditoriaRepository;

	@Autowired
	private ConfiguracaoService configuracaoService;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Produto entity) {
		entity.setStatus(DomStatus.ATIVO);

		repository.store(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaProduto.CADASTRO, null);
	}

	public void atualiza(Produto entity) {
		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaProduto.ATUALIZACAO, null);
	}

	public void inativa(Produto entity) {
		entity.setStatus(DomStatus.INATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaProduto.INATIVACAO, null);
	}

	public void ativa(Produto entity) {
		entity.setStatus(DomStatus.ATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaProduto.ATIVACAO, null);
	}

	private void executaAtualiza(Produto entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Imagem
	public List<File> getImagens(Integer idProduto) {
		File diretorio = new File(getCaminhoDiretorioImagens(idProduto));

		if (null == diretorio || !diretorio.exists()) {
			return new ArrayList<>();
		}

		return Arrays.asList(diretorio.listFiles());
	}

	public void uploadImagem(Integer idProduto, String nomeArquivo, byte[] conteudoArquivo) throws FileNotFoundException, IOException {
		File diretorio = new File(getCaminhoDiretorioImagens(idProduto));

		if (!diretorio.exists()) {
			diretorio.mkdirs();
		}

		File imagem = new File(getCaminhoImagem(idProduto, nomeArquivo));

		IOUtils.write(conteudoArquivo, new FileOutputStream(imagem));

		registraAuditoria(idProduto, null, DomEventoAuditoriaProduto.UPLOAD_IMAGEM, nomeArquivo);
	}

	public void excluiImagem(Integer idProduto, String nomeArquivo) throws IOException {
		File imagem = new File(getCaminhoImagem(idProduto, nomeArquivo));

		FileUtils.forceDelete(imagem);

		registraAuditoria(idProduto, null, DomEventoAuditoriaProduto.EXCLUSAO_IMAGEM, nomeArquivo);
	}

	private String getCaminhoImagem(Integer idProduto, String nomeArquivo) {
		String caminhoImagem = getCaminhoDiretorioImagens(idProduto);

		caminhoImagem += File.separator + nomeArquivo;

		return caminhoImagem;
	}

	private String getCaminhoDiretorioImagens(Integer idProduto) {
		String caminhoDiretorio = configuracaoService.consulta(ConfiguracaoEnum.PRODUTO_DIRETORIO_IMAGENS);

		caminhoDiretorio += File.separator + idProduto;

		return caminhoDiretorio;
	}

	// Consulta
	public List<Produto> consulta(Produto entity) {
		return repository.consulta(entity);
	}

	public Produto consultaPorId(Integer id) {
		Produto entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	// Util
	private String registraAuditoria(Integer id, Produto objeto, String codigoEvento, String observacao) {
		ProdutoAuditoria evento = new ProdutoAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (Produto) ObjectCopier.copy(objeto);

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}
}