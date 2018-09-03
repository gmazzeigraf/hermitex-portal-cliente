package br.com.graflogic.hermitex.cliente.rest.wrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

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

	private static final String STATUS_BOLETO_PENDENTE = "P";

	private static final String STATUS_BOLETO_PAGO = "G";

	private static final String STATUS_PEDIDO_PAGAMENTO_PENDENTE = "P";

	private static final String STATUS_PEDIDO_PAGO = "G";

	private static final String EVENTO_PEDIDO_PAGAMENTO = "P";

	@Transactional(rollbackOn = Throwable.class)
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
					Map<String, Object> boleto = getJdbcTemplate().queryForMap(
							"SELECT id_pedido, status FROM tb_pedido_boleto WHERE id_transacao_pagamento = ?", new Object[] { idTransacao });

					// Verifica o status do boleto
					if (STATUS_BOLETO_PENDENTE.equals(boleto.get("status"))) {
						getJdbcTemplate().update(
								"UPDATE tb_pedido_boleto SET status = ?, dt_notificacao_pagamento = CURRENT_TIMESTAMP, dt_pagamento = ? WHERE id_transacao_pagamento = ?",
								new Object[] { STATUS_BOLETO_PAGO, dataPagamento, idTransacao });

						Long idPedido = (Long) boleto.get("id_pedido");

						// Verifica se ainda existem boletos pendentes
						Long boletosEmAberto = (Long) getJdbcTemplate()
								.queryForMap("SELECT COALESCE(COUNT(id), 0) AS quantidade FROM tb_pedido_boleto WHERE status = ? AND id_pedido = ?",
										new Object[] { STATUS_BOLETO_PENDENTE, idPedido })
								.get("quantidade");

						if (0 == boletosEmAberto) {
							// Consulta o status do pedido
							Map<String, Object> pedido = getJdbcTemplate().queryForMap("SELECT status FROM tb_pedido WHERE id = ?",
									new Object[] { idPedido });

							// Verifica se o pedido esta em pagamento pendente
							if (STATUS_PEDIDO_PAGAMENTO_PENDENTE.equals(pedido.get("status"))) {
								// Altera o pedido para pago
								getJdbcTemplate().update("UPDATE tb_pedido SET status = ?, versao = (versao + 1) WHERE id = ?",
										new Object[] { STATUS_PEDIDO_PAGO, idPedido });

								getJdbcTemplate().update(
										"INSERT INTO tb_pedido_aud (id, id_relacionado, data, cd_evento) VALUES (?, ?, CURRENT_TIMESTAMP, ?)",
										new Object[] { UUID.randomUUID().toString(), idPedido, EVENTO_PEDIDO_PAGAMENTO });
							} else {
								LOGGER.warn("Pedido nao esta mais pendente de pagamento");
							}
						}

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