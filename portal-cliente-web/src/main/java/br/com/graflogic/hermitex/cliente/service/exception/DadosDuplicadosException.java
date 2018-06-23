package br.com.graflogic.hermitex.cliente.service.exception;

/**
 * 
 * @author gmazz
 *
 */
public class DadosDuplicadosException extends RuntimeException {

	private static final long serialVersionUID = -1821278975911312003L;

	public DadosDuplicadosException() {
		super();
	}

	public DadosDuplicadosException(String message) {
		super(message);
	}
}