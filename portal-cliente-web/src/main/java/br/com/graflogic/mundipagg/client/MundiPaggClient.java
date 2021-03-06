package br.com.graflogic.mundipagg.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.mundipagg.model.SaleRequest;
import br.com.graflogic.mundipagg.model.SaleResponse;

/**
 * 
 * @author gmazz
 *
 */
public class MundiPaggClient {

	private static final Logger LOGGER = LoggerFactory.getLogger("MUNDIPAGG");

	private String url;

	private String merchantKey;

	public MundiPaggClient(String url, String merchantKey) {
		this.url = url;
		this.merchantKey = merchantKey;
	}

	public SaleResponse enviaTransacao(SaleRequest objetoEnvio) throws IOException {
		String conteudoEnvio = GsonUtil.gson.toJson(objetoEnvio);

		LOGGER.debug("-> " + conteudoEnvio);

		// Cria o cliente HTTP
		RequestConfig config = RequestConfig.custom().setConnectTimeout(15000).setSocketTimeout(60000).build();

		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

		// Monta a requisicao
		HttpPost post = new HttpPost(url + "/Sale/");
		post.setHeaders(new BasicHeader[] {});
		post.setHeader("Content-Type", "application/json");

		post.setEntity(new StringEntity(conteudoEnvio, "UTF-8"));

		return execute(client, post, SaleResponse.class);
	}

	protected <T> T execute(HttpClient httpClient, HttpUriRequest request, Class<T> classeResposta) throws IOException {
		request.addHeader(new BasicHeader("MerchantKey", merchantKey));
		request.addHeader(new BasicHeader("Accept", "application/json"));

		HttpResponse response = httpClient.execute(request);
		StatusLine statusLine = response.getStatusLine();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		response.getEntity().writeTo(out);
		out.close();
		String conteudoRetorno = out.toString();

		LOGGER.debug("<- " + conteudoRetorno);

		try {
			return GsonUtil.gson.fromJson(conteudoRetorno, classeResposta);

		} catch (JsonSyntaxException e) {
			throw new IOException(statusLine.getReasonPhrase());
		}
	}
}