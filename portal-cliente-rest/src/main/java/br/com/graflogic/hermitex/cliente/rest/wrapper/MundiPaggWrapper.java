package br.com.graflogic.hermitex.cliente.rest.wrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import br.com.graflogic.hermitex.cliente.rest.model.NotificacaoPagamentoRequest;

/**
 * 
 * @author gmazz
 *
 */
@Component
public class MundiPaggWrapper extends BaseWrapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(MundiPaggWrapper.class);

	private static final String STATUS_TRANSACAO_PAGO = "PAID";

	private static final String STATUS_BOLETO_PAGO = "G";

	public void notificaPagamento(NotificacaoPagamentoRequest requisicao) {
		if (null != requisicao.getBoletoTransaction()) {
			// Verifica se e uma transacao de boleto
			if (STATUS_TRANSACAO_PAGO.equals(requisicao.getOrderStatus().toUpperCase())) {
				try {
					String idTransacao = requisicao.getBoletoTransaction().getTransactionKey();

					// Faz o parse da data de pagamento
					Date dataPagamento = null;
					try {
						dataPagamento = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
								.parse(requisicao.getBoletoTransaction().getStatusChangedDate());
					} catch (ParseException e) {
						LOGGER.warn("Data de pagamento invalida");
						return;
					}

					// Consulta o boleto pelo id da transacao
					Map<String, Object> boleto = getJdbcTemplate()
							.queryForMap("SELECT id, status FROM tb_pedido_boleto WHERE id_transacao_pagamento = ?", new Object[] { idTransacao });

					// Verifica o status do boleto
					if ("P".equals(boleto.get("status"))) {
						getJdbcTemplate().update(
								"UPDATE tb_pedido_boleto SET status = ?, dt_notificacao_pagamento = CURRENT_TIMESTAMP, dt_pagamento = ? WHERE id_transacao_pagamento = ?",
								new Object[] { STATUS_BOLETO_PAGO, dataPagamento, idTransacao });

						// TODO - Verifica se ainda existem boletos em aberto

						// TODO - Paga o pedido caso nao tenha mais boletos em aberto

					} else {
						LOGGER.warn("Boleto nao esta mais pendente");
					}

				} catch (EmptyResultDataAccessException e) {
					LOGGER.warn("Boleto nao encontrado");
				}
			}

		} else {
			LOGGER.warn("Notificacao de pagamento nao implementada");
		}
	}
}