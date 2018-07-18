package br.com.graflogic.correios.client;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author gmazz
 *
 */
public class CorreiosClientIT {

	private CorreiosClient client;

	@Before
	public void setUp() {
		client = new CorreiosClient("http://ws.correios.com.br/calculador/CalcPrecoPrazo.asmx", "12009903", "Y7T27", 5000, 15000);
	}

	@Test
	public void testCalculaPrecoPrazo() {
		client.calculaPrecoPrazo(Arrays.asList("04014"), "13041311", "13219816", 1, new BigDecimal("20"), new BigDecimal("10"), new BigDecimal("30"));
	}
}