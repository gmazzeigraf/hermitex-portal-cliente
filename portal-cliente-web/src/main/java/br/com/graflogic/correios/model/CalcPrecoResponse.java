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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * <p>Classe Java de anonymous complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CalcPrecoResult" type="{http://tempuri.org/}cResultado"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "calcPrecoResult" })
@XmlRootElement(name = "CalcPrecoResponse")
public class CalcPrecoResponse {

	@XmlElement(name = "CalcPrecoResult", required = true)
	protected CResultado calcPrecoResult;

	/**
	 * Obtém o valor da propriedade calcPrecoResult.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CResultado }
	 *     
	 */
	public CResultado getCalcPrecoResult() {
		return calcPrecoResult;
	}

	/**
	 * Define o valor da propriedade calcPrecoResult.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link CResultado }
	 *     
	 */
	public void setCalcPrecoResult(CResultado value) {
		this.calcPrecoResult = value;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}