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
		public static final String CANCELADO = "C";

		DomStatus() {
			mapa.put(PAGAMENTO_PENDENTE, "Pagamento Pendente");
			mapa.put(PAGO, "Pago");
			mapa.put(ENVIADO, "Enviado");
			mapa.put(FINALIZADO, "Finalizado");
			mapa.put(CANCELADO, "Cancelado");
		}
	}

	public static DomStatus domStatus = new DomStatus();

	public static class DomTipoFormaPagamento extends DomBase {
		public static final String BOLETO = "B";
		public static final String CARTAO_CREDITO = "C";
		public static final String FATURAMENTO = "F";

		DomTipoFormaPagamento() {
			mapa.put(BOLETO, "Boleto");
			mapa.put(CARTAO_CREDITO, "Cartão de crédito");
			mapa.put(FATURAMENTO, "Faturamento");
		}
	}

	public static DomTipoFormaPagamento domTipoFormaPagamento = new DomTipoFormaPagamento();

	public static class DomStatusFormaPagamento extends DomBase {
		public static final String ATIVA = "A";
		public static final String INATIVA = "I";

		DomStatusFormaPagamento() {
			mapa.put(ATIVA, "Ativa");
			mapa.put(INATIVA, "Inativa");
		}
	}

	public static DomStatusFormaPagamento domStatusFormaPagamento = new DomStatusFormaPagamento();

	public static class DomStatusBoleto extends DomBase {
		public static final String PENDENTE = "P";
		public static final String PAGO = "G";

		DomStatusBoleto() {
			mapa.put(PENDENTE, "Pendente");
			mapa.put(PAGO, "Pago");
		}
	}

	public static DomStatusBoleto domStatusBoleto = new DomStatusBoleto();

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

	public static class DomStatusJanelaCompra extends DomBase {
		public static final String CADASTRADA = "C";
		public static final String FECHADA = "F";
		public static final String REABERTA = "R";
		public static final String CANCELADA = "L";

		DomStatusJanelaCompra() {
			mapa.put(CADASTRADA, "Cadastrada");
			mapa.put(FECHADA, "Fechada");
			mapa.put(REABERTA, "Reaberta");
			mapa.put(CANCELADA, "Cancelada");
		}
	}

	public static DomStatusJanelaCompra domStatusJanelaCompra = new DomStatusJanelaCompra();

	public static class DomStatusTroca extends DomBase {
		public static final String CADASTRADA = "C";
		public static final String FINALIZADA = "F";
		public static final String CANCELADA = "L";

		DomStatusTroca() {
			mapa.put(CADASTRADA, "Cadastrada");
			mapa.put(FINALIZADA, "Finalizada");
			mapa.put(CANCELADA, "Cancelada");
		}
	}

	public static DomStatusTroca domStatusTroca = new DomStatusTroca();

	public static class DomServicoFrete extends DomBase {
		public static final String RETIRADA_HERMITEX = "00001";
		public static final String SEDEX = "04162";
		public static final String PAC = "04812";

		DomServicoFrete() {
			mapa.put(RETIRADA_HERMITEX, "Retirada na Hermitex. Cidade de Campinas - SP");
			mapa.put(SEDEX, "SEDEX");
			mapa.put(PAC, "PAC");
		}
	}

	public static DomServicoFrete domServicoFrete = new DomServicoFrete();
}