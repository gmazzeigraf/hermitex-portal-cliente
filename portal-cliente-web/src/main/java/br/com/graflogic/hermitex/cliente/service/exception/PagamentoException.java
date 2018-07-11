package br.com.graflogic.hermitex.cliente.service.exception;

/**
 * 
 * @author gmazz
 *
 */
public class PagamentoException extends RuntimeException {

	private static final long serialVersionUID = -787378527155588292L;

	public PagamentoException(String message) {
		super(message);
	}

	public PagamentoException(Throwable t) {
		super(t);
	}
}