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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatusJanelaCompra;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_janela_compra")
public class JanelaCompra implements Serializable {

	private static final long serialVersionUID = -1820969571941856052L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_JANELA_COMPRA")
	@SequenceGenerator(name = "SQ_JANELA_COMPRA", sequenceName = "SQ_JANELA_COMPRA", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

	@Column(name = "descricao")
	private String descricao;

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_abertura", nullable = false)
	private Date dataAbertura;

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_fechamento", nullable = false)
	private Date dataFechamento;

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

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataAbertura() {
		return dataAbertura;
	}

	public void setDataAbertura(Date dataAbertura) {
		this.dataAbertura = dataAbertura;
	}

	public Date getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(Date dataFechamento) {
		this.dataFechamento = dataFechamento;
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

	public boolean isCadastrada() {
		return null != status && DomStatusJanelaCompra.CADASTRADA.equals(status);
	}

	public String getDeStatus() {
		return DomPedido.domStatusJanelaCompra.getDeValor(status);
	}
}