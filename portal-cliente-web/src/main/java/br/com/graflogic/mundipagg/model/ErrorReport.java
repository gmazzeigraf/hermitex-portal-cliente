package br.com.graflogic.mundipagg.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author gmazz
 *
 */
public class ErrorReport {

	@SerializedName("Category")
	private String category;
	
	@SerializedName("ErrorItemCollection")
	private List<ErrorItem> errorItemCollection = null;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<ErrorItem> getErrorItemCollection() {
		return errorItemCollection;
	}

	public void setErrorItemCollection(List<ErrorItem> errorItemCollection) {
		this.errorItemCollection = errorItemCollection;
	}

}
