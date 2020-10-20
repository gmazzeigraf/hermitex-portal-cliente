package br.com.graflogic.hermitex.cliente.data.entity.acesso;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomTipoUsuario;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_perfil_usuario_administrador")
@PrimaryKeyJoinColumn(name = "id")
public class PerfilUsuarioAdministrador extends PerfilUsuario {

	private static final long serialVersionUID = 5470893052520028326L;

	public PerfilUsuarioAdministrador() {
		setTipoUsuario(DomTipoUsuario.ADMINISTRADOR);
	}

	@Column(name = "pc_desconto_livre_maximo", nullable = false)
	private BigDecimal porcentagemDescontoLivreMaximo;

	@Column(name = "pc_desconto_gerencial_maximo", nullable = false)
	private BigDecimal porcentagemDescontoGerencialMaximo;

	public BigDecimal getPorcentagemDescontoLivreMaximo() {
		return porcentagemDescontoLivreMaximo;
	}

	public void setPorcentagemDescontoLivreMaximo(BigDecimal porcentagemDescontoLivreMaximo) {
		this.porcentagemDescontoLivreMaximo = porcentagemDescontoLivreMaximo;
	}

	public BigDecimal getPorcentagemDescontoGerencialMaximo() {
		return porcentagemDescontoGerencialMaximo;
	}

	public void setPorcentagemDescontoGerencialMaximo(BigDecimal porcentagemDescontoGerencialMaximo) {
		this.porcentagemDescontoGerencialMaximo = porcentagemDescontoGerencialMaximo;
	}
}