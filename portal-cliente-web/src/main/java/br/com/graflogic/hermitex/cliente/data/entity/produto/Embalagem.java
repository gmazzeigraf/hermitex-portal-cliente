package br.com.graflogic.hermitex.cliente.data.entity.produto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import br.com.graflogic.hermitex.cliente.data.dom.DomProduto;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatusEmbalagem;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_embalagem")
public class Embalagem implements Serializable {

	private static final long serialVersionUID = 8604362263059898812L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_EMBALAGEM")
	@SequenceGenerator(name = "SQ_EMBALAGEM", sequenceName = "SQ_EMBALAGEM", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "tp_produto", nullable = false)
	private String tipoProduto;

	@Column(name = "peso_maximo", nullable = false)
	private BigDecimal pesoMaximo;

	@Column(name = "qt_maxima", nullable = false)
	private Integer quantidadeMaxima;

	@Column(name = "altura", nullable = false)
	private BigDecimal altura;

	@Column(name = "largura", nullable = false)
	private BigDecimal largura;

	@Column(name = "comprimento", nullable = false)
	private BigDecimal comprimento;

	@Column(name = "peso", nullable = false)
	private BigDecimal peso;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipoProduto() {
		return tipoProduto;
	}

	public void setTipoProduto(String tipoProduto) {
		this.tipoProduto = tipoProduto;
	}

	public BigDecimal getPesoMaximo() {
		return pesoMaximo;
	}

	public void setPesoMaximo(BigDecimal pesoMaximo) {
		this.pesoMaximo = pesoMaximo;
	}

	public Integer getQuantidadeMaxima() {
		return quantidadeMaxima;
	}

	public void setQuantidadeMaxima(Integer quantidadeMaxima) {
		this.quantidadeMaxima = quantidadeMaxima;
	}

	public BigDecimal getAltura() {
		return altura;
	}

	public void setAltura(BigDecimal altura) {
		this.altura = altura;
	}

	public BigDecimal getLargura() {
		return largura;
	}

	public void setLargura(BigDecimal largura) {
		this.largura = largura;
	}

	public BigDecimal getComprimento() {
		return comprimento;
	}

	public void setComprimento(BigDecimal comprimento) {
		this.comprimento = comprimento;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
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

	public boolean isAtivo() {
		return null != status && DomStatusEmbalagem.ATIVO.equals(status);
	}

	public boolean isInativo() {
		if (DomStatusEmbalagem.INATIVO.equals(status)) {
			return true;
		}
		return false;
	}

	public String getDeTipoProduto() {
		return DomProduto.domTipo.getDeValor(tipoProduto);
	}

	public String getDeStatus() {
		return DomProduto.domStatusEmbalagem.getDeValor(status);
	}
}