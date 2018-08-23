package br.com.graflogic.hermitex.cliente.data.entity.cadastro;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomStatusCliente;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoEndereco;
import br.com.graflogic.utilities.datautil.util.FormatUtil;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_cliente")
public class Cliente implements Serializable {

	private static final long serialVersionUID = 8284333242118992528L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_CLIENTE")
	@SequenceGenerator(name = "SQ_CLIENTE", sequenceName = "SQ_CLIENTE", allocationSize = 1)
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

	@Column(name = "id_representante")
	private Integer idRepresentante;

	@Column(name = "qt_dias_entrega", nullable = false)
	private Integer quantidadeDiasEntrega;

	@Column(name = "status", nullable = false)
	private String status;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cliente", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<ClienteEndereco> enderecos;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cliente", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<ClienteContato> contatos;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "cliente", orphanRemoval = true, fetch = FetchType.LAZY)
	private ClienteLogotipo logotipo;

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

	public Integer getIdRepresentante() {
		return idRepresentante;
	}

	public void setIdRepresentante(Integer idRepresentante) {
		this.idRepresentante = idRepresentante;
	}

	public Integer getQuantidadeDiasEntrega() {
		return quantidadeDiasEntrega;
	}

	public void setQuantidadeDiasEntrega(Integer quantidadeDiasEntrega) {
		this.quantidadeDiasEntrega = quantidadeDiasEntrega;
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

	public List<ClienteEndereco> getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(List<ClienteEndereco> enderecos) {
		this.enderecos = enderecos;
	}

	public List<ClienteContato> getContatos() {
		return contatos;
	}

	public void setContatos(List<ClienteContato> contatos) {
		this.contatos = contatos;
	}

	public ClienteLogotipo getLogotipo() {
		return logotipo;
	}

	public void setLogotipo(ClienteLogotipo logotipo) {
		this.logotipo = logotipo;
	}

	public boolean isAtivo() {
		return null != status && DomStatusCliente.ATIVO.equals(status);
	}

	public boolean isInativo() {
		if (DomStatusCliente.INATIVO.equals(status)) {
			return true;
		}
		return false;
	}

	public String getDeStatus() {
		return DomCadastro.domStatusCliente.getDeValor(status);
	}

	public String getFormattedCnpj() {
		return FormatUtil.formatCNPJ(cnpj);
	}

	public ClienteEndereco getEnderecoFaturamento() {
		return getEndereco(DomTipoEndereco.FATURAMENTO);
	}

	public ClienteEndereco getEnderecoEntrega() {
		return getEndereco(DomTipoEndereco.ENTREGA);
	}

	private ClienteEndereco getEndereco(String tipo) {
		for (ClienteEndereco endereco : enderecos) {
			if (tipo.equals(endereco.getId().getTipo())) {
				return endereco;
			}
		}

		return null;
	}

	public String getNomeApresentacao() {
		return (StringUtils.isNotBlank(nomeFantasia)) ? nomeFantasia : razaoSocial;
	}
}