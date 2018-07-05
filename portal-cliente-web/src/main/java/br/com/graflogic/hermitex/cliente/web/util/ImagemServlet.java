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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import br.com.graflogic.base.service.util.CacheUtil;
import br.com.graflogic.hermitex.cliente.service.impl.produto.ProdutoService;

/**
 * 
 * @author gmazz
 *
 */
public class ImagemServlet extends HttpServlet {

	private static final String CACHE_NAME = "imagensApresentacao";

	private static final long serialVersionUID = -8994235178100494201L;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private CacheUtil cacheUtil;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idImagem = request.getParameter("idImagem");

		String miniaturaStr = request.getParameter("miniatura");

		byte[] imagemArray = produtoService.downloadImagem(idImagem);

		if (imagemArray == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		if (StringUtils.isNotEmpty(miniaturaStr) && StringUtils.isNumeric(miniaturaStr)) {
			String chaveCache = idImagem + "_" + miniaturaStr;

			byte[] imagemCacheArray = (byte[]) cacheUtil.findOnCache(CACHE_NAME, chaveCache);

			if (null == imagemCacheArray) {
				BufferedImage original = ImageIO.read(new ByteArrayInputStream(imagemArray));

				// Calcula as proporcoes
				Double width = (double) original.getWidth();
				Double height = (double) original.getHeight();

				Double divisor = width / (50 * Integer.parseInt(miniaturaStr));
				width = width / divisor;
				height = height / divisor;

				// Redimensiona a imagem
				BufferedImage alterada = new BufferedImage(width.intValue(), height.intValue(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g = alterada.createGraphics();
				g.drawImage(original, 0, 0, width.intValue(), height.intValue(), null);
				g.dispose();

				// Corta a imagem
				//			alterada = original.getSubimage(0, 0, 150, 200);

				// Grava a imagem alterada
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				ImageIO.write(alterada, "jpg", stream);
				stream.flush();

				imagemArray = stream.toByteArray();

				stream.close();

				cacheUtil.putOnCache(CACHE_NAME, chaveCache, imagemArray);

			} else {
				imagemArray = imagemCacheArray;
			}
		}

		response.reset();
		response.setContentType("image/jpg");
		response.setContentLength(imagemArray.length);

		response.getOutputStream().write(imagemArray);
	}
}