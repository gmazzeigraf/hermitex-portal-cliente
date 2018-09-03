package br.com.graflogic.mundipagg.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class Options {

	@SerializedName("AntiFraudServiceCode")
	private Integer antiFraudServiceCode;

	@SerializedName("CurrencyIso")
	private String currencyIso;

	@SerializedName("IsAntiFraudEnabled")
	private Boolean isAntiFraudEnabled;

	@SerializedName("Retries")
	private Integer retries;

	@SerializedName("DaysToAddInBoletoExpirationDate")
	private Integer daysToAddInBoletoExpirationDate;

	@SerializedName("PaymentMethodCode")
	private Integer paymentMethodCode;

	@SerializedName("SoftDescriptorText")
	private String softDescriptorText;

	@SerializedName("NotificationUrl")
	private String notificationUrl;

	public Integer getAntiFraudServiceCode() {
		return antiFraudServiceCode;
	}

	public void setAntiFraudServiceCode(Integer antiFraudServiceCode) {
		this.antiFraudServiceCode = antiFraudServiceCode;
	}

	public String getCurrencyIso() {
		return currencyIso;
	}

	public void setCurrencyIso(String currencyIso) {
		this.currencyIso = currencyIso;
	}

	public Boolean getIsAntiFraudEnabled() {
		return isAntiFraudEnabled;
	}

	public void setIsAntiFraudEnabled(Boolean isAntiFraudEnabled) {
		this.isAntiFraudEnabled = isAntiFraudEnabled;
	}

	public Integer getRetries() {
		return retries;
	}

	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	public Integer getDaysToAddInBoletoExpirationDate() {
		return daysToAddInBoletoExpirationDate;
	}

	public void setDaysToAddInBoletoExpirationDate(Integer daysToAddInBoletoExpirationDate) {
		this.daysToAddInBoletoExpirationDate = daysToAddInBoletoExpirationDate;
	}

	public Integer getPaymentMethodCode() {
		return paymentMethodCode;
	}

	public void setPaymentMethodCode(Integer paymentMethodCode) {
		this.paymentMethodCode = paymentMethodCode;
	}

	public String getSoftDescriptorText() {
		return softDescriptorText;
	}

	public void setSoftDescriptorText(String softDescriptorText) {
		this.softDescriptorText = softDescriptorText;
	}

	public String getNotificationUrl() {
		return notificationUrl;
	}

	public void setNotificationUrl(String notificationUrl) {
		this.notificationUrl = notificationUrl;
	}
}