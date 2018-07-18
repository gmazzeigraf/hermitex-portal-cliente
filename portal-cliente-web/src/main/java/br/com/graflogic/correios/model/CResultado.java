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
 * <p>Classe Java de cResultado complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="cResultado"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Servicos" type="{http://tempuri.org/}ArrayOfCServico" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cResultado", propOrder = { "servicos" })
public class CResultado {

	@XmlElement(name = "Servicos")
	protected ArrayOfCServico servicos;

	/**
	 * Obtém o valor da propriedade servicos.
	 * 
	 * @return
	 *     possible object is
	 *     {@link ArrayOfCServico }
	 *     
	 */
	public ArrayOfCServico getServicos() {
		return servicos;
	}

	/**
	 * Define o valor da propriedade servicos.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link ArrayOfCServico }
	 *     
	 */
	public void setServicos(ArrayOfCServico value) {
		this.servicos = value;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}