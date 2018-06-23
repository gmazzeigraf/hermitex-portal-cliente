package br.com.graflogic.hermitex.cliente.service.util;

/**
 * 
 * @author gmazz
 *
 */
public class Generator extends br.com.graflogic.commonutil.util.Generator {

	public static String geraSenhaUsuario() {
		return generate(8, null).toUpperCase();
	}
}