package br.com.graflogic.hermitex.cliente.data.entity.produto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import br.com.graflogic.hermitex.cliente.data.dom.DomProduto;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatusTamanho;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_tamanho_produto")
public class TamanhoProduto implements Serializable {

	private static final long serialVersionUID = -2308830937308217243L;

	@Id
	@Column(name = "codigo", nullable = false)
	private String codigo;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "ordem", nullable = false)
	private Integer ordem;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
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
		return null != status && DomStatusTamanho.ATIVO.equals(status);
	}

	public boolean isInativo() {
		if (DomStatusTamanho.INATIVO.equals(status)) {
			return true;
		}
		return false;
	}

	public String getDeStatus() {
		return DomProduto.domStatusTamanho.getDeValor(status);
	}
}