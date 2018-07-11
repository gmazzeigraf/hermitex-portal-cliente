package br.com.graflogic.mundipagg.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class DeliveryAddress {

	@SerializedName("City")
	private String city;
	
	@SerializedName("Complement")
	private String complement;
	
	@SerializedName("Country")
	private String country;
	
	@SerializedName("District")
	private String district;
	
	@SerializedName("Number")
	private String number;
	
	@SerializedName("State")
	private String state;
	
	@SerializedName("Street")
	private String street;
	
	@SerializedName("ZipCode")
	private String zipCode;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getComplement() {
		return complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

}
