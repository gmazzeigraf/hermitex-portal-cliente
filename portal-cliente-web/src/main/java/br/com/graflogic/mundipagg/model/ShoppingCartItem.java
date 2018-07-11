package br.com.graflogic.mundipagg.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class ShoppingCartItem {

	@SerializedName("Description")
	private String description;
	
	@SerializedName("DiscountAmountInCents")
	private Integer discountAmountInCents;
	
	@SerializedName("ItemReference")
	private String itemReference;
	
	@SerializedName("Name")
	private String name;
	
	@SerializedName("Quantity")
	private Integer quantity;
	
	@SerializedName("TotalCostInCents")
	private Integer totalCostInCents;
	
	@SerializedName("UnitCostInCents")
	private Integer unitCostInCents;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getDiscountAmountInCents() {
		return discountAmountInCents;
	}

	public void setDiscountAmountInCents(Integer discountAmountInCents) {
		this.discountAmountInCents = discountAmountInCents;
	}

	public String getItemReference() {
		return itemReference;
	}

	public void setItemReference(String itemReference) {
		this.itemReference = itemReference;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getTotalCostInCents() {
		return totalCostInCents;
	}

	public void setTotalCostInCents(Integer totalCostInCents) {
		this.totalCostInCents = totalCostInCents;
	}

	public Integer getUnitCostInCents() {
		return unitCostInCents;
	}

	public void setUnitCostInCents(Integer unitCostInCents) {
		this.unitCostInCents = unitCostInCents;
	}

}
