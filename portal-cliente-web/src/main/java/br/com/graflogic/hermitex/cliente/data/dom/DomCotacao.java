package br.com.graflogic.hermitex.cliente.data.dom;

import br.com.graflogic.base.data.dom.DomBase;

/**
 * 
 * @author gmazz
 *
 */
public class DomCotacao {

	public static class DomStatus extends DomBase {
		public static final String GERADA = "G";
		public static final String FINALIZADA = "Z";
		public static final String CANCELADA = "C";

		DomStatus() {
			mapa.put(GERADA, "Gerada");
			mapa.put(FINALIZADA, "Finalizada");
			mapa.put(CANCELADA, "Cancelada");
		}
	}

	public static DomStatus domStatus = new DomStatus();

	public static class DomTipoFormaPagamento extends DomBase {
		public static final String BOLETO = "B";
		public static final String FATURAMENTO = "F";

		DomTipoFormaPagamento() {
			mapa.put(BOLETO, "Boleto");
			mapa.put(FATURAMENTO, "Faturamento");
		}
	}

	public static DomTipoFormaPagamento domTipoFormaPagamento = new DomTipoFormaPagamento();

	public static class DomServicoFrete extends DomBase {
		public static final String RETIRADA_HERMITEX = "00001";
		public static final String TRANSPORADORA = "00002";
		public static final String SEDEX = "04162";
		public static final String PAC = "04812";

		DomServicoFrete() {
			mapa.put(RETIRADA_HERMITEX, "Retirada na Hermitex. Cidade de Campinas - SP");
			mapa.put(TRANSPORADORA, "Transportadora");
			mapa.put(SEDEX, "SEDEX");
			mapa.put(PAC, "PAC");
		}
	}

	public static DomServicoFrete domServicoFrete = new DomServicoFrete();
}