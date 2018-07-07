package br.com.graflogic.hermitex.cliente.data.entity.cadastro;

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

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomStatusRepresentante;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoEndereco;
import br.com.graflogic.utilities.datautil.util.FormatUtil;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_representante")
public class Representante implements Serializable {

	private static final long serialVersionUID = -3747790521790651488L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_REPRESENTANTE")
	@SequenceGenerator(name = "SQ_REPRESENTANTE", sequenceName = "SQ_REPRESENTANTE", allocationSize = 1)
	@Column(name = "id")
	private Integer id;

	@Column(name = "cnpj", nullable = false)
	private String cnpj;

	@Column(name = "razao_social", nullable = false)
	private String razaoSocial;

	@Column(name = "nm_fantasia")
	private String nomeFantasia;

	@Column(name = "inscricao_estadual")
	private String inscricaoEstadual;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "telefone", nullable = false)
	private String telefone;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "representante", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<RepresentanteEndereco> enderecos;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "representante", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<RepresentanteContato> contatos;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
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

	public List<RepresentanteEndereco> getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(List<RepresentanteEndereco> enderecos) {
		this.enderecos = enderecos;
	}

	public List<RepresentanteContato> getContatos() {
		return contatos;
	}

	public void setContatos(List<RepresentanteContato> contatos) {
		this.contatos = contatos;
	}

	public boolean isAtivo() {
		return null != status && DomStatusRepresentante.ATIVO.equals(status);
	}

	public boolean isInativo() {
		if (DomStatusRepresentante.INATIVO.equals(status)) {
			return true;
		}
		return false;
	}

	public String getDeStatus() {
		return DomCadastro.domStatusRepresentante.getDeValor(status);
	}

	public String getFormattedCnpj() {
		return FormatUtil.formatCNPJ(cnpj);
	}

	public RepresentanteEndereco getEnderecoCadastro() {
		return enderecos.get(enderecos.indexOf(new RepresentanteEndereco(new RepresentanteEnderecoPK(id, DomTipoEndereco.CADASTRO))));
	}
}