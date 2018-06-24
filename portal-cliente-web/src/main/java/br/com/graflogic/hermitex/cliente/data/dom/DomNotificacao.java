package br.com.graflogic.hermitex.cliente.data.dom;

import br.com.graflogic.base.data.dom.DomBase;

/**
 * 
 * @author gmazz
 *
 */
public class DomNotificacao {

	public static class DomStatus extends DomBase {
		public static final String PENDENTE = "P";
		public static final String ERRO = "R";
		public static final String ENVIADA = "E";
		public static final String DADOS_INVALIDOS = "I";

		DomStatus() {
			mapa.put(PENDENTE, "Pendente");
			mapa.put(ERRO, "Erro");
			mapa.put(ENVIADA, "Enviada");
			mapa.put(DADOS_INVALIDOS, "Dados Inválidos");
		}
	}

	public static DomStatus domStatus = new DomStatus();

	public static class DomTipo extends DomBase {
		public static final String EMAIL = "E";

		DomTipo() {
			mapa.put(EMAIL, "E-mail");
		}
	}

	public static DomTipo domTipo = new DomTipo();
}