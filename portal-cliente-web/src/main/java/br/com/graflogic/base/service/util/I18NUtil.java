package br.com.graflogic.base.service.util;

import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

/**
 * 
 * @author gmazz
 *
 */
public class I18NUtil {

	public static String getMessage(String key) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		ResourceBundle bundle = app.getResourceBundle(context, "messages");
		return bundle.getString(key);
	}

	public static String getLabel(String key) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		ResourceBundle bundle = app.getResourceBundle(context, "labels");
		return bundle.getString(key);
	}
}