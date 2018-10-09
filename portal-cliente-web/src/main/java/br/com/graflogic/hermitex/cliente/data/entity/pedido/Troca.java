package br.com.graflogic.hermitex.cliente.data.entity.pedido;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;

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

	@Column(name = "id_pedido_item", nullable = false)
	private Long idPedidoItem;

	@Column(name = "quantidade", nullable = false)
	private Integer quantidade;

	@Column(name = "motivo", nullable = false)
	private String motivo;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	// Apresentacao
	@Transient
	private Long idPedido;

	@Transient
	private Date dataCadastro;

	@Transient
	private String codigoProduto;

	@Transient
	private String tituloProduto;

	@Transient
	private String idImagemCapaProduto;

	@Transient
	private String codigoTamanhoProduto;

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

	public Long getIdPedidoItem() {
		return idPedidoItem;
	}

	public void setIdPedidoItem(Long idPedidoItem) {
		this.idPedidoItem = idPedidoItem;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
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

	public Long getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Long idPedido) {
		this.idPedido = idPedido;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getCodigoProduto() {
		return codigoProduto;
	}

	public void setCodigoProduto(String codigoProduto) {
		this.codigoProduto = codigoProduto;
	}

	public String getTituloProduto() {
		return tituloProduto;
	}

	public void setTituloProduto(String tituloProduto) {
		this.tituloProduto = tituloProduto;
	}

	public String getIdImagemCapaProduto() {
		return idImagemCapaProduto;
	}

	public void setIdImagemCapaProduto(String idImagemCapaProduto) {
		this.idImagemCapaProduto = idImagemCapaProduto;
	}

	public String getCodigoTamanhoProduto() {
		return codigoTamanhoProduto;
	}

	public void setCodigoTamanhoProduto(String codigoTamanhoProduto) {
		this.codigoTamanhoProduto = codigoTamanhoProduto;
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