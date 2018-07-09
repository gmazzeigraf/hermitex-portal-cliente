package br.com.graflogic.hermitex.cliente.service.impl.pedido;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mundipagg.api.models.CreateAddressRequest;
import com.mundipagg.api.models.CreateCardRequest;
import com.mundipagg.api.models.CreateCreditCardPaymentRequest;
import com.mundipagg.api.models.CreateCustomerRequest;
import com.mundipagg.api.models.CreateOrderRequest;
import com.mundipagg.api.models.CreatePaymentRequest;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.service.model.DadosPagamentoCartaoCredito;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class PagamentoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PagamentoService.class);

	public void enviaPagamentoCartaoCredio(Cliente cliente, Filial filial, Pedido pedido, DadosPagamentoCartaoCredito dadosPagamentoCartaoCredito) {
		// Ordem
		CreateOrderRequest orderRequest = new CreateOrderRequest();
		orderRequest.setPayments(new ArrayList<>());
		orderRequest.setItems(new ArrayList<>());
		orderRequest.setCode(pedido.getId().toString());

		// Transacao
		CreatePaymentRequest request = new CreatePaymentRequest();
		request.setPaymentMethod("credit_card");
		request.setAmount(Integer.parseInt(pedido.getValorTotal().toString().replace(".", "")));
		// TODO Obter configuracao
		request.setGatewayAffiliationId("f0cd3ebd-ef95-4511-9b44-b51b9549167e");

		// Pagamento
		CreateCreditCardPaymentRequest cardPaymentRequest = new CreateCreditCardPaymentRequest();
		cardPaymentRequest.setInstallments(dadosPagamentoCartaoCredito.getParcelas());
		cardPaymentRequest.setStatementDescriptor("HERMITEX " + pedido.getId());
		cardPaymentRequest.setCapture(true);
		cardPaymentRequest.setRecurrence(false);

		// Cartao
		CreateCardRequest cardRequest = new CreateCardRequest();
		cardRequest.setNumber(dadosPagamentoCartaoCredito.getNumero());
		cardRequest.setHolderName(dadosPagamentoCartaoCredito.getNomeImpresso());
		cardRequest.setHolderDocument(dadosPagamentoCartaoCredito.getDocumentoPortador());
		cardRequest.setExpMonth(Integer.parseInt(dadosPagamentoCartaoCredito.getVencimento().substring(0, 2)));
		cardRequest.setExpYear(Integer.parseInt(dadosPagamentoCartaoCredito.getVencimento().substring(2)));
		cardRequest.setCvv(dadosPagamentoCartaoCredito.getCodigoSeguranca());
		cardRequest.setBrand(dadosPagamentoCartaoCredito.getBandeira());
		cardRequest.setPrivateLabel(false);

		CreateAddressRequest addressRequest = new CreateAddressRequest();
		addressRequest.setStreet(pedido.getEnderecoFaturamento().getLogradouro());
		addressRequest.setNumber(pedido.getEnderecoFaturamento().getNumero());
		addressRequest.setZipCode(pedido.getEnderecoFaturamento().getCep());
		addressRequest.setNeighborhood(pedido.getEnderecoFaturamento().getBairro());
		addressRequest.setCity(pedido.getEnderecoFaturamento().getNomeMunicipio());
		addressRequest.setState(pedido.getEnderecoFaturamento().getSiglaEstado());
		addressRequest.setCountry("Brazil");
		addressRequest.setComplement(pedido.getEnderecoFaturamento().getComplemento());

		cardRequest.setBillingAddress(addressRequest);

		cardPaymentRequest.setCard(cardRequest);

		request.setCreditCard(cardPaymentRequest);

		// Cliente
		CreateCustomerRequest customerRequest = new CreateCustomerRequest();
		if (null != filial) {
			customerRequest.setName(filial.getNomeFantasia());
			customerRequest.setEmail(filial.getEmail());
		} else {
			customerRequest.setName(cliente.getNomeFantasia());
			customerRequest.setEmail(cliente.getEmail());
		}

		request.setCustomer(customerRequest);

		orderRequest.setCustomer(customerRequest);
		orderRequest.getPayments().add(request);

		LOGGER.info(GsonUtil.gson.toJson(request));
	}
}