package br.com.graflogic.hermitex.cliente.service.impl.pedido;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.OptimisticLockException;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.commonutil.util.PessoaFisicaValidator;
import br.com.graflogic.commonutil.util.PessoaJuridicaValidator;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatus;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatusBoleto;
import br.com.graflogic.hermitex.cliente.data.entity.aud.PedidoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.JanelaCompra;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoBoleto;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoFrete;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoSimple;
import br.com.graflogic.hermitex.cliente.data.entity.produto.FormaPagamento;
import br.com.graflogic.hermitex.cliente.data.entity.produto.TamanhoProduto;
import br.com.graflogic.hermitex.cliente.data.impl.aud.PedidoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoBoletoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoEnderecoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoFreteRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoItemRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoRepository;
import br.com.graflogic.hermitex.cliente.data.util.ConfiguracaoEnum;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.PagamentoException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.ConfiguracaoService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.TamanhoProdutoService;
import br.com.graflogic.hermitex.cliente.service.model.pedido.DadosPagamentoCartaoCredito;
import br.com.graflogic.hermitex.cliente.service.util.ExcelUtil;
import br.com.graflogic.mundipagg.client.MundiPaggClient;
import br.com.graflogic.mundipagg.model.BoletoTransaction;
import br.com.graflogic.mundipagg.model.Buyer;
import br.com.graflogic.mundipagg.model.CreditCard;
import br.com.graflogic.mundipagg.model.CreditCardTransaction;
import br.com.graflogic.mundipagg.model.CreditCardTransactionResult;
import br.com.graflogic.mundipagg.model.Options;
import br.com.graflogic.mundipagg.model.Order;
import br.com.graflogic.mundipagg.model.SaleRequest;
import br.com.graflogic.mundipagg.model.SaleResponse;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class PedidoService {

	private static final String OPERACAO_CARTAO_CREDITO = "AuthAndCapture";

	@Autowired
	private PedidoRepository repository;

	@Autowired
	private PedidoAuditoriaRepository auditoriaRepository;

	@Autowired
	private PedidoItemRepository itemRepository;

	@Autowired
	private PedidoEnderecoRepository enderecoRepository;

	@Autowired
	private PedidoFreteRepository freteRepository;

	@Autowired
	private PedidoBoletoRepository boletoRepository;

	@Autowired
	private JanelaCompraService janelaCompraService;

	@Autowired
	private TamanhoProdutoService tamanhoProdutoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private ConfiguracaoService configuracaoService;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Pedido entity, FormaPagamento formaPagamento, DadosPagamentoCartaoCredito dadosPagamentoCartaoCredito, Integer idUsuario) {
		// Verifica se a compra esta bloqueada
		if (null != entity.getIdFilial() && filialService.isCompraBloqueada(entity.getIdFilial())) {
			throw new DadosInvalidosException("Não foi possível prosseguir com a compra, contate o administrador");
		}

		// Verifica o status do cliente
		Cliente cliente = clienteService.consultaPorId(entity.getIdCliente());

		if (!cliente.isAtivo()) {
			throw new DadosInvalidosException("Cliente inativo, contate o administrador");
		}

		if (null != entity.getIdFilial() && 0 != entity.getIdFilial()) {
			// Caso seja filial, valida o status
			Filial filial = filialService.consultaPorId(entity.getIdFilial());

			if (!filial.isAtiva()) {
				throw new DadosInvalidosException("Filial inativa, contate o administrador");
			}
		}

		// Caso seja compra com carta, valida o documento do portador
		if (formaPagamento.isCartaoCredito()) {
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

		List<PedidoFrete> fretes = entity.getFretes();
		entity.setFretes(null);

		entity.setBoletos(null);

		try {
			repository.store(entity);

			for (PedidoItem item : itens) {
				item.setIdPedido(entity.getId());
			}

			for (PedidoFrete frete : fretes) {
				frete.setIdPedido(entity.getId());
			}

			entity.setItens(itens);

			entity.setFretes(fretes);

			executaAtualiza(entity);

			registraAuditoria(entity.getId(), entity, DomEventoAuditoriaPedido.CADASTRO, idUsuario, null);

			// Envia o pagamento
			if (formaPagamento.isBoleto()) {
				entity.setBoletos(new ArrayList<>());

				enviaPagamentoBoleto(entity, formaPagamento);

				for (PedidoBoleto boleto : entity.getBoletos()) {
					boleto.setIdPedido(entity.getId());
				}

			} else if (formaPagamento.isCartaoCredito()) {
				enviaPagamentoCartaoCredito(entity, formaPagamento, dadosPagamentoCartaoCredito);

			}

			executaAtualiza(entity);

		} catch (Throwable t) {
			entity.setItens(itens);
			entity.setFretes(fretes);
			entity.setId(null);

			throw t;
		}
	}

	@Transactional(rollbackFor = Throwable.class)
	public void paga(Pedido entity, Integer idUsuario, String observacao) {
		if (!entity.isPagamentoPendente()) {
			throw new DadosInvalidosException("Apenas pedidos com pagamento pendente podem ser marcados como pagos");
		}

		entity.setStatus(DomStatus.PAGO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaPedido.PAGAMENTO, idUsuario, observacao);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void envia(Pedido entity, Integer idUsuario, String observacao) {
		if (!entity.isPago()) {
			throw new DadosInvalidosException("Apenas pedidos pagos podem ser marcados como enviados");
		}

		entity.setStatus(DomStatus.ENVIADO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaPedido.ENVIO, idUsuario, observacao);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void finaliza(Pedido entity, Integer idUsuario, String observacao) {
		if (!entity.isEnviado()) {
			throw new DadosInvalidosException("Apenas pedidos enviados podem ser marcados como finalizados");
		}

		entity.setStatus(DomStatus.FINALIZADO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaPedido.FINALIZACAO, idUsuario, observacao);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void cancela(Pedido entity, Integer idUsuario, String observacao) {
		if (!entity.isPagamentoPendente() && !entity.isPago()) {
			throw new DadosInvalidosException("Apenas pedidos pendentes de pagamento ou pagos podem ser cancelados");
		}

		entity.setStatus(DomStatus.CANCELADO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaPedido.CANCELAMENTO, idUsuario, observacao);
	}

	private void executaAtualiza(Pedido entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
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

	public Pedido consultaPorIdClienteFilial(Long id, Integer idCliente, Integer idFilial) {
		Pedido pedido = consultaPorId(id);

		if (!idCliente.equals(pedido.getIdCliente())) {
			throw new ResultadoNaoEncontradoException();
		}

		if (null != idFilial && 0 != idFilial && !idFilial.equals(pedido.getIdFilial())) {
			throw new ResultadoNaoEncontradoException();
		}

		return pedido;
	}

	@Transactional(readOnly = true)
	public Pedido consultaPorId(Long id) {
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

	public PedidoItem consultaItemPorId(Long idItem) {
		return itemRepository.findById(idItem);
	}

	// Extracao
	public byte[] geraExtracao(List<PedidoItem> itens) throws IOException {
		// Obtem todos os produtos e tamanhos de todos os pedidos
		HashMap<String, Map<String, Integer>> produtosMap = new HashMap<>();
		List<String> tamanhosItens = new ArrayList<>();

		HashMap<String, String> titulosProdutosMap = new HashMap<>();

		for (PedidoItem item : itens) {
			if (!produtosMap.containsKey(item.getCodigoProduto())) {
				produtosMap.put(item.getCodigoProduto(), new HashMap<String, Integer>());
				titulosProdutosMap.put(item.getCodigoProduto(), item.getTituloProduto());
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
			cell.setCellValue(codigoProduto + " - " + titulosProdutosMap.get(codigoProduto));
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
	private void enviaPagamentoBoleto(Pedido entity, FormaPagamento formaPagamento) {
		Integer quantidadeParcelas = formaPagamento.getQuantidadeParcelas();
		String[] dias = formaPagamento.getConfiguracao().split(";");

		// Requisicao
		SaleRequest request = new SaleRequest();
		request.setBoletoTransactionCollection(new ArrayList<>());

		for (int i = 0; i < quantidadeParcelas; i++) {
			Integer diasVencimento = Integer.parseInt(dias[i]);

			// Dados do boleto
			BoletoTransaction transaction = new BoletoTransaction();
			transaction.setAmountInCents(Long.parseLong(formaPagamento.getValorParcela().toString().replace(".", "")));
			transaction.setBankNumber(configuracaoService.consulta(ConfiguracaoEnum.PAGAMENTO_BOLETO_CODIGO_BANCO));
			transaction.setInstructions(configuracaoService.consulta(ConfiguracaoEnum.PAGAMENTO_BOLETO_INSTRUCAO));
			transaction.setTransactionReference(entity.getId().toString());

			// Configuracao
			Options options = new Options();
			options.setDaysToAddInBoletoExpirationDate(diasVencimento);
			transaction.setOptions(options);

			request.getBoletoTransactionCollection().add(transaction);
		}

		SaleResponse response = enviaPagamento(entity, request);

		entity.setIdOrdemPagamento(response.getOrderResult().getOrderKey());

		for (int i = 0; i < quantidadeParcelas; i++) {
			Integer diasVencimento = Integer.parseInt(dias[i]);

			Calendar calendarVencimento = Calendar.getInstance();
			calendarVencimento.add(Calendar.DAY_OF_YEAR, diasVencimento);

			PedidoBoleto boleto = new PedidoBoleto();
			boleto.setIdTransacaoPagamento(response.getBoletoTransactionResultCollection().get(i).getTransactionKey());
			boleto.setUrl(response.getBoletoTransactionResultCollection().get(i).getBoletoUrl());
			boleto.setDataVencimento(calendarVencimento.getTime());
			boleto.setStatus(DomStatusBoleto.PENDENTE);

			entity.getBoletos().add(boleto);
		}
	}

	private void enviaPagamentoCartaoCredito(Pedido entity, FormaPagamento formaPagamento, DadosPagamentoCartaoCredito dadosPagamentoCartaoCredito) {
		// Requisicao
		SaleRequest request = new SaleRequest();
		request.setCreditCardTransactionCollection(new ArrayList<>());

		// Dados do cartao
		CreditCard creditCard = new CreditCard();
		creditCard.setCreditCardBrand(dadosPagamentoCartaoCredito.getBandeira());
		creditCard.setCreditCardNumber(dadosPagamentoCartaoCredito.getNumero());
		creditCard.setExpMonth(Integer.parseInt(dadosPagamentoCartaoCredito.getVencimento().substring(0, 2)));
		creditCard.setExpYear(Integer.parseInt(dadosPagamentoCartaoCredito.getVencimento().substring(2)));
		creditCard.setHolderName(dadosPagamentoCartaoCredito.getNomeImpresso());
		creditCard.setSecurityCode(dadosPagamentoCartaoCredito.getCodigoSeguranca());

		// Dados da transacao
		CreditCardTransaction transaction = new CreditCardTransaction();
		transaction.setAmountInCents(Long.parseLong(entity.getValorTotal().toString().replace(".", "")));
		transaction.setCreditCard(creditCard);
		transaction.setInstallmentCount(dadosPagamentoCartaoCredito.getParcelas());
		transaction.setCreditCardOperation(OPERACAO_CARTAO_CREDITO);
		transaction.setTransactionReference(entity.getId().toString());

		request.getCreditCardTransactionCollection().add(transaction);

		SaleResponse response = enviaPagamento(entity, request);

		// Valida o retorno
		CreditCardTransactionResult transactionResult = response.getCreditCardTransactionResultCollection().get(0);

		if (StringUtils.isEmpty(transactionResult.getAcquirerReturnCode()) || !StringUtils.isNumeric(transactionResult.getAcquirerReturnCode())
				|| 0 != Integer.parseInt(transactionResult.getAcquirerReturnCode())) {
			throw new PagamentoException(transactionResult.getAcquirerMessage());
		}

		entity.setIdOrdemPagamento(response.getOrderResult().getOrderKey());
		entity.setIdTransacaoPagamento(transactionResult.getTransactionKey());

		paga(entity, null, null);
	}

	private SaleResponse enviaPagamento(Pedido entity, SaleRequest request) {
		try {
			// Consulta o cliente
			Cliente cliente = clienteService.consultaPorId(entity.getIdCliente());

			// Ordem
			Order order = new Order();
			order.setOrderReference(entity.getId().toString());

			// Cliente
			Buyer buyer = new Buyer();
			if (null == entity.getIdFilial()) {
				buyer.setName(cliente.getNomeFantasia());
				buyer.setDocumentNumber(cliente.getCnpj());

			} else {
				Filial filial = filialService.consultaPorId(entity.getIdFilial());

				buyer.setName(filial.getNomeFantasia());
				buyer.setDocumentNumber(filial.getCnpj());

			}

			buyer.setDocumentType("CNPJ");
			buyer.setPersonType("Company");
			buyer.setAddressCollection(new ArrayList<>());

			request.setBuyer(buyer);
			request.setOrder(order);

			// Envia
			MundiPaggClient client = new MundiPaggClient(configuracaoService.consulta(ConfiguracaoEnum.PAGAMENTO_URL),
					configuracaoService.consulta(ConfiguracaoEnum.PAGAMENTO_MERCHANT_KEY));

			SaleResponse response = client.enviaTransacao(request);

			// Verifica se ocorreu erro
			if (null != response.getErrorReport() && null != response.getErrorReport().getErrorItemCollection()
					&& !response.getErrorReport().getErrorItemCollection().isEmpty()) {
				throw new PagamentoException(response.getErrorReport().getErrorItemCollection().get(0).getDescription());
			}

			return response;

		} catch (PagamentoException e) {
			throw e;

		} catch (Throwable t) {
			throw new PagamentoException(t);
		}
	}

	// Util
	private void validaDados(Pedido entity) {
		if (entity.getItens().isEmpty()) {
			throw new DadosInvalidosException("Ao menos um item deve ser adicionado");
		}
	}

	private String registraAuditoria(Long id, Pedido objeto, String codigoEvento, Integer idUsuario, String observacao) {
		PedidoAuditoria evento = new PedidoAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(idUsuario);
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
			for (PedidoFrete frete : objeto.getFretes()) {
				frete.setPedido(null);
			}

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}

	private void preencheRelacionados(Pedido entity) {
		entity.setItens(consultaItensPorPedido(entity.getId()));
		entity.setEnderecos(enderecoRepository.consultaPorPedido(entity.getId()));
		entity.setFretes(freteRepository.consultaPorPedido(entity.getId()));
		entity.setBoletos(boletoRepository.consultaPorPedido(entity.getId()));
	}
}