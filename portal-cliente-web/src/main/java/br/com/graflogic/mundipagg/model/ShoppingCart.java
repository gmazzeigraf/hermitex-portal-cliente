package br.com.graflogic.mundipagg.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class ShoppingCart {

	@SerializedName("DeliveryAddress")
	private DeliveryAddress deliveryAddress;

	@SerializedName("DeliveryDeadline")
	private String deliveryDeadline;
	
	@SerializedName("EstimatedDeliveryDate")
	private String estimatedDeliveryDate;
	
	@SerializedName("FreightCostInCents")
	private Integer freightCostInCents;
	
	@SerializedName("ShippingCompany")
	private String shippingCompany;
	
	@SerializedName("ShoppingCartItemCollection")
	private List<ShoppingCartItem> shoppingCartItemCollection = null;

	public DeliveryAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getDeliveryDeadline() {
		return deliveryDeadline;
	}

	public void setDeliveryDeadline(String deliveryDeadline) {
		this.deliveryDeadline = deliveryDeadline;
	}

	public String getEstimatedDeliveryDate() {
		return estimatedDeliveryDate;
	}

	public void setEstimatedDeliveryDate(String estimatedDeliveryDate) {
		this.estimatedDeliveryDate = estimatedDeliveryDate;
	}

	public Integer getFreightCostInCents() {
		return freightCostInCents;
	}

	public void setFreightCostInCents(Integer freightCostInCents) {
		this.freightCostInCents = freightCostInCents;
	}

	public String getShippingCompany() {
		return shippingCompany;
	}

	public void setShippingCompany(String shippingCompany) {
		this.shippingCompany = shippingCompany;
	}

	public List<ShoppingCartItem> getShoppingCartItemCollection() {
		return shoppingCartItemCollection;
	}

	public void setShoppingCartItemCollection(List<ShoppingCartItem> shoppingCartItemCollection) {
		this.shoppingCartItemCollection = shoppingCartItemCollection;
	}

}
