package br.com.graflogic.hermitex.cliente.data.entity.produto;

import java.io.Serializable;

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

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_solicitacao_estoque_item")
public class SolicitacaoEstoqueItem implements Serializable {

	private static final long serialVersionUID = 3742246501575091571L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_SOLICITACAO_ESTOQUE_ITEM")
	@SequenceGenerator(name = "SQ_SOLICITACAO_ESTOQUE_ITEM", sequenceName = "SQ_SOLICITACAO_ESTOQUE_ITEM", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "id_solicitacao", nullable = false)
	private Long idSolicitacao;

	@Column(name = "id_produto", nullable = false)
	private Integer idProduto;

	@Column(name = "cd_tamanho", nullable = false)
	private String codigoTamanho;

	@Column(name = "quantidade", nullable = false)
	private Integer quantidade;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_solicitacao", referencedColumnName = "id", insertable = false, updatable = false)
	private SolicitacaoEstoque solicitacao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdSolicitacao() {
		return idSolicitacao;
	}

	public void setIdSolicitacao(Long idSolicitacao) {
		this.idSolicitacao = idSolicitacao;
	}

	public Integer getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(Integer idProduto) {
		this.idProduto = idProduto;
	}

	public String getCodigoTamanho() {
		return codigoTamanho;
	}

	public void setCodigoTamanho(String codigoTamanho) {
		this.codigoTamanho = codigoTamanho;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public SolicitacaoEstoque getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(SolicitacaoEstoque solicitacao) {
		this.solicitacao = solicitacao;
	}
}