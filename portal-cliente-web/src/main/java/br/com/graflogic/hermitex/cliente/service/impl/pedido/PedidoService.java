package br.com.graflogic.hermitex.cliente.service.impl.pedido;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.commonutil.util.PessoaFisicaValidator;
import br.com.graflogic.commonutil.util.PessoaJuridicaValidator;
import br.com.graflogic.correios.client.CorreiosClient;
import br.com.graflogic.correios.model.CResultado;
import br.com.graflogic.correios.model.CServico;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomServicoFrete;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatus;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomTipo;
import br.com.graflogic.hermitex.cliente.data.entity.aud.PedidoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.JanelaCompra;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoFrete;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoSimple;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Embalagem;
import br.com.graflogic.hermitex.cliente.data.entity.produto.FormaPagamento;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.TamanhoProduto;
import br.com.graflogic.hermitex.cliente.data.impl.aud.PedidoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cadastro.PedidoEnderecoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoFreteRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoItemRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoRepository;
import br.com.graflogic.hermitex.cliente.data.util.ConfiguracaoEnum;
import br.com.graflogic.hermitex.cliente.service.exception.CorreiosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.PagamentoException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.ConfiguracaoService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.EmbalagemService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.FormaPagamentoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.ProdutoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.TamanhoProdutoService;
import br.com.graflogic.hermitex.cliente.service.model.DadosPagamentoCartaoCredito;
import br.com.graflogic.hermitex.cliente.service.model.TipoFrete;
import br.com.graflogic.hermitex.cliente.service.util.ExcelUtil;
import br.com.graflogic.mundipagg.client.MundiPaggClient;
import br.com.graflogic.mundipagg.model.BoletoTransaction;
import br.com.graflogic.mundipagg.model.Buyer;
import br.com.graflogic.mundipagg.model.CreditCard;
import br.com.graflogic.mundipagg.model.CreditCardTransaction;
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

	private static final double FATOR_REGIME_TRIBUTARIO_FRETE = 0.85;

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
	private JanelaCompraService janelaCompraService;

	@Autowired
	private TamanhoProdutoService tamanhoProdutoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ConfiguracaoService configuracaoService;

	@Autowired
	private EmbalagemService embalagemService;

	@Autowired
	private FormaPagamentoService formaPagamentoService;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Pedido entity, DadosPagamentoCartaoCredito dadosPagamentoCartaoCredito, Integer idUsuario) {
		// TODO Caso seja filial, verifica se esta com compra bloqueada e emite alerta
		
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

		List<PedidoFrete> fretes = entity.getFretes();
		entity.setFretes(null);

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

			if (entity.isPagamentoCartaoCredito() || entity.isPagamentoBoleto()) {
				// Envia o pagamento
				enviaPagamento(entity, dadosPagamentoCartaoCredito);
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
		try {
			// Consulta o cliente
			Cliente cliente = clienteService.consultaPorId(entity.getIdCliente());

			// Ordem
			Order order = new Order();
			order.setOrderReference(entity.getId().toString());

			// Requisicao
			SaleRequest request = new SaleRequest();

			// Pagamento
			if (entity.isPagamentoCartaoCredito()) {
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

				request.setCreditCardTransactionCollection(new ArrayList<>());
				request.getCreditCardTransactionCollection().add(transaction);

			} else if (entity.isPagamentoBoleto()) {
				// Dados do boleto
				BoletoTransaction transaction = new BoletoTransaction();
				transaction.setAmountInCents(Long.parseLong(entity.getValorTotal().toString().replace(".", "")));
				transaction.setBankNumber(configuracaoService.consulta(ConfiguracaoEnum.PAGAMENTO_BOLETO_CODIGO_BANCO));
				transaction.setInstructions(configuracaoService.consulta(ConfiguracaoEnum.PAGAMENTO_BOLETO_INSTRUCAO));
				transaction.setTransactionReference(entity.getId().toString());

				// Configuracao
				Options options = new Options();
				options.setDaysToAddInBoletoExpirationDate(cliente.getDiasBoleto());
				transaction.setOptions(options);

				request.setBoletoTransactionCollection(new ArrayList<>());
				request.getBoletoTransactionCollection().add(transaction);

			}

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

			entity.setIdPagamento(response.getOrderResult().getOrderKey());

			if (entity.isPagamentoCartaoCredito()) {
				paga(entity, null, null);

			} else if (entity.isPagamentoBoleto()) {
				// TODO Adiciona o objeto boleto
				//				entity.setUrlBoleto(response.getBoletoTransactionResultCollection().get(0).getBoletoUrl());
			}

		} catch (Throwable t) {
			throw new PagamentoException(t);
		}
	}

	// Frete
	public List<TipoFrete> geraTiposFreteCorreios(Pedido entity) {
		try {
			CorreiosClient client = new CorreiosClient(configuracaoService.consulta(ConfiguracaoEnum.CORREIOS_URL),
					configuracaoService.consulta(ConfiguracaoEnum.CORREIOS_CODIGO_EMPRESA),
					configuracaoService.consulta(ConfiguracaoEnum.CORREIOS_SENHA));

			// Separa os itens por tipo
			List<String> tiposProduto = new ArrayList<>();
			Map<String, BigDecimal> pesos = new HashMap<>();
			Map<String, Integer> quantidades = new HashMap<>();

			// Separa os pesos e quantidades por tipo de produto
			for (PedidoItem item : entity.getItens()) {
				Produto produto = produtoService.consultaPorId(item.getIdProduto());

				String tipoProduto = produto.getTipo();

				// Caso seja bolsa, calcula junto de roupa
				if (DomTipo.BOLSA.equals(tipoProduto)) {
					tipoProduto = DomTipo.ROUPA;
				}

				if (!tiposProduto.contains(tipoProduto)) {
					tiposProduto.add(tipoProduto);
					pesos.put(tipoProduto, BigDecimal.ZERO);
					quantidades.put(tipoProduto, 0);
				}

				pesos.put(tipoProduto, pesos.get(tipoProduto).add(item.getPesoTotal()));
				quantidades.put(tipoProduto, quantidades.get(tipoProduto) + item.getQuantidade());
			}

			List<String> servicosFrete = Arrays.asList(DomServicoFrete.PAC, DomServicoFrete.SEDEX);

			List<TipoFrete> tiposFrete = new ArrayList<>();

			// Processa os tipos de frete
			for (String servicoFrete : servicosFrete) {
				TipoFrete tipoFrete = new TipoFrete();
				tipoFrete.setCodigoServico(servicoFrete);

				List<PedidoFrete> fretesTipoFrete = new ArrayList<>();
				BigDecimal valorTipoFrete = BigDecimal.ZERO;

				// Processa os tipos de produto
				tipoProdutoFor: {
					for (String tipoProduto : tiposProduto) {
						List<PedidoFrete> fretesTipoProduto = new ArrayList<>();

						// Gera as embalagens
						BigDecimal pesoRestante = BigDecimal.ZERO;
						Integer quantidadeRestante = 0;

						if (DomTipo.ROUPA.equals(tipoProduto)) {
							pesoRestante = pesos.get(tipoProduto);

						} else if (DomTipo.SAPATO.equals(tipoProduto)) {
							quantidadeRestante = quantidades.get(tipoProduto);

						} else {
							throw new DadosInvalidosException("Tipo de produto " + tipoProduto + " sem frete implementado");
						}

						while (pesoRestante.compareTo(BigDecimal.ZERO) > 0 || quantidadeRestante > 0) {
							BigDecimal pesoFrete = BigDecimal.ZERO;
							Integer quantidadeFrete = 0;

							Embalagem embalagem = null;
							try {
								if (DomTipo.ROUPA.equals(tipoProduto)) {
									// Consulta por peso
									embalagem = embalagemService.consultaPorTipoProdutoPeso(tipoProduto, pesoRestante);

									pesoFrete = pesoRestante;

									pesoRestante = BigDecimal.ZERO;

								} else if (DomTipo.SAPATO.equals(tipoProduto)) {
									// Consulta por quantidade
									embalagem = embalagemService.consultaPorTipoProdutoQuantidade(tipoProduto, quantidadeRestante);

									quantidadeFrete = quantidadeRestante;

									quantidadeRestante = 0;

								}

							} catch (ResultadoNaoEncontradoException e) {
								try {
									// Consulta a maior
									if (DomTipo.ROUPA.equals(tipoProduto)) {
										embalagem = embalagemService.consultaMaiorPesoPorTipoProduto(tipoProduto);

										pesoFrete = embalagem.getPesoMaximo();

										pesoRestante = pesoRestante.subtract(embalagem.getPesoMaximo());

									} else if (DomTipo.SAPATO.equals(tipoProduto)) {
										embalagem = embalagemService.consultaMaiorQuantidadePorTipoProduto(tipoProduto);

										quantidadeFrete = embalagem.getQuantidadeMaxima();

										quantidadeRestante = quantidadeRestante - embalagem.getQuantidadeMaxima();

									}

								} catch (ResultadoNaoEncontradoException x) {
									throw new DadosInvalidosException("Nenhuma embalagem cadastrada para o tipo de produto " + tipoProduto);

								}
							}

							// Caso tenha embalagem, gera o frete
							if (null != embalagem) {

								// O peso de cada sapato foi definido com 1kg
								if (DomTipo.SAPATO.equals(tipoProduto)) {
									pesoFrete = BigDecimal.ONE.multiply(new BigDecimal(quantidadeFrete));
								}

								// Adiciona o peso da embalagem
								pesoFrete = pesoFrete.add(embalagem.getPeso());

								// Caso o peso total seja menor que um, define um que e o menor aceito pelos correios
								if (pesoFrete.compareTo(BigDecimal.ONE) < 0) {
									pesoFrete = BigDecimal.ONE;
								}

								// Consulta o frete
								CResultado resultado = client.calculaPrecoPrazo(Arrays.asList(servicoFrete),
										configuracaoService.consulta(ConfiguracaoEnum.FRETE_CEP_ORIGEM), entity.getEnderecoEntrega().getCep(),
										pesoFrete.intValue(), embalagem.getComprimento(), embalagem.getAltura(), embalagem.getLargura());

								for (CServico servico : resultado.getServicos().getCServico()) {
									BigDecimal valor = new BigDecimal(servico.getValor().replace(",", "."));

									// Caso o valor seja retornado, gera o frete
									if (valor.compareTo(BigDecimal.ZERO) > 0) {
										PedidoFrete frete = new PedidoFrete();
										frete.setIdEmbalagem(embalagem.getId());
										frete.setPesoItens(pesoFrete);
										frete.setQuantidadeItens(quantidadeFrete);
										frete.setNomeEmbalagem(embalagem.getNome());
										frete.setValor(valor);
										frete.setCodigoServico(servicoFrete);
										frete.setPrazoDias(Integer.parseInt(servico.getPrazoEntrega()));

										fretesTipoProduto.add(frete);

										valorTipoFrete = valorTipoFrete.add(valor);

									} else {
										// Caso nao seja retornado valor, remove o tipo de frete
										fretesTipoFrete.clear();
										break tipoProdutoFor;
									}
								}

							}
						}

						fretesTipoFrete.addAll(fretesTipoProduto);
					}
				}

				if (fretesTipoFrete.isEmpty()) {
					continue;
				}

				tipoFrete.setFretes(fretesTipoFrete);
				tipoFrete.setValor(valorTipoFrete.divide(new BigDecimal(FATOR_REGIME_TRIBUTARIO_FRETE), 2, RoundingMode.HALF_EVEN));

				tiposFrete.add(tipoFrete);

			}

			return tiposFrete;

		} catch (Throwable t) {
			throw new CorreiosException(t);
		}
	}

	public TipoFrete geraTipoFreteRetirada() {
		TipoFrete tipo = new TipoFrete();
		tipo.setFretes(new ArrayList<>());
		tipo.setCodigoServico(DomServicoFrete.RETIRADA_HERMITEX);
		tipo.setValor(BigDecimal.ZERO);

		PedidoFrete frete = new PedidoFrete();
		frete.setCodigoServico(DomServicoFrete.RETIRADA_HERMITEX);
		frete.setValor(BigDecimal.ZERO);
		frete.setPesoItens(BigDecimal.ZERO);
		frete.setQuantidadeItens(0);
		frete.setPrazoDias(0);

		tipo.getFretes().add(frete);

		return tipo;
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
	}

	public List<FormaPagamento> geraFormasPagamento(Pedido pedido) {
		List<FormaPagamento> formasPagamento = new ArrayList<>();

		// TODO Gera as formas de pagamento de acordo com cliente, franquia e pedido
		//		for (int i = 1; i <= cliente.getMaximoParcelasCartao(); i++) {
		//			formasPagamento.add(new FormaPagamento(formasPagamento.size(), DomTipoFormaPagamento.CARTAO_CREDITO, i));
		//		}
		//
		//		if (cliente.isFaturamento()) {
		//			formasPagamento.add(new FormaPagamento(formasPagamento.size(), DomTipoFormaPagamento.FATURAMENTO, 1));
		//		}
		//
		//		for (FormaPagamento forma : formasPagamento) {
		//			forma.setValor(valorTotal.divide(new BigDecimal(forma.getParcelas()), 2, RoundingMode.HALF_EVEN));
		//
		//			String descricao = DomPedido.domTipoFormaPagamento.getDeValor(forma.getCodigo());
		//
		//			if (DomTipoFormaPagamento.BOLETO.equals(forma.getCodigo())) {
		//				descricao += " " + cliente.getDiasBoleto() + " dias";
		//
		//			} else if (DomTipoFormaPagamento.CARTAO_CREDITO.equals(forma.getCodigo())) {
		//				descricao += " " + forma.getParcelas() + "x R$ " + forma.getValor().toString().replace(".", ",");
		//
		//			} else if (DomTipoFormaPagamento.FATURAMENTO.equals(forma.getCodigo())) {
		//				descricao = cliente.getDescricaoFaturamento();
		//			}
		//
		//			forma.setDescricao(descricao);
		//		}

		return formasPagamento;
	}
}