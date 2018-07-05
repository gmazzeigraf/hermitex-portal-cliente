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
			mapa.put(BOLETO, "Boleto 1x");
			mapa.put(CARTAO_CREDITO_1, "Cartão de Crédito 1x");
			mapa.put(CARTAO_CREDITO_2, "Cartão de Crédito 2x");
		}
	}

	public static DomTipoPagamento domTipoPagamento = new DomTipoPagamento();

	public static class DomBandeiraCartaoCredito extends DomBase {
		public static final String ELO = "Elo";
		public static final String MASTERCARD = "Mastercard";
		public static final String VISA = "Visa";
		public static final String AMEX = "Amex";
		public static final String JCB = "JCB";
		public static final String AURA = "Aura";
		public static final String HIPERCARD = "Hipercard";
		public static final String DINERS = "Diners";
		public static final String DISCOVER = "Discover";

		DomBandeiraCartaoCredito() {
			mapa.put(ELO, "Elo");
			mapa.put(MASTERCARD, "Mastercard");
			mapa.put(VISA, "Visa");
			mapa.put(AMEX, "Amex");
			mapa.put(JCB, "JCB");
			mapa.put(AURA, "Aura");
			mapa.put(HIPERCARD, "Hipercard");
			mapa.put(DINERS, "Diners");
			mapa.put(DISCOVER, "Discover");
		}
	}

	public static DomBandeiraCartaoCredito domBandeiraCartaoCredito = new DomBandeiraCartaoCredito();

}