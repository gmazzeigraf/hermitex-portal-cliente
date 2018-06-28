package br.com.graflogic.hermitex.cliente.web.util;

import java.awt.Graphics2D;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import br.com.graflogic.hermitex.cliente.service.impl.produto.ProdutoService;

/**
 * 
 * @author gmazz
 *
 */
public class ImagemServlet extends HttpServlet {

	private static final long serialVersionUID = -8994235178100494201L;

	@Autowired
	private ProdutoService produtoService;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idImagem = request.getParameter("idImagem");

		boolean miniatura = new Boolean(request.getParameter("miniatura"));

		byte[] imagemArray = produtoService.downloadImagem(idImagem);

		if (imagemArray == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		if (miniatura) {
			BufferedImage original = ImageIO.read(new ByteArrayInputStream(imagemArray));

			Double width = (double) original.getWidth();
			Double height = (double) original.getHeight();

			Double divisor = width / 260;
			width = width / divisor;
			height = height / divisor;

			BufferedImage alterada = new BufferedImage(width.intValue(), height.intValue(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = alterada.createGraphics();
			g.drawImage(original, 0, 0, width.intValue(), height.intValue(), null);
			g.dispose();

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(alterada, "jpg", stream);
			stream.flush();

			imagemArray = stream.toByteArray();

			stream.close();
		}

		response.reset();
		response.setContentType("image/jpg");
		response.setContentLength(imagemArray.length);

		response.getOutputStream().write(imagemArray);
	}
}