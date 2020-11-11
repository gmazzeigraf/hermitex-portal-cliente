package br.com.graflogic.hermitex.cliente.data.entity.cadastro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomStatusFilial;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoEndereco;
import br.com.graflogic.hermitex.cliente.data.dom.DomGeral;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.Usuario;
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

	public Filial() {
		this.enderecos = new ArrayList<>();
		this.contatos = new ArrayList<>();
		this.proprietarios = new ArrayList<>();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_FILIAL")
	@SequenceGenerator(name = "SQ_FILIAL", sequenceName = "SQ_FILIAL", allocationSize = 1)
	@Column(name = "id")
	private Integer id;

	@Column(name = "id_cliente", nullable = false)
	private Integer idCliente;

	@Column(name = "documento", nullable = false)
	private String documento;

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

	@Column(name = "tipo", nullable = false)
	private String tipo;

	@Column(name = "in_sem_credito", nullable = false)
	private String semCredito;

	@Column(name = "in_compra_bloqueada", nullable = false)
	private String compraBloqueada;

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

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tb_filial_usuario_proprietario", joinColumns = {
			@JoinColumn(name = "id_filial", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "id_usuario", referencedColumnName = "id") })
	private List<Usuario> proprietarios;

	@Transient
	private String siglaEstadoFaturamento;

	@Transient
	private String siglaEstadoEntrega;

	@Transient
	private Integer idUsuarioProprietario;

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

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getSemCredito() {
		return semCredito;
	}

	public void setSemCredito(String semCredito) {
		this.semCredito = semCredito;
	}

	public String getCompraBloqueada() {
		return compraBloqueada;
	}

	public void setCompraBloqueada(String compraBloqueada) {
		this.compraBloqueada = compraBloqueada;
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
		this.enderecos.clear();
		if (null != enderecos) {
			this.enderecos.addAll(enderecos);
		}
	}

	public List<FilialContato> getContatos() {
		return contatos;
	}

	public void setContatos(List<FilialContato> contatos) {
		this.contatos.clear();
		if (null != contatos) {
			this.contatos.addAll(contatos);
		}
	}

	public List<Usuario> getProprietarios() {
		return proprietarios;
	}

	public void setProprietarios(List<Usuario> proprietarios) {
		this.proprietarios.clear();
		if (null != proprietarios) {
			this.proprietarios.addAll(proprietarios);
		}
	}

	public String getSiglaEstadoFaturamento() {
		return siglaEstadoFaturamento;
	}

	public void setSiglaEstadoFaturamento(String siglaEstadoFaturamento) {
		this.siglaEstadoFaturamento = siglaEstadoFaturamento;
	}

	public String getSiglaEstadoEntrega() {
		return siglaEstadoEntrega;
	}

	public void setSiglaEstadoEntrega(String siglaEstadoEntrega) {
		this.siglaEstadoEntrega = siglaEstadoEntrega;
	}

	public Integer getIdUsuarioProprietario() {
		return idUsuarioProprietario;
	}

	public void setIdUsuarioProprietario(Integer idUsuarioProprietario) {
		this.idUsuarioProprietario = idUsuarioProprietario;
	}

	public boolean isAtiva() {
		return null != status && DomStatusFilial.ATIVO.equals(status);
	}

	public boolean isInativa() {
		if (DomStatusFilial.INATIVO.equals(status)) {
			return true;
		}
		return false;
	}

	public String getDeTipo() {
		return DomCadastro.domTipoFilial.getDeValor(tipo);
	}

	public String getDeCompraBloqueada() {
		return DomGeral.domBoolean.getDeValor(compraBloqueada);
	}

	public String getDeStatus() {
		return DomCadastro.domStatusFilial.getDeValor(status);
	}

	public String getFormattedDocumento() {
		return documento.length() == 14 ? FormatUtil.formatCNPJ(documento) : FormatUtil.formatCPF(documento);
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

	public String getNomeApresentacao() {
		return (StringUtils.isNotBlank(nomeFantasia)) ? nomeFantasia : razaoSocial;
	}
}