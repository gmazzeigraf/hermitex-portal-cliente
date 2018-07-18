package br.com.graflogic.hermitex.cliente.service.exception;

/**
 * 
 * @author gmazz
 *
 */
public class CorreiosException extends RuntimeException {

	private static final long serialVersionUID = 7253925076600703106L;

	public CorreiosException(String message) {
		super(message);
	}

	public CorreiosException(Throwable t) {
		super(t);
	}
}