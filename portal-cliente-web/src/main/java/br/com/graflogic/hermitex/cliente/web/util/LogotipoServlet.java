package br.com.graflogic.hermitex.cliente.web.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;

/**
 * 
 * @author gmazz
 *
 */
public class LogotipoServlet extends HttpServlet {

	private static final long serialVersionUID = -8188481707499255869L;

	@Autowired
	private ClienteService clienteService;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idCliente = request.getParameter("idCliente");

		if (StringUtils.isEmpty(idCliente) || !StringUtils.isNumeric(idCliente)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		byte[] logotipoArray = clienteService.downloadLogotipo(Integer.parseInt(idCliente));

		if (logotipoArray == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		BufferedImage original = ImageIO.read(new ByteArrayInputStream(logotipoArray));

		// Grava o logotipo
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ImageIO.write(original, "png", stream);
		stream.flush();

		logotipoArray = stream.toByteArray();

		stream.close();

		response.reset();
		response.setContentType("image/png");
		response.setContentLength(logotipoArray.length);

		response.getOutputStream().write(logotipoArray);
	}
}