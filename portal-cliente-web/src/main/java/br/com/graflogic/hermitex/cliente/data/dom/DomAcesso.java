package br.com.graflogic.hermitex.cliente.data.dom;

import br.com.graflogic.base.data.dom.DomBase;

/**
 * 
 * @author gmazz
 *
 */
public class DomAcesso {

	public static class DomTipoUsuario extends DomBase {
		public static final String ADMINISTRADOR = "A";
		public static final String CLIENTE = "C";
		public static final String FILIAL = "F";

		DomTipoUsuario() {
			mapa.put(ADMINISTRADOR, "Administrador");
			mapa.put(CLIENTE, "Cliente");
			mapa.put(FILIAL, "Filial");
		}
	}

	public static DomTipoUsuario domTipoUsuario = new DomTipoUsuario();

	public static class DomStatusUsuario extends DomBase {
		public static final String ATIVO = "A";
		public static final String INATIVO = "I";

		DomStatusUsuario() {
			mapa.put(ATIVO, "Ativo");
			mapa.put(INATIVO, "Inativo");
		}
	}

	public static DomStatusUsuario domStatusUsuario = new DomStatusUsuario();

	public static class DomStatusSenhaUsuario extends DomBase {
		public static final String TEMPORARIA = "T";
		public static final String DEFINITIVA = "D";

		DomStatusSenhaUsuario() {
			mapa.put(TEMPORARIA, "Temporária");
			mapa.put(DEFINITIVA, "Definitiva");
		}
	}

	public static DomStatusSenhaUsuario domStatusSenhaUsuario = new DomStatusSenhaUsuario();

	public static class DomStatusPerfilUsuario extends DomBase {
		public static final String ATIVO = "A";
		public static final String INATIVO = "I";

		DomStatusPerfilUsuario() {
			mapa.put(ATIVO, "Ativo");
			mapa.put(INATIVO, "Inativo");
		}
	}

	public static DomStatusPerfilUsuario domStatusPerfilUsuario = new DomStatusPerfilUsuario();

	public static class DomPermissaoAcesso extends DomBase {
		public static final String ROLE_PEDIDO = "ROLE_PEDIDO";

		DomPermissaoAcesso() {
			mapa.put(ROLE_PEDIDO, "Pedido");
		}
	}
}