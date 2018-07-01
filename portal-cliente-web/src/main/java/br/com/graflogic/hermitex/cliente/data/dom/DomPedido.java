package br.com.graflogic.hermitex.cliente.data.dom;

import br.com.graflogic.base.data.dom.DomBase;

/**
 * 
 * @author gmazz
 *
 */
public class DomPedido {

	public static class DomStatus extends DomBase {
		public static final String PAGAMENTO_PENDENTE = "P";
		public static final String PAGO = "G";
		public static final String ENVIADO = "V";
		public static final String FINALIZADO = "Z";

		DomStatus() {
			mapa.put(PAGAMENTO_PENDENTE, "Pagamento Pendente");
			mapa.put(PAGO, "Pago");
			mapa.put(ENVIADO, "Enviado");
			mapa.put(FINALIZADO, "Finalizado");
		}
	}

	public static DomStatus domStatus = new DomStatus();

	public static class DomTipoPagamento extends DomBase {
		public static final String BOLETO = "B";
		public static final String CARTAO_CREDITO_1 = "1";
		public static final String CARTAO_CREDITO_2 = "2";

		DomTipoPagamento() {
			mapa.put(BOLETO, "Boleto");
			mapa.put(CARTAO_CREDITO_1, "Cartão de Crédito 1x");
			mapa.put(CARTAO_CREDITO_2, "Cartão de Crédito 2x");
		}
	}

	public static DomTipoPagamento domTipoPagamento = new DomTipoPagamento();
}