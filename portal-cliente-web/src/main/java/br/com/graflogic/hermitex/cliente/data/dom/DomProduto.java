package br.com.graflogic.hermitex.cliente.data.dom;

import br.com.graflogic.base.data.dom.DomBase;

/**
 * 
 * @author gmazz
 *
 */
public class DomProduto {

	public static class DomStatusTamanho extends DomBase {
		public static final String ATIVO = "A";
		public static final String INATIVO = "I";

		DomStatusTamanho() {
			mapa.put(ATIVO, "Ativo");
			mapa.put(INATIVO, "Inativo");
		}
	}

	public static DomStatusTamanho domStatusTamanho = new DomStatusTamanho();

	public static class DomStatusCategoria extends DomBase {
		public static final String ATIVO = "A";
		public static final String INATIVO = "I";

		DomStatusCategoria() {
			mapa.put(ATIVO, "Ativo");
			mapa.put(INATIVO, "Inativo");
		}
	}

	public static DomStatusCategoria domStatusCategoria = new DomStatusCategoria();

	public static class DomStatusSetor extends DomBase {
		public static final String ATIVO = "A";
		public static final String INATIVO = "I";

		DomStatusSetor() {
			mapa.put(ATIVO, "Ativo");
			mapa.put(INATIVO, "Inativo");
		}
	}

	public static DomStatusSetor domStatusSetor = new DomStatusSetor();
}