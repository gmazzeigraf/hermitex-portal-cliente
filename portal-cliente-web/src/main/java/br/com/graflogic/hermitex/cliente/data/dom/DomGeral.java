package br.com.graflogic.hermitex.cliente.data.dom;

import br.com.graflogic.base.data.dom.DomBase;

/**
 * 
 * @author gmazz
 *
 */
public class DomGeral {

	public static class DomBoolean extends DomBase {
		public static final String SIM = "S";
		public static final String NAO = "N";

		DomBoolean() {
			mapa.put(SIM, "Sim");
			mapa.put(NAO, "Não");
		}
	}

	public static DomBoolean domBoolean = new DomBoolean();

	public static class DomStatusSimple extends DomBase {
		public static final String ATIVO = "A";
		public static final String INATIVO = "I";

		DomStatusSimple() {
			mapa.put(ATIVO, "Ativo");
			mapa.put(INATIVO, "Inativo");
		}
	}

	public static DomStatusSimple domStatusSimple = new DomStatusSimple();

}