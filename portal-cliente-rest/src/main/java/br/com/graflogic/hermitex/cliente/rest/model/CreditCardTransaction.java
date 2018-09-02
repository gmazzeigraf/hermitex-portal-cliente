package br.com.graflogic.hermitex.cliente.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author ggraf
 *
 */
public class CreditCardTransaction {

	@JsonProperty("StatusChangedDate")
	private String statusChangedDate;
	@JsonProperty("TransactionKey")
	private String transactionKey;
	@JsonProperty("TransactionReference")
	private String transactionReference;
	@JsonProperty("TransactionIdentifier")
	private String transactionIdentifier;
	@JsonProperty("UniqueSequentialNumber")
	private String uniqueSequentialNumber;
	@JsonProperty("AmountInCents")
	private Long amountInCents;
	@JsonProperty("Acquirer")
	private String acquirer;
	@JsonProperty("CreditCardBrand")
	private String creditCardBrand;
	@JsonProperty("AuthorizedAmountInCents")
	private Long authorizedAmountInCents;
	@JsonProperty("CapturedAmountInCents")
	private Long capturedAmountInCents;
	@JsonProperty("AuthorizationCode")
	private String authorizationCode;
	@JsonProperty("PreviousCreditCardTransactionStatus")
	private String previousCreditCardTransactionStatus;
	@JsonProperty("CreditCardTransactionStatus")
	private String creditCardTransactionStatus;

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

	public String getTransactionIdentifier() {
		return transactionIdentifier;
	}

	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}

	public String getUniqueSequentialNumber() {
		return uniqueSequentialNumber;
	}

	public void setUniqueSequentialNumber(String uniqueSequentialNumber) {
		this.uniqueSequentialNumber = uniqueSequentialNumber;
	}

	public Long getAmountInCents() {
		return amountInCents;
	}

	public void setAmountInCents(Long amountInCents) {
		this.amountInCents = amountInCents;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getCreditCardBrand() {
		return creditCardBrand;
	}

	public void setCreditCardBrand(String creditCardBrand) {
		this.creditCardBrand = creditCardBrand;
	}

	public Long getAuthorizedAmountInCents() {
		return authorizedAmountInCents;
	}

	public void setAuthorizedAmountInCents(Long authorizedAmountInCents) {
		this.authorizedAmountInCents = authorizedAmountInCents;
	}

	public Long getCapturedAmountInCents() {
		return capturedAmountInCents;
	}

	public void setCapturedAmountInCents(Long capturedAmountInCents) {
		this.capturedAmountInCents = capturedAmountInCents;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	public String getPreviousCreditCardTransactionStatus() {
		return previousCreditCardTransactionStatus;
	}

	public void setPreviousCreditCardTransactionStatus(String previousCreditCardTransactionStatus) {
		this.previousCreditCardTransactionStatus = previousCreditCardTransactionStatus;
	}

	public String getCreditCardTransactionStatus() {
		return creditCardTransactionStatus;
	}

	public void setCreditCardTransactionStatus(String creditCardTransactionStatus) {
		this.creditCardTransactionStatus = creditCardTransactionStatus;
	}
}