package br.com.graflogic.hermitex.cliente.service.impl.produto;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.base.service.util.CacheUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaProduto;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatus;
import br.com.graflogic.hermitex.cliente.data.entity.aud.ProdutoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoApresentacaoLista;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoImagem;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoTamanho;
import br.com.graflogic.hermitex.cliente.data.impl.aud.ProdutoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.ProdutoImagemRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.ProdutoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.ProdutoTamanhoRepository;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class ProdutoService {

	private static final String IMAGENS_CACHE_NAME = "imagensProduto";

	@Autowired
	private ProdutoRepository repository;

	@Autowired
	private ProdutoTamanhoRepository tamanhoRepository;

	@Autowired
	private ProdutoImagemRepository imagemRepository;

	@Autowired
	private ProdutoAuditoriaRepository auditoriaRepository;

	@Autowired
	private CacheUtil cacheUtil;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Produto entity) {
		validaDados(entity);

		entity.setStatus(DomStatus.ATIVO);

		List<ProdutoImagem> imagens = entity.getImagens();
		entity.setImagens(null);

		try {
			try {
				repository.consultaPorClienteCodigo(entity.getIdCliente(), entity.getCodigo());

				throw new DadosInvalidosException("Código já cadastrado");

			} catch (NoResultException e) {
			}

			repository.store(entity);

			for (ProdutoImagem imagem : imagens) {
				imagem.setIdProduto(entity.getId());
			}

			entity.setImagens(imagens);

			repository.update(entity);

			registraAuditoria(entity.getId(), entity, DomEventoAuditoriaProduto.CADASTRO, null);

		} catch (Throwable t) {
			entity.setImagens(imagens);

			throw t;
		}
	}

	public void atualiza(Produto entity) {
		validaDados(entity);

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
	public ProdutoImagem consultaImagem(String idImagem) {
		Object cacheObj = cacheUtil.findOnCache(IMAGENS_CACHE_NAME, idImagem);

		if (null == cacheObj) {
			// Consulta
			ProdutoImagem imagem = imagemRepository.findById(idImagem);

			if (null == imagem) {
				throw new ResultadoNaoEncontradoException();
			}

			// Atualiza o cache
			cacheUtil.putOnCache(IMAGENS_CACHE_NAME, idImagem, ObjectCopier.copy(imagem));

			cacheObj = cacheUtil.findOnCache(IMAGENS_CACHE_NAME, idImagem);
		}

		return (ProdutoImagem) ObjectCopier.copy(cacheObj);
	}

	public ProdutoImagem geraImagem(byte[] conteudo) {
		ProdutoImagem imagem = new ProdutoImagem();
		imagem.setId(UUID.randomUUID().toString());
		imagem.setConteudo(Base64.encodeBase64String(conteudo));
		imagem.setCapa(false);
		imagem.setTemporaria(true);

		return imagem;
	}

	public byte[] downloadImagem(String idImagem) throws IOException {
		try {
			ProdutoImagem imagem = consultaImagem(idImagem);

			return downloadImagem(imagem);

		} catch (ResultadoNaoEncontradoException e) {
			return null;
		}
	}

	public byte[] downloadImagem(ProdutoImagem imagem) {
		return Base64.decodeBase64(imagem.getConteudo());
	}

	// Consulta
	public List<Produto> consulta(Produto entity) {
		return repository.consulta(entity);
	}

	public List<ProdutoApresentacaoLista> consultaApresentacaoLista(ProdutoApresentacaoLista filter) {
		return repository.consultaApresentacaoLista(filter);
	}

	public Produto consultaPorId(Integer id) {
		Produto entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	public Produto consultaCompletoPorId(Integer id) {
		Produto entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		preencheRelacionados(entity);

		return entity;
	}

	// Util
	private void validaDados(Produto entity) {
		if (entity.getImagens().isEmpty()) {
			throw new DadosInvalidosException("Ao menos uma imagem devem ser enviada");
		}

		if (entity.getTamanhos().isEmpty()) {
			throw new DadosInvalidosException("Ao menos um tamanho deve ser cadastrado");
		}

		boolean possuiCapa = false;
		for (ProdutoImagem imagem : entity.getImagens()) {
			if (imagem.isCapa()) {
				possuiCapa = true;
				break;
			}
		}

		if (!possuiCapa) {
			throw new DadosInvalidosException("Ao menos uma imagem deve ser capa");
		}
	}

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
			// Remove as referencias recursivas
			for (ProdutoTamanho tamanho : objeto.getTamanhos()) {
				tamanho.setProduto(null);
				tamanho.setTamanho(null);
			}
			objeto.setImagens(null);

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}

	private void preencheRelacionados(Produto entity) {
		entity.setTamanhos(tamanhoRepository.consultaPorProduto(entity.getId()));
		entity.setImagens(imagemRepository.consultaPorProduto(entity.getId()));
	}
}