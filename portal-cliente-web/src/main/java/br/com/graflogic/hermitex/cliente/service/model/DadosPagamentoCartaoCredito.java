package br.com.graflogic.hermitex.cliente.service.model;

/**
 * 
 * @author gmazz
 *
 */
public class DadosPagamentoCartaoCredito {

	private String bandeira;

	private String nomeImpresso;

	private String numero;

	private String vencimento;

	private String codigoSeguranca;

	private String documentoPortador;

	private Integer parcelas;

	public String getBandeira() {
		return bandeira;
	}

	public void setBandeira(String bandeira) {
		this.bandeira = bandeira;
	}

	public String getNomeImpresso() {
		return nomeImpresso;
	}

	public void setNomeImpresso(String nomeImpresso) {
		this.nomeImpresso = nomeImpresso;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getVencimento() {
		return vencimento;
	}

	public void setVencimento(String vencimento) {
		this.vencimento = vencimento;
	}

	public String getCodigoSeguranca() {
		return codigoSeguranca;
	}

	public void setCodigoSeguranca(String codigoSeguranca) {
		this.codigoSeguranca = codigoSeguranca;
	}

	public String getDocumentoPortador() {
		return documentoPortador;
	}

	public void setDocumentoPortador(String documentoPortador) {
		this.documentoPortador = documentoPortador;
	}

	public Integer getParcelas() {
		return parcelas;
	}

	public void setParcelas(Integer parcelas) {
		this.parcelas = parcelas;
	}
}