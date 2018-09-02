package br.com.graflogic.hermitex.cliente.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author ggraf
 *
 */
public class BoletoTransaction {

	@JsonProperty("StatusChangedDate")
	private String statusChangedDate;
	@JsonProperty("TransactionKey")
	private String transactionKey;
	@JsonProperty("TransactionReference")
	private String transactionReference;
	@JsonProperty("BoletoExpirationDate")
	private String boletoExpirationDate;
	@JsonProperty("NossoNumero")
	private String nossoNumero;
	@JsonProperty("AmountInCents")
	private Long amountInCents;
	@JsonProperty("AmountPaidInCents")
	private Long amountPaidInCents;
	@JsonProperty("Bank")
	private String bank;
	@JsonProperty("PreviousBoletoTransactionStatus")
	private String previousBoletoTransactionStatus;
	@JsonProperty("BoletoTransactionStatus")
	private String boletoTransactionStatus;

	public String getStatusChangedDate() {
		return statusChangedDate;
	}

	public void setStatusChangedDate(String statusChangedDate) {
		this.statusChangedDate = statusChangedDate;
	}

	public String getTransactionKey() {
		return transactionKey;
	}

	public void setTransactionKey(String transactionKey) {
		this.transactionKey = transactionKey;
	}

	public String getTransactionReference() {
		return transactionReference;
	}

	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}

	public String getBoletoExpirationDate() {
		return boletoExpirationDate;
	}

	public void setBoletoExpirationDate(String boletoExpirationDate) {
		this.boletoExpirationDate = boletoExpirationDate;
	}

	public String getNossoNumero() {
		return nossoNumero;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public Long getAmountInCents() {
		return amountInCents;
	}

	public void setAmountInCents(Long amountInCents) {
		this.amountInCents = amountInCents;
	}

	public Long getAmountPaidInCents() {
		return amountPaidInCents;
	}

	public void setAmountPaidInCents(Long amountPaidInCents) {
		this.amountPaidInCents = amountPaidInCents;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getPreviousBoletoTransactionStatus() {
		return previousBoletoTransactionStatus;
	}

	public void setPreviousBoletoTransactionStatus(String previousBoletoTransactionStatus) {
		this.previousBoletoTransactionStatus = previousBoletoTransactionStatus;
	}

	public String getBoletoTransactionStatus() {
		return boletoTransactionStatus;
	}

	public void setBoletoTransactionStatus(String boletoTransactionStatus) {
		this.boletoTransactionStatus = boletoTransactionStatus;
	}
}