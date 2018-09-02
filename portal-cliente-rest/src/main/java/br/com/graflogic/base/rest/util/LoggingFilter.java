package br.com.graflogic.base.rest.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

/**
 * 
 * @author ggraf
 *
 */
public class LoggingFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger("AUDIT");

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		if (servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse && LOGGER.isDebugEnabled()) {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpServletResponse response = (HttpServletResponse) servletResponse;

			String path = request.getRequestURI().substring(request.getContextPath().length());

			if (path.contains("swagger-ui") || path.contains("v2/api-docs") || path.contains("swagger-resources")) {
				chain.doFilter(servletRequest, servletResponse);
				return;
			}

			HttpServletRequest requestToCache = new ContentCachingRequestWrapper(request);
			HttpServletResponse responseToCache = new ContentCachingResponseWrapper(response);
			chain.doFilter(requestToCache, responseToCache);
			String requestData = getRequestData(requestToCache);
			String responseData = getResponseData(responseToCache);
			String parameters = request.getQueryString();

			LOGGER.debug(path + " -> " + (StringUtils.isNotEmpty(parameters) ? parameters + "|" : "") + requestData);
			LOGGER.debug(path + " <- " + responseData);

		} else {
			chain.doFilter(servletRequest, servletResponse);
		}
	}

	private static String getRequestData(final HttpServletRequest request) throws UnsupportedEncodingException {
		ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);

		String payload = "";
		if (wrapper != null) {
			byte[] buf = wrapper.getContentAsByteArray();
			if (buf.length > 0) {
				payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
			}

			payload = payload.replace("\n", "").replace("\r", "");
		}

		return payload;
	}

	private static String getResponseData(final HttpServletResponse response) throws IOException {
		ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);

		String payload = "";
		if (wrapper != null) {
			byte[] buf = wrapper.getContentAsByteArray();
			if (buf.length > 0) {
				payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
				wrapper.copyBodyToResponse();
			}

			payload = payload.replace("\n", "").replace("\r", "");
		}

		return payload;
	}

	@Override
	public void destroy() {
	}
}