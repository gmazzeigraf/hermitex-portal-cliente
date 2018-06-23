package br.com.graflogic.hermitex.cliente.service.exception;

/**
 * 
 * @author gmazz
 *
 */
public class ResultadoNaoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = -2076821882822058920L;

	public ResultadoNaoEncontradoException() {
		super();
	}

	public ResultadoNaoEncontradoException(String message) {
		super(message);
	}
}