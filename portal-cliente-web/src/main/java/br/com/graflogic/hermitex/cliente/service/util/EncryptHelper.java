package br.com.graflogic.hermitex.cliente.service.util;

/**
 * 
 * @author gmazz
 *
 */
public class EncryptHelper extends br.com.graflogic.utilities.datautil.encrypt.EncryptHelper {

	private static final String SECRET_KEY_ALGORITHM = "SHA-256";

	public static String encrypt(String originalValue) {
		return encrypt(originalValue, SECRET_KEY_ALGORITHM);
	}
}