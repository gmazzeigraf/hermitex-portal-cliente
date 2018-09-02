package br.com.graflogic.hermitex.cliente.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author ggraf
 *
 */
public class NotificacaoPagamentoRequest {

	@JsonProperty("CreditCardTransaction")
	private CreditCardTransaction creditCardTransaction;
	@JsonProperty("BoletoTransaction")
	private BoletoTransaction boletoTransaction;
	@JsonProperty("MerchantKey")
	private String merchantKey;
	@JsonProperty("OrderReference")
	private String orderReference;
	@JsonProperty("OrderKey")
	private String orderKey;
	@JsonProperty("AmountInCents")
	private Long amountInCents;
	@JsonProperty("AmountPaidInCents")
	private Long amountPaidInCents;
	@JsonProperty("OrderStatus")
	private String orderStatus;

	public CreditCardTransaction getCreditCardTransaction() {
		return creditCardTransaction;
	}

	public void setCreditCardTransaction(CreditCardTransaction creditCardTransaction) {
		this.creditCardTransaction = creditCardTransaction;
	}

	public BoletoTransaction getBoletoTransaction() {
		return boletoTransaction;
	}

	public void setBoletoTransaction(BoletoTransaction boletoTransaction) {
		this.boletoTransaction = boletoTransaction;
	}

	public String getMerchantKey() {
		return merchantKey;
	}

	public void setMerchantKey(String merchantKey) {
		this.merchantKey = merchantKey;
	}

	public String getOrderReference() {
		return orderReference;
	}

	public void setOrderReference(String orderReference) {
		this.orderReference = orderReference;
	}

	public String getOrderKey() {
		return orderKey;
	}

	public void setOrderKey(String orderKey) {
		this.orderKey = orderKey;
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

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
}