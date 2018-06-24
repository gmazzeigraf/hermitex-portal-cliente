package br.com.graflogic.hermitex.cliente.data.entity.cadastro;

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
@Table(name = "tb_cliente_contato")
public class ClienteContato implements Serializable {

	private static final long serialVersionUID = -5328516051141700536L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_CLIENTE_CONTATO")
	@SequenceGenerator(name = "SQ_CLIENTE_CONTATO", sequenceName = "SQ_CLIENTE_CONTATO", allocationSize = 1)
	@Column(name = "id")
	private Integer id;

	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "descricao")
	private String descricao;

	@Column(name = "email")
	private String email;

	@Column(name = "telefone")
	private String telefone;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente", referencedColumnName = "id", insertable = false, updatable = false)
	private Cliente cliente;

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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
}