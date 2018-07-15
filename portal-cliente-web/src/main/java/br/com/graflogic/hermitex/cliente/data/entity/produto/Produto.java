package br.com.graflogic.hermitex.cliente.data.entity.produto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.graflogic.hermitex.cliente.data.dom.DomProduto;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatus;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_produto")
public class Produto implements Serializable {

	private static final long serialVersionUID = 3010435758145376436L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_PRODUTO")
	@SequenceGenerator(name = "SQ_PRODUTO", sequenceName = "SQ_PRODUTO", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

	@Column(name = "tipo", nullable = false)
	private String tipo;

	@Column(name = "id_categoria", nullable = false)
	private Integer idCategoria;

	@Column(name = "id_setor", nullable = false)
	private Integer idSetor;

	@Column(name = "codigo", nullable = false)
	private String codigo;

	@Column(name = "titulo", nullable = false)
	private String titulo;

	@Column(name = "descricao", nullable = false)
	private String descricao;

	@Column(name = "garantia", nullable = false)
	private String garantia;

	@Column(name = "genero", nullable = false)
	private String genero;

	@Column(name = "valor", nullable = false)
	private BigDecimal valor;

	@Column(name = "peso", nullable = false)
	private BigDecimal peso;

	@Column(name = "link_youtube", nullable = false)
	private String linkYoutube;

	@Column(name = "linhas_tabela_medidas", nullable = false)
	private Integer linhasTabelaMedidas;

	@Column(name = "ct_tabela_medidas", nullable = false)
	private String conteudoTabelaMedidas;

	@Column(name = "qt_estoque", nullable = false)
	private Integer quantidadeEstoque;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "produto", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<ProdutoTamanho> tamanhos;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "produto", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<ProdutoImagem> imagens;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}

	public Integer getIdSetor() {
		return idSetor;
	}

	public void setIdSetor(Integer idSetor) {
		this.idSetor = idSetor;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getGarantia() {
		return garantia;
	}

	public void setGarantia(String garantia) {
		this.garantia = garantia;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public String getLinkYoutube() {
		return linkYoutube;
	}

	public void setLinkYoutube(String linkYoutube) {
		this.linkYoutube = linkYoutube;
	}

	public Integer getLinhasTabelaMedidas() {
		return linhasTabelaMedidas;
	}

	public void setLinhasTabelaMedidas(Integer linhasTabelaMedidas) {
		this.linhasTabelaMedidas = linhasTabelaMedidas;
	}

	public String getConteudoTabelaMedidas() {
		return conteudoTabelaMedidas;
	}

	public void setConteudoTabelaMedidas(String conteudoTabelaMedidas) {
		this.conteudoTabelaMedidas = conteudoTabelaMedidas;
	}

	public Integer getQuantidadeEstoque() {
		return quantidadeEstoque;
	}

	public void setQuantidadeEstoque(Integer quantidadeEstoque) {
		this.quantidadeEstoque = quantidadeEstoque;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getVersao() {
		return versao;
	}

	public void setVersao(Long versao) {
		this.versao = versao;
	}

	public List<ProdutoTamanho> getTamanhos() {
		return tamanhos;
	}

	public void setTamanhos(List<ProdutoTamanho> tamanhos) {
		this.tamanhos = tamanhos;
	}

	public List<ProdutoImagem> getImagens() {
		return imagens;
	}

	public void setImagens(List<ProdutoImagem> imagens) {
		this.imagens = imagens;
	}

	public boolean isAtivo() {
		return null != status && DomStatus.ATIVO.equals(status);
	}

	public boolean isInativo() {
		if (DomStatus.INATIVO.equals(status)) {
			return true;
		}
		return false;
	}

	public String getDeTipo() {
		return DomProduto.domTipo.getDeValor(tipo);
	}

	public String getDeStatus() {
		return DomProduto.domStatus.getDeValor(status);
	}
}