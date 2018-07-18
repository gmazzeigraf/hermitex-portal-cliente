//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2018.07.17 às 08:59:40 PM BRT 
//

package br.com.graflogic.correios.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * <p>Classe Java de cServicosCalculo complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="cServicosCalculo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="descricao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="calcula_preco" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="calcula_prazo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="erro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="msgErro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cServicosCalculo", propOrder = { "codigo", "descricao", "calculaPreco", "calculaPrazo", "erro", "msgErro" })
public class CServicosCalculo {

	protected String codigo;
	protected String descricao;
	@XmlElement(name = "calcula_preco")
	protected String calculaPreco;
	@XmlElement(name = "calcula_prazo")
	protected String calculaPrazo;
	protected String erro;
	protected String msgErro;

	/**
	 * Obtém o valor da propriedade codigo.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * Define o valor da propriedade codigo.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setCodigo(String value) {
		this.codigo = value;
	}

	/**
	 * Obtém o valor da propriedade descricao.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * Define o valor da propriedade descricao.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setDescricao(String value) {
		this.descricao = value;
	}

	/**
	 * Obtém o valor da propriedade calculaPreco.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getCalculaPreco() {
		return calculaPreco;
	}

	/**
	 * Define o valor da propriedade calculaPreco.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setCalculaPreco(String value) {
		this.calculaPreco = value;
	}

	/**
	 * Obtém o valor da propriedade calculaPrazo.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getCalculaPrazo() {
		return calculaPrazo;
	}

	/**
	 * Define o valor da propriedade calculaPrazo.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setCalculaPrazo(String value) {
		this.calculaPrazo = value;
	}

	/**
	 * Obtém o valor da propriedade erro.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getErro() {
		return erro;
	}

	/**
	 * Define o valor da propriedade erro.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setErro(String value) {
		this.erro = value;
	}

	/**
	 * Obtém o valor da propriedade msgErro.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getMsgErro() {
		return msgErro;
	}

	/**
	 * Define o valor da propriedade msgErro.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setMsgErro(String value) {
		this.msgErro = value;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}