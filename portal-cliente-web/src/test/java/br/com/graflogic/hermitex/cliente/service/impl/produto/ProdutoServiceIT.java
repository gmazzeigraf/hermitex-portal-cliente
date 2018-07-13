package br.com.graflogic.hermitex.cliente.service.impl.produto;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author gmazz
 *
 */
public class ProdutoServiceIT {

	private ProdutoService service;

	@Before
	public void setUp() {
		service = new ProdutoService();
	}

	@Test
	public void test() {
		String tabelaMedidas = service.geraTabelaMedidas(
				"[{\"conteudo\":\"TAMANHO;32;33;34;35;36\"};{\"conteudo\":\"BUSTO;0;2;3;4;5\"};{\"conteudo\":\"CINTURA;2;4;5;6;7\"}]");

		System.out.println(tabelaMedidas);
	}
}