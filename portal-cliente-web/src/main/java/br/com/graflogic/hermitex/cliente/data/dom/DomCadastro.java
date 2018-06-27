package br.com.graflogic.hermitex.cliente.data.dom;

import br.com.graflogic.base.data.dom.DomBase;

/**
 * 
 * @author gmazz
 *
 */
public class DomCadastro {

	public static class DomStatusCliente extends DomBase {
		public static final String ATIVO = "A";
		public static final String INATIVO = "I";

		DomStatusCliente() {
			mapa.put(ATIVO, "Ativo");
			mapa.put(INATIVO, "Inativo");
		}
	}

	public static DomStatusCliente domStatusCliente = new DomStatusCliente();

	public static class DomTipoEndereco extends DomBase {
		public static final String FATURAMENTO = "F";
		public static final String ENTREGA = "T";

		DomTipoEndereco() {
			mapa.put(FATURAMENTO, "Faturamento");
			mapa.put(ENTREGA, "Entrega");
		}
	}

	public static DomTipoEndereco domTipoEndereco = new DomTipoEndereco();
}