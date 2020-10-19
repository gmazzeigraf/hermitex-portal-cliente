package br.com.graflogic.hermitex.cliente.service.impl.frete;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.graflogic.correios.client.CorreiosClient;
import br.com.graflogic.correios.model.CResultado;
import br.com.graflogic.correios.model.CServico;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomServicoFrete;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomTipo;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.Cotacao;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoItem;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Embalagem;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.hermitex.cliente.data.util.ConfiguracaoEnum;
import br.com.graflogic.hermitex.cliente.service.exception.CorreiosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.ConfiguracaoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.EmbalagemService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.ProdutoService;
import br.com.graflogic.hermitex.cliente.service.model.frete.Frete;
import br.com.graflogic.hermitex.cliente.service.model.pedido.TipoFrete;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class FreteService {

	private static final double FATOR_REGIME_TRIBUTARIO_FRETE = 0.75;

	@Autowired
	private ConfiguracaoService configuracaoService;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private EmbalagemService embalagemService;

	public List<TipoFrete> geraTiposCorreios(Pedido entity) {
		try {
			if (null == entity.getEnderecoEntrega() || StringUtils.isBlank(entity.getEnderecoEntrega().getCep())) {
				return new ArrayList<>();
			}

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

			return geraTiposCorreios(tiposProduto, pesos, quantidades, entity.getEnderecoEntrega().getCep());

		} catch (Exception e) {
			throw new CorreiosException(e);
		}
	}

	public List<TipoFrete> geraTiposCorreios(Cotacao entity) {
		try {
			if (null == entity.getEnderecoEntrega() || StringUtils.isBlank(entity.getEnderecoEntrega().getCep())) {
				return new ArrayList<>();
			}

			// Separa os itens por tipo
			List<String> tiposProduto = new ArrayList<>();
			Map<String, BigDecimal> pesos = new HashMap<>();
			Map<String, Integer> quantidades = new HashMap<>();

			// Separa os pesos e quantidades por tipo de produto
			for (CotacaoItem item : entity.getItens()) {
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

			return geraTiposCorreios(tiposProduto, pesos, quantidades, entity.getEnderecoEntrega().getCep());

		} catch (Exception e) {
			throw new CorreiosException(e);
		}
	}

	private List<TipoFrete> geraTiposCorreios(List<String> tiposProduto, Map<String, BigDecimal> pesos, Map<String, Integer> quantidades,
			String cepEntrega) {
		CorreiosClient client = new CorreiosClient(configuracaoService.consulta(ConfiguracaoEnum.CORREIOS_URL),
				configuracaoService.consulta(ConfiguracaoEnum.CORREIOS_CODIGO_EMPRESA),
				configuracaoService.consulta(ConfiguracaoEnum.CORREIOS_SENHA));

		List<String> servicosFrete = Arrays.asList(DomServicoFrete.PAC, DomServicoFrete.SEDEX);

		List<TipoFrete> tiposFrete = new ArrayList<>();

		// Processa os tipos de frete
		for (String servicoFrete : servicosFrete) {
			TipoFrete tipoFrete = new TipoFrete();
			tipoFrete.setCodigoServico(servicoFrete);

			List<Frete> fretesTipoFrete = new ArrayList<>();
			BigDecimal valorTipoFrete = BigDecimal.ZERO;

			// Processa os tipos de produto
			tipoProdutoFor: {
				for (String tipoProduto : tiposProduto) {
					List<Frete> fretes = new ArrayList<>();

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

							// Caso o peso total seja menor que um, define um que e o menor aceito pelos
							// correios
							if (pesoFrete.compareTo(BigDecimal.ONE) < 0) {
								pesoFrete = BigDecimal.ONE;
							}

							// Consulta o frete
							CResultado resultado = client.calculaPrecoPrazo(Arrays.asList(servicoFrete),
									configuracaoService.consulta(ConfiguracaoEnum.FRETE_CEP_ORIGEM), cepEntrega, pesoFrete.intValue(),
									embalagem.getComprimento(), embalagem.getAltura(), embalagem.getLargura());

							for (CServico servico : resultado.getServicos().getCServico()) {
								BigDecimal valor = new BigDecimal(servico.getValor().replace(",", "."));

								// Caso o valor seja retornado, gera o frete
								if (valor.compareTo(BigDecimal.ZERO) > 0) {
									Frete Frete = new Frete();
									Frete.setIdEmbalagem(embalagem.getId());
									Frete.setPesoItens(pesoFrete);
									Frete.setQuantidadeItens(quantidadeFrete);
									Frete.setNomeEmbalagem(embalagem.getNome());
									Frete.setValor(valor);
									Frete.setCodigoServico(servicoFrete);
									Frete.setPrazoDias(Integer.parseInt(servico.getPrazoEntrega()));

									fretes.add(Frete);

									valorTipoFrete = valorTipoFrete.add(valor);

								} else {
									// Caso nao seja retornado valor, remove o tipo de frete
									fretesTipoFrete.clear();
									break tipoProdutoFor;
								}
							}

						}
					}

					fretesTipoFrete.addAll(fretes);
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
	}

	public TipoFrete geraTipoRetirada() {
		TipoFrete tipo = new TipoFrete();
		tipo.setFretes(new ArrayList<>());
		tipo.setCodigoServico(DomServicoFrete.RETIRADA_HERMITEX);
		tipo.setValor(BigDecimal.ZERO);

		Frete frete = new Frete();
		frete.setCodigoServico(DomServicoFrete.RETIRADA_HERMITEX);
		frete.setValor(BigDecimal.ZERO);
		frete.setPesoItens(BigDecimal.ZERO);
		frete.setQuantidadeItens(0);
		frete.setPrazoDias(0);

		tipo.getFretes().add(frete);

		return tipo;
	}

	public TipoFrete geraTipoTransportadora() {
		TipoFrete tipo = new TipoFrete();
		tipo.setFretes(new ArrayList<>());
		tipo.setCodigoServico(DomServicoFrete.TRANSPORTADORA);
		tipo.setValor(BigDecimal.ZERO);

		Frete frete = new Frete();
		frete.setCodigoServico(DomServicoFrete.TRANSPORTADORA);
		frete.setValor(BigDecimal.ZERO);
		frete.setPesoItens(BigDecimal.ZERO);
		frete.setQuantidadeItens(0);
		frete.setPrazoDias(0);

		tipo.getFretes().add(frete);

		return tipo;
	}
}