package br.com.graflogic.hermitex.cliente.data.util;

/**
 * 
 * @author gmazz
 *
 */
public enum ConfiguracaoEnum {

	USUARIO_EMAIL_SENHA_TITULO("usuario.email_senha.titulo"), USUARIO_EMAIL_SENHA_CONTEUDO(
			"usuario.email_senha.conteudo"), USUARIO_EMAIL_NOVA_SENHA_TITULO(
					"usuario.email_nova_senha.titulo"), USUARIO_EMAIL_NOVA_SENHA_CONTEUDO("usuario.email_nova_senha.conteudo"),

	EMAIL_SERVIDOR("email.servidor"), EMAIL_PORTA("email.porta"), EMAIL_USUARIO("email.usuario"), EMAIL_SENHA("email.senha"), EMAIL_SSL("email.ssl"),

	WEB_URL("web.url"),

	PAGAMENTO_BOLETO_CODIGO_BANCO("pagamento_boleto.codigo_banco"), PAGAMENTO_BOLETO_INSTRUCAO("pagamento_boleto.instrucao"), PAGAMENTO_URL(
			"pagamento.url"), PAGAMENTO_MERCHANT_KEY("pagamento.merchant_key"),

	CORREIOS_CODIGO_EMPRESA("correios.codigo_empresa"), CORREIOS_SENHA("correios.senha"), CORREIOS_URL("correios.url")

	;

	private final String codigo;

	private ConfiguracaoEnum(String codigo) {
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}

	public static ConfiguracaoEnum valueOfCodigo(String codigo) {
		for (ConfiguracaoEnum codigoEnum : ConfiguracaoEnum.values()) {
			if (codigoEnum.getCodigo().equals(codigo)) {
				return codigoEnum;
			}
		}

		return null;
	}
}