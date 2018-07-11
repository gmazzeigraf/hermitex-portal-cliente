package br.com.graflogic.mundipagg.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class CreditCardTransaction {

	@SerializedName("AmountInCents")
	private Long amountInCents;
	
	@SerializedName("CreditCard")
	private CreditCard creditCard;
	
	@SerializedName("CreditCardOperation")
	private String creditCardOperation;
	
	@SerializedName("InstallmentCount")
	private Integer installmentCount;
	
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

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public String getCreditCardOperation() {
		return creditCardOperation;
	}

	public void setCreditCardOperation(String creditCardOperation) {
		this.creditCardOperation = creditCardOperation;
	}

	public Integer getInstallmentCount() {
		return installmentCount;
	}

	public void setInstallmentCount(Integer installmentCount) {
		this.installmentCount = installmentCount;
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