package br.com.graflogic.hermitex.cliente.service.exception;

/**
 * 
 * @author gmazz
 *
 */
public class DadosInvalidosException extends RuntimeException {

	private static final long serialVersionUID = 7772209690504269929L;

	public DadosInvalidosException(String message) {
		super(message);
	}
}