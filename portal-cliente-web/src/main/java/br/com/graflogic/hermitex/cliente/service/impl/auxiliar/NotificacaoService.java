package br.com.graflogic.hermitex.cliente.service.impl.auxiliar;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.graflogic.hermitex.cliente.data.dom.DomNotificacao.DomStatus;
import br.com.graflogic.hermitex.cliente.data.dom.DomNotificacao.DomTipo;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Notificacao;
import br.com.graflogic.hermitex.cliente.data.impl.auxiliar.NotificacaoRepository;

/**
 * 
 * @author gmazz
 *
 */
@Component
public class NotificacaoService {

	@Autowired
	private NotificacaoRepository repository;

	public void enviaEmail(String titulo, String conteudo, List<String> destinatarios, List<String> anexos) {
		envia(DomTipo.EMAIL, titulo, conteudo, destinatarios, anexos);
	}

	public void envia(String tipo, String titulo, String conteudo, List<String> destinatarios, List<String> anexos) {
		String destinatariosStr = "";
		String anexosStr = "";

		for (String destinatario : destinatarios) {
			destinatariosStr += destinatario + ";";
		}

		if (null != anexos) {
			for (String anexo : anexos) {
				anexosStr += anexo + ";";
			}
		}

		Notificacao entity = new Notificacao();
		entity.setId(UUID.randomUUID().toString());
		entity.setTipo(tipo);
		entity.setTitulo(titulo);
		entity.setConteudo(conteudo);
		entity.setDestinatarios(destinatariosStr);
		entity.setAnexos(anexosStr);
		entity.setDataSolicitacao(new Date());
		entity.setStatus(DomStatus.PENDENTE);

		repository.store(entity);
	}

	public List<Notificacao> consulta(Notificacao filter) {
		return repository.consulta(filter);
	}
}