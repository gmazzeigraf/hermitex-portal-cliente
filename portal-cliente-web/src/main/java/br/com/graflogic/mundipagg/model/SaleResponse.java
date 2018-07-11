package br.com.graflogic.mundipagg.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class SaleResponse {

	@SerializedName("ErrorReport")
	private ErrorReport errorReport;
	
	@SerializedName("InternalTime")
	private Integer internalTime;
	
	@SerializedName("MerchantKey")
	private String merchantKey;
	
	@SerializedName("RequestKey")
	private String requestKey;
	
	@SerializedName("BoletoTransactionResultCollection")
	private List<BoletoTransactionResult> boletoTransactionResultCollection = null;
	
	@SerializedName("BuyerKey")
	private String buyerKey;
	
	@SerializedName("CreditCardTransactionResultCollection")
	private List<CreditCardTransactionResult> creditCardTransactionResultCollection = null;
	
	@SerializedName("OrderResult")
	private OrderResult orderResult;

	public ErrorReport getErrorReport() {
		return errorReport;
	}

	public void setErrorReport(ErrorReport errorReport) {
		this.errorReport = errorReport;
	}

	public Integer getInternalTime() {
		return internalTime;
	}

	public void setInternalTime(Integer internalTime) {
		this.internalTime = internalTime;
	}

	public String getMerchantKey() {
		return merchantKey;
	}

	public void setMerchantKey(String merchantKey) {
		this.merchantKey = merchantKey;
	}

	public String getRequestKey() {
		return requestKey;
	}

	public void setRequestKey(String requestKey) {
		this.requestKey = requestKey;
	}

	public List<BoletoTransactionResult> getBoletoTransactionResultCollection() {
		return boletoTransactionResultCollection;
	}

	public void setBoletoTransactionResultCollection(List<BoletoTransactionResult> boletoTransactionResultCollection) {
		this.boletoTransactionResultCollection = boletoTransactionResultCollection;
	}

	public String getBuyerKey() {
		return buyerKey;
	}

	public void setBuyerKey(String buyerKey) {
		this.buyerKey = buyerKey;
	}

	public List<CreditCardTransactionResult> getCreditCardTransactionResultCollection() {
		return creditCardTransactionResultCollection;
	}

	public void setCreditCardTransactionResultCollection(List<CreditCardTransactionResult> creditCardTransactionResultCollection) {
		this.creditCardTransactionResultCollection = creditCardTransactionResultCollection;
	}

	public OrderResult getOrderResult() {
		return orderResult;
	}

	public void setOrderResult(OrderResult orderResult) {
		this.orderResult = orderResult;
	}
}