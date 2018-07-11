package br.com.graflogic.mundipagg.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class BoletoTransaction {

	@SerializedName("AmountInCents")
	private Long amountInCents;

	@SerializedName("BankNumber")
	private String bankNumber;

	@SerializedName("BillingAddress")
	private BillingAddress billingAddress;

	@SerializedName("DocumentNumber")
	private String documentNumber;

	@SerializedName("Instructions")
	private String instructions;

	@SerializedName("Options")
	private Options options;

	@SerializedName("TransactionReference")
	private String transactionReference;

	public Long getAmountInCents() {
		return amountInCents;
	}

	public void setAmountInCents(Long amountInCents) {
		this.amountInCents = amountInCents;
	}

	public String getBankNumber() {
		return bankNumber;
	}

	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}

	public BillingAddress getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(BillingAddress billingAddress) {
		this.billingAddress = billingAddress;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public String getTransactionReference() {
		return transactionReference;
	}

	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}

}
