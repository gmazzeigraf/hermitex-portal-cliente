package br.com.graflogic.mundipagg.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class SaleRequest {

	@SerializedName("BoletoTransactionCollection")
	private List<BoletoTransaction> boletoTransactionCollection = null;
	
	@SerializedName("Buyer")
	private Buyer buyer;
	
	@SerializedName("CreditCardTransactionCollection")
	private List<CreditCardTransaction> creditCardTransactionCollection = null;
	
	@SerializedName("Merchant")
	private Merchant merchant;
	
	@SerializedName("Options")
	private Options options;
	
	@SerializedName("Order")
	private Order order;
	
	@SerializedName("RequestData")
	private RequestData requestData;
	
	@SerializedName("ShoppingCartCollection")
	private List<ShoppingCart> shoppingCartCollection = null;

	public List<BoletoTransaction> getBoletoTransactionCollection() {
		return boletoTransactionCollection;
	}

	public void setBoletoTransactionCollection(List<BoletoTransaction> boletoTransactionCollection) {
		this.boletoTransactionCollection = boletoTransactionCollection;
	}

	public Buyer getBuyer() {
		return buyer;
	}

	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	public List<CreditCardTransaction> getCreditCardTransactionCollection() {
		return creditCardTransactionCollection;
	}

	public void setCreditCardTransactionCollection(List<CreditCardTransaction> creditCardTransactionCollection) {
		this.creditCardTransactionCollection = creditCardTransactionCollection;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public RequestData getRequestData() {
		return requestData;
	}

	public void setRequestData(RequestData requestData) {
		this.requestData = requestData;
	}

	public List<ShoppingCart> getShoppingCartCollection() {
		return shoppingCartCollection;
	}

	public void setShoppingCartCollection(List<ShoppingCart> shoppingCartCollection) {
		this.shoppingCartCollection = shoppingCartCollection;
	}
}