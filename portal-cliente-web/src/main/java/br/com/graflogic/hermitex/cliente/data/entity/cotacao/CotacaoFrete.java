package br.com.graflogic.hermitex.cliente.data.entity.cotacao;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_cotacao_frete")
public class CotacaoFrete implements Serializable {

	private static final long serialVersionUID = -1130804604832937532L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_COTACAO_FRETE")
	@SequenceGenerator(name = "SQ_COTACAO_FRETE", sequenceName = "SQ_COTACAO_FRETE", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "id_cotacao", nullable = false)
	private Long idCotacao;

	@Column(name = "id_embalagem")
	private Integer idEmbalagem;

	@Column(name = "peso_itens", nullable = false)
	private BigDecimal pesoItens;

	@Column(name = "qt_itens", nullable = false)
	private Integer quantidadeItens;

	@Column(name = "valor", nullable = false)
	private BigDecimal valor;

	@Column(name = "prazo_dias", nullable = false)
	private Integer prazoDias;

	@Column(name = "cd_servico", nullable = false)
	private String codigoServico;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cotacao", referencedColumnName = "id", insertable = false, updatable = false)
	private Cotacao cotacao;

	// Apresentacao
	@Transient
	private String nomeEmbalagem;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdCotacao() {
		return idCotacao;
	}

	public void setIdCotacao(Long idCotacao) {
		this.idCotacao = idCotacao;
	}

	public Integer getIdEmbalagem() {
		return idEmbalagem;
	}

	public void setIdEmbalagem(Integer idEmbalagem) {
		this.idEmbalagem = idEmbalagem;
	}

	public BigDecimal getPesoItens() {
		return pesoItens;
	}

	public void setPesoItens(BigDecimal pesoItens) {
		this.pesoItens = pesoItens;
	}

	public Integer getQuantidadeItens() {
		return quantidadeItens;
	}

	public void setQuantidadeItens(Integer quantidadeItens) {
		this.quantidadeItens = quantidadeItens;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Integer getPrazoDias() {
		return prazoDias;
	}

	public void setPrazoDias(Integer prazoDias) {
		this.prazoDias = prazoDias;
	}

	public String getCodigoServico() {
		return codigoServico;
	}

	public void setCodigoServico(String codigoServico) {
		this.codigoServico = codigoServico;
	}

	public Cotacao getCotacao() {
		return cotacao;
	}

	public void setCotacao(Cotacao cotacao) {
		this.cotacao = cotacao;
	}

	public String getNomeEmbalagem() {
		return nomeEmbalagem;
	}

	public void setNomeEmbalagem(String nomeEmbalagem) {
		this.nomeEmbalagem = nomeEmbalagem;
	}
}