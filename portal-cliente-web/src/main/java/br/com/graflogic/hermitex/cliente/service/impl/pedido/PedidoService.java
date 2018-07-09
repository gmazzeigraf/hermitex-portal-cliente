package br.com.graflogic.hermitex.cliente.service.impl.pedido;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.OptimisticLockException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mundipagg.api.models.CreateAddressRequest;
import com.mundipagg.api.models.CreateBoletoPaymentRequest;
import com.mundipagg.api.models.CreateCardRequest;
import com.mundipagg.api.models.CreateCreditCardPaymentRequest;
import com.mundipagg.api.models.CreateCustomerRequest;
import com.mundipagg.api.models.CreateOrderItemRequest;
import com.mundipagg.api.models.CreateOrderRequest;
import com.mundipagg.api.models.CreatePaymentRequest;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.commonutil.util.PessoaFisicaValidator;
import br.com.graflogic.commonutil.util.PessoaJuridicaValidator;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomFormaPagamento;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatus;
import br.com.graflogic.hermitex.cliente.data.entity.aud.PedidoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Municipio;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.JanelaCompra;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoSimple;
import br.com.graflogic.hermitex.cliente.data.entity.produto.TamanhoProduto;
import br.com.graflogic.hermitex.cliente.data.impl.aud.PedidoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cadastro.PedidoEnderecoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoItemRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoRepository;
import br.com.graflogic.hermitex.cliente.data.util.ConfiguracaoEnum;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.PagamentoException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.ConfiguracaoService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.MunicipioService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.TamanhoProdutoService;
import br.com.graflogic.hermitex.cliente.service.model.DadosPagamentoCartaoCredito;
import br.com.graflogic.hermitex.cliente.service.model.FormaPagamento;
import br.com.graflogic.hermitex.cliente.service.util.ExcelUtil;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class PedidoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PedidoService.class);

	private static final String PAGAMENTO_TIPO_CARTAO_CREDITO = "credit_card";
	private static final String PAGAMENTO_TIPO_BOLETO = "boleto";
	private static final String PAGAMENTO_CARTAO_CREDITO_PREFIXO = "HERMITEX ";

	@Autowired
	private PedidoRepository repository;

	@Autowired
	private PedidoAuditoriaRepository auditoriaRepository;

	@Autowired
	private PedidoItemRepository itemRepository;

	@Autowired
	private PedidoEnderecoRepository enderecoRepository;

	@Autowired
	private JanelaCompraService janelaCompraService;

	@Autowired
	private TamanhoProdutoService tamanhoProdutoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private MunicipioService municipioService;

	@Autowired
	private ConfiguracaoService configuracaoService;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Pedido entity, DadosPagamentoCartaoCredito dadosPagamentoCartaoCredito) {
		// Caso seja compra com carta, valida o documento do portador
		if (entity.isPagamentoCartaoCredito()) {
			String documentoPortador = dadosPagamentoCartaoCredito.getDocumentoPortador();

			if (11 == documentoPortador.length()) {
				if (!PessoaFisicaValidator.validateCPF(documentoPortador)) {
					throw new DadosInvalidosException("CPF do portador inválido");
				}
			} else if (14 == documentoPortador.length()) {
				if (!PessoaJuridicaValidator.validateCNPJ(documentoPortador)) {
					throw new DadosInvalidosException("CNPJ do portador inválido");
				}
			} else {
				throw new DadosInvalidosException("Documento do portador inválido");
			}
		}

		// Verifica se a janela de compras esta aberta
		JanelaCompra janelaCompra = janelaCompraService.consultaAtiva(entity.getIdCliente());

		entity.setIdJanelaCompra(janelaCompra.getId());

		validaDados(entity);

		entity.setStatus(DomStatus.PAGAMENTO_PENDENTE);

		List<PedidoItem> itens = entity.getItens();
		entity.setItens(null);

		try {
			repository.store(entity);

			for (PedidoItem item : itens) {
				item.setIdPedido(entity.getId());
			}

			entity.setItens(itens);

			repository.update(entity);

			registraAuditoria(entity.getId(), entity, DomEventoAuditoriaPedido.CADASTRO, null);

			// Envia o pagamento
			enviaPagamento(entity, dadosPagamentoCartaoCredito);

		} catch (Throwable t) {
			entity.setItens(itens);

			throw t;
		}
	}

	public void atualiza(Pedido entity) {
		validaDados(entity);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaPedido.ATUALIZACAO, null);
	}

	private void executaAtualiza(Pedido entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	public List<FormaPagamento> geraFormasPagamento(Cliente cliente, BigDecimal valorTotal) {
		List<FormaPagamento> formasPagamento = new ArrayList<>();
		formasPagamento.add(new FormaPagamento(DomFormaPagamento.BOLETO, 1));
		formasPagamento.add(new FormaPagamento(DomFormaPagamento.CARTAO_CREDITO_1, 1));
		formasPagamento.add(new FormaPagamento(DomFormaPagamento.CARTAO_CREDITO_2, 2));

		for (FormaPagamento forma : formasPagamento) {
			forma.setValor(valorTotal.divide(new BigDecimal(forma.getParcelas())).setScale(2, RoundingMode.HALF_EVEN));

			String descricao = DomPedido.domFormaPagamento.getDeValor(forma.getCodigo());

			if (DomFormaPagamento.BOLETO.equals(forma.getCodigo())) {
				descricao += " " + cliente.getDiasBoleto() + " dias";

			}

			descricao += " R$ " + forma.getValor().toString().replace(".", ",");

			forma.setDescricao(descricao);
		}

		return formasPagamento;
	}

	// Consulta
	public List<PedidoSimple> consulta(PedidoSimple entity) {
		return repository.consulta(entity);
	}

	public List<PedidoSimple> consultaPorJanelaCompra(Integer idJanela) {
		PedidoSimple filter = new PedidoSimple();
		filter.setIdJanelaCompra(idJanela);

		return consulta(filter);
	}

	public Pedido consultaPorId(Long id) {
		Pedido entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	public Pedido consultaCompletoPorId(Long id) {
		Pedido entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		preencheRelacionados(entity);

		return entity;
	}

	public List<PedidoItem> consultaItensPorPedido(Long id) {
		return itemRepository.consultaPorPedido(id);
	}

	// Extracao
	public byte[] geraExtracao(List<PedidoItem> itens) throws IOException {
		// Obtem todos os produtos e tamanhos de todos os pedidos
		HashMap<String, Map<String, Integer>> produtosMap = new HashMap<>();
		List<String> tamanhosItens = new ArrayList<>();

		for (PedidoItem item : itens) {
			if (!produtosMap.containsKey(item.getCodigoProduto())) {
				produtosMap.put(item.getCodigoProduto(), new HashMap<String, Integer>());
			}

			if (!tamanhosItens.contains(item.getCodigoTamanho())) {
				tamanhosItens.add(item.getCodigoTamanho());
			}
		}

		// Consulta os tamanhos de produto
		List<TamanhoProduto> tamanhosProduto = tamanhoProdutoService.consulta(false);

		// Adiciona os utilizados
		List<String> tamanhosUtilizados = new ArrayList<>();

		for (TamanhoProduto tamanho : tamanhosProduto) {
			if (tamanhosItens.contains(tamanho.getCodigo())) {
				tamanhosUtilizados.add(tamanho.getCodigo());
			}
		}

		// Preenche as quantidades dos tamanhos e produtos
		for (PedidoItem item : itens) {
			Integer quantidade = item.getQuantidade();

			Integer quantidadeTamanho = produtosMap.get(item.getCodigoProduto()).get(item.getCodigoTamanho());

			if (null != quantidadeTamanho) {
				quantidade += quantidadeTamanho;
			}

			produtosMap.get(item.getCodigoProduto()).put(item.getCodigoTamanho(), quantidade);
		}

		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFSheet sheet = workbook.createSheet("Extração");

		int cellIndex = 0;
		int rowIndex = 0;

		Row row = sheet.createRow(rowIndex++);

		// Estilos
		XSSFCellStyle headerStyle = ExcelUtil.ajustaHeaderStyle(workbook.createCellStyle());
		XSSFCellStyle lineStyle = ExcelUtil.ajustaLineStyle(workbook.createCellStyle());

		Cell cell = row.createCell(cellIndex++);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue("Produto");
		cell.setCellStyle(headerStyle);

		for (String tamanho : tamanhosUtilizados) {
			cell = row.createCell(cellIndex++);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(tamanho);
			cell.setCellStyle(headerStyle);
		}

		Iterator<String> produtosIt = produtosMap.keySet().iterator();

		while (produtosIt.hasNext()) {
			String codigoProduto = produtosIt.next();

			cellIndex = 0;

			row = sheet.createRow(rowIndex++);

			cell = row.createCell(cellIndex++);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(codigoProduto);
			cell.setCellStyle(lineStyle);

			for (String tamanho : tamanhosUtilizados) {
				Integer quantidade = produtosMap.get(codigoProduto).get(tamanho);

				if (null == quantidade) {
					quantidade = 0;
				}

				cell = row.createCell(cellIndex++);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(quantidade);
				cell.setCellStyle(lineStyle);
			}
		}

		sheet = ExcelUtil.ajustaColumns(sheet, 0);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		out.flush();

		return out.toByteArray();
	}

	// Pagamento
	private void enviaPagamento(Pedido entity, DadosPagamentoCartaoCredito dadosPagamentoCartaoCredito) {
		// Consulta os dados necessario para pagamento
		Cliente cliente = clienteService.consultaPorId(entity.getIdCliente());
		Filial filial = null;
		if (null != entity.getIdFilial()) {
			filial = filialService.consultaPorId(entity.getIdFilial());
		}

		Municipio municipioFaturamento = municipioService.consultaPorId(entity.getEnderecoFaturamento().getIdMunicipio());

		entity.getEnderecoFaturamento().setNomeMunicipio(municipioFaturamento.getNome());

		try {
			// Ordem
			CreateOrderRequest orderRequest = new CreateOrderRequest();
			orderRequest.setPayments(new ArrayList<>());
			orderRequest.setItems(new ArrayList<>());
			orderRequest.setCode(entity.getId().toString());

			// Transacao
			CreatePaymentRequest request = new CreatePaymentRequest();
			request.setAmount(Integer.parseInt(entity.getValorTotal().setScale(2, RoundingMode.HALF_EVEN).toString().replace(".", "")));

			// Pagamento
			if (entity.isPagamentoCartaoCredito()) {
				request.setPaymentMethod(PAGAMENTO_TIPO_CARTAO_CREDITO);

				CreateCreditCardPaymentRequest creditCard = geraCreditCard(entity, dadosPagamentoCartaoCredito);
				request.setCreditCard(creditCard);

			} else if (entity.isPagamentoBoleto()) {
				request.setPaymentMethod(PAGAMENTO_TIPO_BOLETO);

				CreateBoletoPaymentRequest boleto = geraBoleto(cliente, entity);
				request.setBoleto(boleto);
			}

			// Cliente
			CreateCustomerRequest customerRequest = geraCustomer(cliente, filial);
			request.setCustomer(customerRequest);
			orderRequest.setCustomer(customerRequest);

			orderRequest.getPayments().add(request);

			// Itens
			for (PedidoItem item : entity.getItens()) {
				CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
				itemRequest.setQuantity(item.getQuantidade());
				itemRequest.setDescription(item.getTituloProduto());
				itemRequest.setAmount(Integer.parseInt(item.getValorTotal().setScale(2, RoundingMode.HALF_EVEN).toString().replace(".", "")));

				orderRequest.getItems().add(itemRequest);
			}

			// Envio
			LOGGER.info(GsonUtil.gson.toJson(orderRequest));

			//			MundiAPIClient client = new MundiAPIClient(configuracaoService.consulta(ConfiguracaoEnum.PAGAMENTO_KEY), "");
			//			GetOrderResponse orderResponse = client.getOrders().createOrder(orderRequest);
			//
			//			LOGGER.info(GsonUtil.gson.toJson(orderResponse));

		} catch (Throwable t) {
			throw new PagamentoException(t);
		}
	}

	private CreateCreditCardPaymentRequest geraCreditCard(Pedido entity, DadosPagamentoCartaoCredito dadosPagamentoCartaoCredito) throws Throwable {
		// Pagamento
		CreateCreditCardPaymentRequest creditCardRequest = new CreateCreditCardPaymentRequest();
		creditCardRequest.setInstallments(dadosPagamentoCartaoCredito.getParcelas());
		creditCardRequest.setStatementDescriptor(PAGAMENTO_CARTAO_CREDITO_PREFIXO + entity.getId());
		creditCardRequest.setCapture(true);
		creditCardRequest.setRecurrence(false);

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

		// Endereco faturamento
		CreateAddressRequest addressRequest = geraAddress(entity.getEnderecoFaturamento());

		cardRequest.setBillingAddress(addressRequest);

		creditCardRequest.setCard(cardRequest);

		return creditCardRequest;
	}

	private CreateBoletoPaymentRequest geraBoleto(Cliente cliente, Pedido entity) {
		CreateBoletoPaymentRequest boletoRequest = new CreateBoletoPaymentRequest();
		boletoRequest.setBank(configuracaoService.consulta(ConfiguracaoEnum.PAGAMENTO_BOLETO_CODIGO_BANCO));
		boletoRequest.setInstructions(configuracaoService.consulta(ConfiguracaoEnum.PAGAMENTO_BOLETO_INSTRUCAO));
		boletoRequest.setNossoNumero(entity.getId().toString());

		// Gera a data de vencimento
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, cliente.getDiasBoleto());

		boletoRequest.setDueAt(new DateTime(cal.getTime()));

		// Endereco faturamento
		CreateAddressRequest addressRequest = geraAddress(entity.getEnderecoFaturamento());

		boletoRequest.setBillingAddress(addressRequest);

		return boletoRequest;
	}

	private CreateCustomerRequest geraCustomer(Cliente cliente, Filial filial) {
		CreateCustomerRequest customerRequest = new CreateCustomerRequest();
		if (null != filial) {
			customerRequest.setName(filial.getNomeFantasia());
			customerRequest.setEmail(filial.getEmail());
		} else {
			customerRequest.setName(cliente.getNomeFantasia());
			customerRequest.setEmail(cliente.getEmail());
		}

		return customerRequest;
	}

	private CreateAddressRequest geraAddress(PedidoEndereco endereco) {
		CreateAddressRequest addressRequest = new CreateAddressRequest();
		addressRequest.setStreet(endereco.getLogradouro());
		addressRequest.setNumber(endereco.getNumero());
		addressRequest.setZipCode(endereco.getCep());
		addressRequest.setNeighborhood(endereco.getBairro());
		addressRequest.setCity(endereco.getNomeMunicipio());
		addressRequest.setState(endereco.getSiglaEstado());
		addressRequest.setCountry("Brazil");
		addressRequest.setComplement(endereco.getComplemento());

		return addressRequest;
	}

	// Util
	private void validaDados(Pedido entity) {
		if (entity.getItens().isEmpty()) {
			throw new DadosInvalidosException("Ao menos um item deve ser adicionado");
		}
	}

	private String registraAuditoria(Long id, Pedido objeto, String codigoEvento, String observacao) {
		PedidoAuditoria evento = new PedidoAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (Pedido) ObjectCopier.copy(objeto);
			// Remove as referencias recursivas
			for (PedidoItem item : objeto.getItens()) {
				item.setPedido(null);
			}
			for (PedidoEndereco endereco : objeto.getEnderecos()) {
				endereco.setPedido(null);
			}

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}

	private void preencheRelacionados(Pedido entity) {
		entity.setItens(consultaItensPorPedido(entity.getId()));
		entity.setEnderecos(enderecoRepository.consultaPorPedido(entity.getId()));
	}
}