package br.com.graflogic.correios.client;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import br.com.graflogic.correios.model.CResultado;
import br.com.graflogic.correios.model.CalcPrecoPrazoData;
import br.com.graflogic.correios.model.CalcPrecoPrazoDataResponse;

/**
 * 
 * @author ggraf
 *
 */
public class CorreiosClient extends WebServiceGatewaySupport {

	private static final int FORMATO_CAIXA_PACOTE = 1;

	private static final String FLAG_NEGATIVO = "n";

	private static final Logger LOGGER = LoggerFactory.getLogger("CORREIOS");

	private String codigoEmpresa;

	private String senha;

	public CorreiosClient(String url, String codigoEmpresa, String senha) {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("br.com.graflogic.correios.model");

		HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
		messageSender.setConnectionTimeout(15000);
		messageSender.setReadTimeout(60000);

		setMessageSender(messageSender);
		setDefaultUri(url);
		setMarshaller(marshaller);
		setUnmarshaller(marshaller);

		this.codigoEmpresa = codigoEmpresa;
		this.senha = senha;
	}

	public CResultado calculaPrecoPrazo(List<String> codigosServico, String cepOrigem, String cepDestino, Integer peso, BigDecimal comprimento,
			BigDecimal altura, BigDecimal largura) {
		CalcPrecoPrazoData requisicao = new CalcPrecoPrazoData();
		requisicao.setNCdEmpresa(codigoEmpresa);
		requisicao.setSDsSenha(senha);
		requisicao.setNCdServico(StringUtils.join(codigosServico, ","));
		requisicao.setSCepOrigem(cepOrigem);
		requisicao.setSCepDestino(cepDestino);
		requisicao.setNVlPeso(peso.toString());
		requisicao.setNCdFormato(FORMATO_CAIXA_PACOTE);
		requisicao.setNVlComprimento(comprimento);
		requisicao.setNVlAltura(altura);
		requisicao.setNVlLargura(largura);
		requisicao.setNVlDiametro(BigDecimal.ZERO);
		requisicao.setSCdMaoPropria(FLAG_NEGATIVO);
		requisicao.setNVlValorDeclarado(BigDecimal.ZERO);
		requisicao.setSCdAvisoRecebimento(FLAG_NEGATIVO);
		requisicao.setSDtCalculo("");

		LOGGER.debug("-> " + requisicao.toString());

		CalcPrecoPrazoDataResponse resposta = (CalcPrecoPrazoDataResponse) getWebServiceTemplate().marshalSendAndReceive(requisicao,
				new SoapActionCallback("http://tempuri.org/CalcPrecoPrazoData"));

		LOGGER.debug("<- " + resposta.toString());

		CResultado retorno = resposta.getCalcPrecoPrazoDataResult();

		return retorno;
	}
}