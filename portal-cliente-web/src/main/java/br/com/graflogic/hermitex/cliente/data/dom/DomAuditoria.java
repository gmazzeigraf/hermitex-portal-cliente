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

	public static class DomEventoAuditoriaRepresentante extends DomBase {
		public static final String CADASTRO = "C";
		public static final String ATUALIZACAO = "A";
		public static final String INATIVACAO = "I";
		public static final String ATIVACAO = "T";

		DomEventoAuditoriaRepresentante() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(ATUALIZACAO, "Atualização");
			mapa.put(INATIVACAO, "Inativação");
			mapa.put(ATIVACAO, "Ativação");
		}
	}

	public static class DomEventoAuditoriaFilial extends DomBase {
		public static final String CADASTRO = "C";
		public static final String ATUALIZACAO = "A";
		public static final String INATIVACAO = "I";
		public static final String ATIVACAO = "T";
		public static final String BLOQUEIO_COMPRA = "B";
		public static final String DESBLOQUEIO_COMPRA = "D";

		DomEventoAuditoriaFilial() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(ATUALIZACAO, "Atualização");
			mapa.put(INATIVACAO, "Inativação");
			mapa.put(ATIVACAO, "Ativação");
			mapa.put(BLOQUEIO_COMPRA, "Bloqueio Compra");
			mapa.put(DESBLOQUEIO_COMPRA, "Desbloqueio Compra");
		}
	}

	public static class DomEventoAuditoriaProduto extends DomBase {
		public static final String CADASTRO = "C";
		public static final String ATUALIZACAO = "A";
		public static final String INATIVACAO = "I";
		public static final String ATIVACAO = "T";

		DomEventoAuditoriaProduto() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(ATUALIZACAO, "Atualização");
			mapa.put(INATIVACAO, "Inativação");
			mapa.put(ATIVACAO, "Ativação");
		}
	}

	public static class DomEventoAuditoriaTamanhoProduto extends DomBase {
		public static final String CADASTRO = "C";
		public static final String ATUALIZACAO = "A";
		public static final String INATIVACAO = "I";
		public static final String ATIVACAO = "T";

		DomEventoAuditoriaTamanhoProduto() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(ATUALIZACAO, "Atualização");
			mapa.put(INATIVACAO, "Inativação");
			mapa.put(ATIVACAO, "Ativação");
		}
	}

	public static class DomEventoAuditoriaEmbalagem extends DomBase {
		public static final String CADASTRO = "C";
		public static final String ATUALIZACAO = "A";
		public static final String INATIVACAO = "I";
		public static final String ATIVACAO = "T";

		DomEventoAuditoriaEmbalagem() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(ATUALIZACAO, "Atualização");
			mapa.put(INATIVACAO, "Inativação");
			mapa.put(ATIVACAO, "Ativação");
		}
	}

	public static class DomEventoAuditoriaCategoriaProduto extends DomBase {
		public static final String CADASTRO = "C";
		public static final String ATUALIZACAO = "A";
		public static final String INATIVACAO = "I";
		public static final String ATIVACAO = "T";

		DomEventoAuditoriaCategoriaProduto() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(ATUALIZACAO, "Atualização");
			mapa.put(INATIVACAO, "Inativação");
			mapa.put(ATIVACAO, "Ativação");
		}
	}

	public static class DomEventoAuditoriaSetorProduto extends DomBase {
		public static final String CADASTRO = "C";
		public static final String ATUALIZACAO = "A";
		public static final String INATIVACAO = "I";
		public static final String ATIVACAO = "T";

		DomEventoAuditoriaSetorProduto() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(ATUALIZACAO, "Atualização");
			mapa.put(INATIVACAO, "Inativação");
			mapa.put(ATIVACAO, "Ativação");
		}
	}

	public static class DomEventoAuditoriaFormaPagamento extends DomBase {
		public static final String CADASTRO = "C";
		public static final String ATUALIZACAO = "A";
		public static final String INATIVACAO = "I";
		public static final String ATIVACAO = "T";

		DomEventoAuditoriaFormaPagamento() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(ATUALIZACAO, "Atualização");
			mapa.put(INATIVACAO, "Inativação");
			mapa.put(ATIVACAO, "Ativação");
		}
	}

	public static class DomEventoAuditoriaPedido extends DomBase {
		public static final String CADASTRO = "C";
		public static final String PAGAMENTO = "P";
		public static final String ENVIO = "E";
		public static final String FINALIZACAO = "F";
		public static final String CANCELAMENTO = "L";

		DomEventoAuditoriaPedido() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(PAGAMENTO, "Pagamento");
			mapa.put(ENVIO, "Envio");
			mapa.put(FINALIZACAO, "Finalização");
			mapa.put(CANCELAMENTO, "Cancelamento");
		}
	}

	public static class DomEventoAuditoriaCotacao extends DomBase {
		public static final String CADASTRO = "C";
		public static final String FINALIZACAO = "F";
		public static final String CANCELAMENTO = "L";

		DomEventoAuditoriaCotacao() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(FINALIZACAO, "Finalização");
			mapa.put(CANCELAMENTO, "Cancelamento");
		}
	}

	public static class DomEventoAuditoriaTroca extends DomBase {
		public static final String CADASTRO = "C";
		public static final String FINALIZACAO = "F";
		public static final String CANCELAMENTO = "L";

		DomEventoAuditoriaTroca() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(FINALIZACAO, "Finalização");
			mapa.put(CANCELAMENTO, "Cancelamento");
		}
	}

	public static class DomEventoAuditoriaSolicitacaoEstoque extends DomBase {
		public static final String CADASTRO = "C";
		public static final String FINALIZACAO = "F";

		DomEventoAuditoriaSolicitacaoEstoque() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(FINALIZACAO, "Finalização");
		}
	}

	public static class DomEventoAuditoriaJanelaCompra extends DomBase {
		public static final String CADASTRO = "C";
		public static final String FECHAMENTO = "F";
		public static final String REABERTURA = "R";
		public static final String CANCELAMENTO = "L";

		DomEventoAuditoriaJanelaCompra() {
			mapa.put(CADASTRO, "Cadastro");
			mapa.put(FECHAMENTO, "Fechamento");
			mapa.put(REABERTURA, "Reabertura");
			mapa.put(CANCELAMENTO, "Cancelamento");
		}
	}

	// Processamento
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