package br.com.graflogic.mundipagg.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class CreditCardTransactionResult {

	@SerializedName("AcquirerMessage")
	private String acquirerMessage;
	
	@SerializedName("AcquirerName")
	private String acquirerName;
	
	@SerializedName("AcquirerReturnCode")
	private String acquirerReturnCode;
	
	@SerializedName("AffiliationCode")
	private String affiliationCode;
	
	@SerializedName("AmountInCents")
	private Integer amountInCents;
	
	@SerializedName("AuthorizationCode")
	private String authorizationCode;
	
	@SerializedName("AuthorizedAmountInCents")
	private Integer authorizedAmountInCents;
	
	@SerializedName("CapturedAmountInCents")
	private Integer capturedAmountInCents;
	
	@SerializedName("CapturedDate")
	private String capturedDate;
	
	@SerializedName("CreditCard")
	private CreditCard creditCard;
	
	@SerializedName("CreditCardOperation")
	private String creditCardOperation;
	
	@SerializedName("CreditCardTransactionStatus")
	private String creditCardTransactionStatus;
	
	@SerializedName("DueDate")
	private String dueDate;
	
	@SerializedName("ExternalTime")
	private Integer externalTime;
	
	@SerializedName("PaymentMethodName")
	private String paymentMethodName;
	
	@SerializedName("RefundedAmountInCents")
	private Long refundedAmountInCents;
	
	@SerializedName("Success")
	private Boolean success;
	
	@SerializedName("TransactionIdentifier")
	private String transactionIdentifier;
	
	@SerializedName("TransactionKey")
	private String transactionKey;
	
	@SerializedName("TransactionKeyToAcquirer")
	private String transactionKeyToAcquirer;
	
	@SerializedName("TransactionReference")
	private String transactionReference;
	
	@SerializedName("UniqueSequentialNumber")
	private String uniqueSequentialNumber;
	
	@SerializedName("VoidedAmountInCents")
	private Long voidedAmountInCents;

	public String getAcquirerMessage() {
		return acquirerMessage;
	}

	public void setAcquirerMessage(String acquirerMessage) {
		this.acquirerMessage = acquirerMessage;
	}

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}

	public String getAcquirerReturnCode() {
		return acquirerReturnCode;
	}

	public void setAcquirerReturnCode(String acquirerReturnCode) {
		this.acquirerReturnCode = acquirerReturnCode;
	}

	public String getAffiliationCode() {
		return affiliationCode;
	}

	public void setAffiliationCode(String affiliationCode) {
		this.affiliationCode = affiliationCode;
	}

	public Integer getAmountInCents() {
		return amountInCents;
	}

	public void setAmountInCents(Integer amountInCents) {
		this.amountInCents = amountInCents;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	public Integer getAuthorizedAmountInCents() {
		return authorizedAmountInCents;
	}

	public void setAuthorizedAmountInCents(Integer authorizedAmountInCents) {
		this.authorizedAmountInCents = authorizedAmountInCents;
	}

	public Integer getCapturedAmountInCents() {
		return capturedAmountInCents;
	}

	public void setCapturedAmountInCents(Integer capturedAmountInCents) {
		this.capturedAmountInCents = capturedAmountInCents;
	}

	public String getCapturedDate() {
		return capturedDate;
	}

	public void setCapturedDate(String capturedDate) {
		this.capturedDate = capturedDate;
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

	public String getCreditCardTransactionStatus() {
		return creditCardTransactionStatus;
	}

	public void setCreditCardTransactionStatus(String creditCardTransactionStatus) {
		this.creditCardTransactionStatus = creditCardTransactionStatus;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getExternalTime() {
		return externalTime;
	}

	public void setExternalTime(Integer externalTime) {
		this.externalTime = externalTime;
	}

	public String getPaymentMethodName() {
		return paymentMethodName;
	}

	public void setPaymentMethodName(String paymentMethodName) {
		this.paymentMethodName = paymentMethodName;
	}

	public Long getRefundedAmountInCents() {
		return refundedAmountInCents;
	}

	public void setRefundedAmountInCents(Long refundedAmountInCents) {
		this.refundedAmountInCents = refundedAmountInCents;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getTransactionIdentifier() {
		return transactionIdentifier;
	}

	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}

	public String getTransactionKey() {
		return transactionKey;
	}

	public void setTransactionKey(String transactionKey) {
		this.transactionKey = transactionKey;
	}

	public String getTransactionKeyToAcquirer() {
		return transactionKeyToAcquirer;
	}

	public void setTransactionKeyToAcquirer(String transactionKeyToAcquirer) {
		this.transactionKeyToAcquirer = transactionKeyToAcquirer;
	}

	public String getTransactionReference() {
		return transactionReference;
	}

	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}

	public String getUniqueSequentialNumber() {
		return uniqueSequentialNumber;
	}

	public void setUniqueSequentialNumber(String uniqueSequentialNumber) {
		this.uniqueSequentialNumber = uniqueSequentialNumber;
	}

	public Long getVoidedAmountInCents() {
		return voidedAmountInCents;
	}

	public void setVoidedAmountInCents(Long voidedAmountInCents) {
		this.voidedAmountInCents = voidedAmountInCents;
	}
}