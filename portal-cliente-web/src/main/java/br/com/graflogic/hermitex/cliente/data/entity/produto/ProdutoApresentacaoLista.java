package br.com.graflogic.hermitex.cliente.data.entity.produto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Transient;

/**
 * 
 * @author gmazz
 *
 */
public class ProdutoApresentacaoLista implements Serializable {

	private static final long serialVersionUID = 5153822059012990678L;

	private Integer id;

	private String codigo;

	private String titulo;

	private BigDecimal valor;

	private String idImagemCapa;

	// Filtros
	@Transient
	private Integer idCliente;

	@Transient
	private Integer idCategoria;

	@Transient
	private Integer idSetor;

	@Transient
	private String genero;

	@Transient
	private String codigoTamanho;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getIdImagemCapa() {
		return idImagemCapa;
	}

	public void setIdImagemCapa(String idImagemCapa) {
		this.idImagemCapa = idImagemCapa;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
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

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public String getCodigoTamanho() {
		return codigoTamanho;
	}

	public void setCodigoTamanho(String codigoTamanho) {
		this.codigoTamanho = codigoTamanho;
	}
}