package br.com.graflogic.base.service.gson;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * @author gmazz
 *
 */
public class GsonUtil {

	public static final Gson gson;

	static {
		gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonUTCDateAdapter()).create();
	}
}