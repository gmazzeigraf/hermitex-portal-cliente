package br.com.graflogic.hermitex.cliente.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.graflogic.hermitex.cliente.rest.model.NotificacaoPagamentoRequest;
import br.com.graflogic.hermitex.cliente.rest.wrapper.MundiPaggWrapper;

/**
 * 
 * @author ggraf
 *
 */
@Controller
@RequestMapping(value = "/mundipagg")
public class MundiPaggController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MundiPaggController.class);

	@Autowired
	private MundiPaggWrapper wrapper;

	@RequestMapping(value = "/pagamento", method = RequestMethod.POST, consumes = "application/json", produces = "text/html")
	public @ResponseBody String notificaPagamento(@RequestBody NotificacaoPagamentoRequest requisicao, HttpServletRequest request) {
		try {
			wrapper.notificaPagamento(requisicao);

			return "OK";

		} catch (Throwable t) {
			LOGGER.error("Erro ao processar notificacao de pagamento", t);

			return "NOK";
		}
	}
}