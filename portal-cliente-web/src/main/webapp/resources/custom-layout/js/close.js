var dataClick = null;

 $(document).ready(function() {
	 carregaClose();
	 
 });
 
function carregaClose() {
	$("button").click(function() {
		dataClick = new Date();
	});
	
	$("a")
			.click(
					function(clickEvent) {
						var link = $(this).attr("href");
						

						if ("#" != link
								&& (!clickEvent.metaKey && !clickEvent.ctrlKey && !clickEvent.which === 2)) {
							showStatus();
						}

						dataClick = new Date();
					});

	$("form").submit(function() {
		dataClick = new Date();
	});

	dataClick = new Date();
}


// Tratamento do fechamento de janela
$(window).on('beforeunload', function(event) {
	var ie = isIE();

	if (!ie) {
		var diferencaClick = 0;

		if (null != dataClick) {
			diferencaClick = new Date() - dataClick;
		}

		if (null == dataClick || diferencaClick > 100) {
			var confirmationMessage = "Confirmar";

			event.returnValue = confirmationMessage; // Gecko + IE
			return confirmationMessage; // Webkit, Safari, Chrome etc.
		}

		dataClick = null;
	}
});

function isIE() {
	var ua = window.navigator.userAgent;
	var msie = ua.indexOf("MSIE ");

	return msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./);
}

function liberaRefresh() {
	dataClick = new Date();
}