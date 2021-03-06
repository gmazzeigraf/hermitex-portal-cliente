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

	public static class DomPedidoFaturado extends DomBase {
		public static final String SIM = "S";
		public static final String NAO = "N";
		public static final String TODOS = "T";

		DomPedidoFaturado() {
			mapa.put(SIM, "Sim");
			mapa.put(NAO, "Não");
			mapa.put(TODOS, "Todos");
		}
	}

	public static DomPedidoFaturado domPedidoFaturado = new DomPedidoFaturado();
}