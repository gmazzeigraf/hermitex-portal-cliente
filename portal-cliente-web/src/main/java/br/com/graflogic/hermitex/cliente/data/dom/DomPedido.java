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
}