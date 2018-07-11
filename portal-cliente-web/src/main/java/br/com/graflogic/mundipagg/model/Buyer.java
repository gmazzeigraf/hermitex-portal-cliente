package br.com.graflogic.mundipagg.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class Buyer {

	@SerializedName("AddressCollection")
	private List<Address> addressCollection = null;
	
	@SerializedName("Birthdate")
	private String birthdate;
	
	@SerializedName("BuyerCategory")
	private String buyerCategory;
	
	@SerializedName("BuyerReference")
	private String buyerReference;
	
	@SerializedName("CreateDateInMerchant")
	private String createDateInMerchant;
	
	@SerializedName("DocumentNumber")
	private String documentNumber;
	
	@SerializedName("DocumentType")
	private String documentType;
	
	@SerializedName("Email")
	private String email;
	
	@SerializedName("EmailType")
	private String emailType;
	
	@SerializedName("FacebookId")
	private String facebookId;
	
	@SerializedName("Gender")
	private String gender;
	
	@SerializedName("HomePhone")
	private String homePhone;
	
	@SerializedName("MobilePhone")
	private String mobilePhone;
	
	@SerializedName("Name")
	private String name;
	
	@SerializedName("PersonType")
	private String personType;
	
	@SerializedName("TwitterId")
	private String twitterId;
	
	@SerializedName("WorkPhone")
	private String workPhone;

	public List<Address> getAddressCollection() {
		return addressCollection;
	}

	public void setAddressCollection(List<Address> addressCollection) {
		this.addressCollection = addressCollection;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getBuyerCategory() {
		return buyerCategory;
	}

	public void setBuyerCategory(String buyerCategory) {
		this.buyerCategory = buyerCategory;
	}

	public String getBuyerReference() {
		return buyerReference;
	}

	public void setBuyerReference(String buyerReference) {
		this.buyerReference = buyerReference;
	}

	public String getCreateDateInMerchant() {
		return createDateInMerchant;
	}

	public void setCreateDateInMerchant(String createDateInMerchant) {
		this.createDateInMerchant = createDateInMerchant;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailType() {
		return emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPersonType() {
		return personType;
	}

	public void setPersonType(String personType) {
		this.personType = personType;
	}

	public String getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

}
