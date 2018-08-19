package br.com.graflogic.hermitex.cliente.data.entity.produto;

import java.io.Serializable;
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

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatusSolicitacaoEstoque;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_solicitacao_estoque")
public class SolicitacaoEstoque implements Serializable {

	private static final long serialVersionUID = -6447122204918864713L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_SOLICITACAO_ESTOQUE")
	@SequenceGenerator(name = "SQ_SOLICITACAO_ESTOQUE", sequenceName = "SQ_SOLICITACAO_ESTOQUE", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "solicitacao", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<SolicitacaoEstoqueItem> itens;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
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

	public List<SolicitacaoEstoqueItem> getItens() {
		return itens;
	}

	public void setItens(List<SolicitacaoEstoqueItem> itens) {
		this.itens = itens;
	}

	public String getFormattedId() {
		return StringUtils.leftPad(id.toString(), 10, "0");
	}

	public boolean isCadastrada() {
		return null != status && DomStatusSolicitacaoEstoque.CADASTRADA.equals(status);
	}

	public boolean isFinalizada() {
		return null != status && DomStatusSolicitacaoEstoque.FINALIZADA.equals(status);
	}
}