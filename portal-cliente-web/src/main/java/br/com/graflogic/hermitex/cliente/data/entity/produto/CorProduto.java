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

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_cor_produto")
public class CorProduto implements Serializable {

	private static final long serialVersionUID = -2308830937308217243L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_COR_PRODUTO")
	@SequenceGenerator(name = "SQ_COR_PRODUTO", sequenceName = "SQ_COR_PRODUTO", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "hexa", nullable = false)
	private String hexa;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getHexa() {
		return hexa;
	}

	public void setHexa(String hexa) {
		this.hexa = hexa;
	}

	public Long getVersao() {
		return versao;
	}

	public void setVersao(Long versao) {
		this.versao = versao;
	}

	public String getParsedHexa() {
		return "#" + hexa;
	}
}