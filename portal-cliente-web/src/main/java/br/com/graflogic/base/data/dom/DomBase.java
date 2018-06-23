package br.com.graflogic.base.data.dom;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author gmazz
 *
 */
public class DomBase {

	protected Map<Object, String> mapa = new LinkedHashMap<Object, String>();

	public String getDeValor(Object codigo) {
		if (codigo == null) {
			return "";
		}

		return mapa.get(codigo);
	}

	public Map<String, Object> getMap() {
		Map<String, Object> mapaRetorno = new LinkedHashMap<String, Object>();

		for (Map.Entry<Object, String> entry : mapa.entrySet()) {
			mapaRetorno.put(entry.getValue(), entry.getKey());
		}

		return mapaRetorno;
	}
}