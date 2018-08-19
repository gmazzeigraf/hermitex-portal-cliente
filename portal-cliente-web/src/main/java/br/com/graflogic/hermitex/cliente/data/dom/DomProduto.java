package br.com.graflogic.hermitex.cliente.data.dom;

import br.com.graflogic.base.data.dom.DomBase;

/**
 * 
 * @author gmazz
 *
 */
public class DomProduto {

	public static class DomStatus extends DomBase {
		public static final String ATIVO = "A";
		public static final String INATIVO = "I";

		DomStatus() {
			mapa.put(ATIVO, "Ativo");
			mapa.put(INATIVO, "Inativo");
		}
	}

	public static DomStatus domStatus = new DomStatus();

	public static class DomStatusTamanho extends DomBase {
		public static final String ATIVO = "A";
		public static final String INATIVO = "I";

		DomStatusTamanho() {
			mapa.put(ATIVO, "Ativo");
			mapa.put(INATIVO, "Inativo");
		}
	}

	public static DomStatusTamanho domStatusTamanho = new DomStatusTamanho();

	public static class DomStatusEmbalagem extends DomBase {
		public static final String ATIVO = "A";
		public static final String INATIVO = "I";

		DomStatusEmbalagem() {
			mapa.put(ATIVO, "Ativo");
			mapa.put(INATIVO, "Inativo");
		}
	}

	public static DomStatusEmbalagem domStatusEmbalagem = new DomStatusEmbalagem();

	public static class DomStatusCategoria extends DomBase {
		public static final String ATIVA = "A";
		public static final String INATIVA = "I";

		DomStatusCategoria() {
			mapa.put(ATIVA, "Ativa");
			mapa.put(INATIVA, "Inativa");
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

	public static class DomGenero extends DomBase {
		public static final String MASCULINO = "M";
		public static final String FEMININO = "F";
		public static final String UNISSEX = "U";

		DomGenero() {
			mapa.put(MASCULINO, "Masculino");
			mapa.put(FEMININO, "Feminino");
			mapa.put(UNISSEX, "Unissex");
		}
	}

	public static DomGenero domGenero = new DomGenero();

	public static class DomTipo extends DomBase {
		public static final String ROUPA = "R";
		public static final String SAPATO = "S";
		public static final String BOLSA = "B";

		DomTipo() {
			mapa.put(ROUPA, "Roupa");
			mapa.put(SAPATO, "Sapato");
			mapa.put(BOLSA, "Bolsa");
		}
	}

	public static DomTipo domTipo = new DomTipo();

	public static class DomStatusSolicitacaoEstoque extends DomBase {
		public static final String CADASTRADA = "C";
		public static final String FINALIZADA = "F";

		DomStatusSolicitacaoEstoque() {
			mapa.put(CADASTRADA, "Cadastrada");
			mapa.put(FINALIZADA, "Finalizada");
		}
	}

	public static DomStatusSolicitacaoEstoque domStatusSolicitacaoEstoque = new DomStatusSolicitacaoEstoque();
}