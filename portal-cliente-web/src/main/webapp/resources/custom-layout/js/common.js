function jump(idAtual, tamanhoMaximo, idProximo) {
	var valorAtual = replaceAll(document.getElementById(idAtual).value, '_', '');
	valorAtual = replaceAll(valorAtual, '.', '');
	valorAtual = replaceAll(valorAtual, '-', '');

	var teclaPressionada = event.keyCode;

	if (valorAtual.length == tamanhoMaximo) {
		if ((teclaPressionada >= 48 && teclaPressionada <= 90)
				|| (teclaPressionada >= 96 && teclaPressionada <= 105)) {
			jumpTo(idProximo);
		}
	}
}

function jumpTo(idProximo) {
	document.getElementById(idProximo).focus();
}

function replaceAll(string, token, newtoken) {
	while (string.indexOf(token) != -1) {
		string = string.replace(token, newtoken);
	}
	return string;
}

function printElement(element, title) {
	var mywindow = window.open('', title, 'height=600,width=800');
	mywindow.document.write($(element).html());

	mywindow.document.close(); // necessary for IE >= 10
	mywindow.focus(); // necessary for IE >= 10

	mywindow.print();
	mywindow.close();

	return true;
}

function removeAcento(elemento) {
	var valor = elemento.value;

	str_acento = "áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ";
	str_sem_acento = "aaaaaeeeeiiiiooooouuuucAAAAAEEEEIIIIOOOOOUUUUC";
	var nova = "";
	for (var i = 0; i < valor.length; i++) {
		if (str_acento.indexOf(valor.charAt(i)) != -1) {
			nova += str_sem_acento.substr(
					str_acento.search(valor.substr(i, 1)), 1);
		} else {
			nova += valor.substr(i, 1);
		}
	}

	elemento.value = nova;
}

function removeCaractere(elemento, caractere) {
	elemento.value = replaceAll(elemento.value, caractere, '');
}

function formataChassi(elemento) {
	var valor = elemento.value;

	valor = valor.replace(/[^a-zA-Z0-9]/g, "");

	valor = valor.toUpperCase();

	valor = replaceAll(valor, 'I', '');
	valor = replaceAll(valor, 'Q', '');
	valor = replaceAll(valor, 'O', '');

	elemento.value = valor;
}

function toLowerCase(elemento) {
	elemento.value = elemento.value.toLowerCase();
}

function toUpperCase(elemento) {
	elemento.value = elemento.value.toUpperCase();
}

function formataCampo(elemento) {
	var valor = elemento.value;

	str_acento = "áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ";
	str_sem_acento = "aaaaaeeeeiiiiooooouuuucAAAAAEEEEIIIIOOOOOUUUUC";
	var nova = "";
	for (var i = 0; i < valor.length; i++) {
		if (str_acento.indexOf(valor.charAt(i)) != -1) {
			nova += str_sem_acento.substr(
					str_acento.search(valor.substr(i, 1)), 1);
		} else {
			nova += valor.substr(i, 1);
		}
	}

	elemento.value = nova;

	elemento.value = elemento.value.toUpperCase();
}

function scrollPageTop() {
	$("html, body").animate({
		scrollTop : 0
	});
	return false;
}

if (window['PrimeFaces'] && window['PrimeFaces'].widget.Dialog) {
	PrimeFaces.widget.Dialog = PrimeFaces.widget.Dialog.extend({

		enableModality : function() {
			this._super();
			$(document.body).children(this.jqId + '_modal').addClass(
					'ui-dialog-mask');
			$('html, body').css('overflow', 'hidden');
		},
		disableModality : function() {
			this._super();
			$('html, body').css('overflow', 'auto');
		},

		syncWindowResize : function() {
		}
	});
}