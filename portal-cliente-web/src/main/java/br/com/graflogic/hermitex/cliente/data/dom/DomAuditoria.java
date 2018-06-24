package br.com.graflogic.hermitex.cliente.data.dom;

import br.com.graflogic.base.data.dom.DomBase;

/**
 * 
 * @author gmazz
 *
 */
public class DomAuditoria {

	public static class DomEventoAuditoriaUsuario extends DomBase {
		public static final String CADASTRO = "C";
		public static final String ATUALIZACAO = "A";
		public static final String INATIVACAO = "I";
		public static final String ATIVACAO = "T";
		public static final String GERACAO_NOVA_SENHA = "G";
		public static final String ALTERACAO_SENHA = "S";
		public static final String LOGOUT_FORCADO = "L";

		DomEventoAuditoriaUsuario() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(ATUALIZACAO, "Atualização");
			mapa.put(INATIVACAO, "Inativação");
			mapa.put(ATIVACAO, "Ativação");
			mapa.put(GERACAO_NOVA_SENHA, "Geração Nova Senha");
			mapa.put(ALTERACAO_SENHA, "Alteração Senha");
			mapa.put(LOGOUT_FORCADO, "Logout Forçado");
		}
	}

	public static class DomEventoAuditoriaPerfilUsuario extends DomBase {
		public static final String CADASTRO = "C";
		public static final String ATUALIZACAO = "A";
		public static final String INATIVACAO = "I";
		public static final String ATIVACAO = "T";

		DomEventoAuditoriaPerfilUsuario() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(ATUALIZACAO, "Atualização");
			mapa.put(INATIVACAO, "Inativação");
			mapa.put(ATIVACAO, "Ativação");
		}
	}

	public static class DomEventoAuditoriaCliente extends DomBase {
		public static final String CADASTRO = "C";
		public static final String ATUALIZACAO = "A";
		public static final String INATIVACAO = "I";
		public static final String ATIVACAO = "T";

		DomEventoAuditoriaCliente() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(ATUALIZACAO, "Atualização");
			mapa.put(INATIVACAO, "Inativação");
			mapa.put(ATIVACAO, "Ativação");
		}
	}

	public static class DomStatusProcessamento extends DomBase {
		public static final String ERRO = "E";
		public static final String FINALIZADO = "F";

		DomStatusProcessamento() {
			mapa.put(ERRO, "Erro");
			mapa.put(FINALIZADO, "Finalizado");
		}
	}

	public static DomStatusProcessamento domStatusProcessamento = new DomStatusProcessamento();
}