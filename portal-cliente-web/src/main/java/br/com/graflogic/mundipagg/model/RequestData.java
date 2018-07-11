package br.com.graflogic.mundipagg.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class RequestData {

	@SerializedName("EcommerceCategory")
	private String ecommerceCategory;
	
	@SerializedName("IpAddress")
	private String ipAddress;
	
	@SerializedName("Origin")
	private String origin;
	
	@SerializedName("SessionId")
	private String sessionId;

	public String getEcommerceCategory() {
		return ecommerceCategory;
	}

	public void setEcommerceCategory(String ecommerceCategory) {
		this.ecommerceCategory = ecommerceCategory;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
