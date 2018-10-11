package br.com.graflogic.hermitex.cliente.data.entity.pedido;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatusTroca;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_troca")
public class Troca implements Serializable {

	private static final long serialVersionUID = 4539920995943368202L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_TROCA")
	@SequenceGenerator(name = "SQ_TROCA", sequenceName = "SQ_TROCA", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "id_pedido", nullable = false)
	private Long idPedido;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "troca", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<TrocaItem> itens;

	// Apresentacao
	@Transient
	private Date dataCadastro;

	@Transient
	private Integer quantidadeItens;

	// Filtros
	@Transient
	private Integer idCliente;

	@Transient
	private Integer idFilial;

	@Transient
	private Date dataCadastroDe;

	@Transient
	private Date dataCadastroAte;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Long idPedido) {
		this.idPedido = idPedido;
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

	public List<TrocaItem> getItens() {
		return itens;
	}

	public void setItens(List<TrocaItem> itens) {
		this.itens = itens;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Integer getQuantidadeItens() {
		return quantidadeItens;
	}

	public void setQuantidadeItens(Integer quantidadeItens) {
		this.quantidadeItens = quantidadeItens;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public Integer getIdFilial() {
		return idFilial;
	}

	public void setIdFilial(Integer idFilial) {
		this.idFilial = idFilial;
	}

	public Date getDataCadastroDe() {
		return dataCadastroDe;
	}

	public void setDataCadastroDe(Date dataCadastroDe) {
		this.dataCadastroDe = dataCadastroDe;
	}

	public Date getDataCadastroAte() {
		return dataCadastroAte;
	}

	public void setDataCadastroAte(Date dataCadastroAte) {
		this.dataCadastroAte = dataCadastroAte;
	}

	public String getDeStatus() {
		return DomPedido.domStatusTroca.getDeValor(status);
	}

	public String getFormattedId() {
		return StringUtils.leftPad(id.toString(), 10, "0");
	}

	public String getFormattedIdPedido() {
		return StringUtils.leftPad(idPedido.toString(), 10, "0");
	}

	public boolean isCadastrada() {
		return null != status && DomStatusTroca.CADASTRADA.equals(status);
	}

	public boolean isFinalizada() {
		return null != status && DomStatusTroca.FINALIZADA.equals(status);
	}
}