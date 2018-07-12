package br.com.graflogic.hermitex.cliente.service.impl.model;

import java.io.Serializable;

/**
 * 
 * @author gmazz
 *
 */
public class LinhaTabelaMedidas implements Serializable {

	private static final long serialVersionUID = 4572535118170608775L;

	private String conteudo;

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
}