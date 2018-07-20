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
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomStatusFilial;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoEndereco;
import br.com.graflogic.utilities.datautil.util.FormatUtil;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_filial")
public class Filial implements Serializable {

	private static final long serialVersionUID = 2671690481027833377L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_FILIAL")
	@SequenceGenerator(name = "SQ_FILIAL", sequenceName = "SQ_FILIAL", allocationSize = 1)
	@Column(name = "id")
	private Integer id;

	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

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

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "filial", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<FilialEndereco> enderecos;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "filial", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<FilialContato> contatos;

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

	public List<FilialEndereco> getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(List<FilialEndereco> enderecos) {
		this.enderecos = enderecos;
	}

	public List<FilialContato> getContatos() {
		return contatos;
	}

	public void setContatos(List<FilialContato> contatos) {
		this.contatos = contatos;
	}

	public boolean isAtivo() {
		return null != status && DomStatusFilial.ATIVO.equals(status);
	}

	public boolean isInativo() {
		if (DomStatusFilial.INATIVO.equals(status)) {
			return true;
		}
		return false;
	}

	public String getDeStatus() {
		return DomCadastro.domStatusFilial.getDeValor(status);
	}

	public String getFormattedCnpj() {
		return FormatUtil.formatCNPJ(cnpj);
	}

	public FilialEndereco getEnderecoFaturamento() {
		return getEndereco(DomTipoEndereco.FATURAMENTO);
	}

	public FilialEndereco getEnderecoEntrega() {
		return getEndereco(DomTipoEndereco.ENTREGA);
	}

	private FilialEndereco getEndereco(String tipo) {
		for (FilialEndereco endereco : enderecos) {
			if (tipo.equals(endereco.getId().getTipo())) {
				return endereco;
			}
		}

		return null;
	}
}