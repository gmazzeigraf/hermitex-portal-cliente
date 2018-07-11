package br.com.graflogic.mundipagg.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class BoletoTransactionResult {

	@SerializedName("BoletoUrl")
	private String boletoUrl;

	@SerializedName("Barcode")
	private String barcode;

	@SerializedName("BoletoTransactionStatus")
	private String boletoTransactionStatus;

	@SerializedName("TransactionKey")
	private String transactionKey;

	@SerializedName("AmountInCents")
	private Integer amountInCents;

	@SerializedName("DocumentNumber")
	private String documentNumber;

	@SerializedName("TransactionReference")
	private String transactionReference;

	@SerializedName("Success")
	private Boolean success;

	@SerializedName("NossoNumero")
	private String nossoNumero;

	@SerializedName("AcquirerReturnCode")
	private Object acquirerReturnCode;

	@SerializedName("AcquirerReturnMessage")
	private Object acquirerReturnMessage;

	@SerializedName("TypeTag")
	private String typeTag;

	public String getBoletoUrl() {
		return boletoUrl;
	}

	public void setBoletoUrl(String boletoUrl) {
		this.boletoUrl = boletoUrl;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getBoletoTransactionStatus() {
		return boletoTransactionStatus;
	}

	public void setBoletoTransactionStatus(String boletoTransactionStatus) {
		this.boletoTransactionStatus = boletoTransactionStatus;
	}

	public String getTransactionKey() {
		return transactionKey;
	}

	public void setTransactionKey(String transactionKey) {
		this.transactionKey = transactionKey;
	}

	public Integer getAmountInCents() {
		return amountInCents;
	}

	public void setAmountInCents(Integer amountInCents) {
		this.amountInCents = amountInCents;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getTransactionReference() {
		return transactionReference;
	}

	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getNossoNumero() {
		return nossoNumero;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public Object getAcquirerReturnCode() {
		return acquirerReturnCode;
	}

	public void setAcquirerReturnCode(Object acquirerReturnCode) {
		this.acquirerReturnCode = acquirerReturnCode;
	}

	public Object getAcquirerReturnMessage() {
		return acquirerReturnMessage;
	}

	public void setAcquirerReturnMessage(Object acquirerReturnMessage) {
		this.acquirerReturnMessage = acquirerReturnMessage;
	}

	public String getTypeTag() {
		return typeTag;
	}

	public void setTypeTag(String typeTag) {
		this.typeTag = typeTag;
	}

}
