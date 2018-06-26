package br.com.graflogic.hermitex.cliente.data.entity.produto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import br.com.graflogic.hermitex.cliente.data.dom.DomProduto;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatusCategoria;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_setor_produto")
public class SetorProduto implements Serializable {

	private static final long serialVersionUID = 1683443828974068318L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_SETOR_PRODUTO")
	@SequenceGenerator(name = "SQ_SETOR_PRODUTO", sequenceName = "SQ_SETOR_PRODUTO", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

	@Column(name = "nome", nullable = false)
	private String nome;

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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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
		return null != status && DomStatusCategoria.ATIVA.equals(status);
	}

	public boolean isInativo() {
		if (DomStatusCategoria.INATIVA.equals(status)) {
			return true;
		}
		return false;
	}

	public String getDeStatus() {
		return DomProduto.domStatusCategoria.getDeValor(status);
	}
}