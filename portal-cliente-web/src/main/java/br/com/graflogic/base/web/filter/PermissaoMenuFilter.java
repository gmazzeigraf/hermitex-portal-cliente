package br.com.graflogic.base.web.filter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.submenu.UISubmenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author gmazz
 *
 */
public class PermissaoMenuFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(PermissaoMenuFilter.class);

	private String caminhoMenu;

	public PermissaoMenuFilter(String caminhoMenu) {
		this.caminhoMenu = caminhoMenu;
	}

	public void filtraMenuVazio() {
		try {
			LOGGER.debug("Comecando limpeza de menu");

			UIComponent componenteMenu = FacesContext.getCurrentInstance().getViewRoot().findComponent(caminhoMenu);

			if (null != componenteMenu) {
				for (UIComponent item : componenteMenu.getChildren()) {
					if (item instanceof UISubmenu) {
						limpaSubmenu((UISubmenu) item);
					}
				}

			} else {
				LOGGER.warn("Caminho do menu invalido: " + caminhoMenu);
			}

			LOGGER.debug("Finalizando limpeza de menu");

		} catch (Throwable t) {
			LOGGER.error("Erro ao limpar menu", t);
		}
	}

	private int limpaSubmenu(UISubmenu submenu) {
		LOGGER.debug("Processando submenu " + submenu.getLabel());

		int itensSubmenuVisiveis = 0;

		for (UIComponent item : submenu.getChildren()) {
			if (item instanceof UISubmenu) {
				int itensVisiveis = limpaSubmenu((UISubmenu) item);

				if (itensVisiveis > 0) {
					itensSubmenuVisiveis += itensVisiveis;

				}

			}

			if (item.isRendered()) {
				itensSubmenuVisiveis++;

			}
		}

		if (itensSubmenuVisiveis == 0) {
			submenu.setRendered(false);

			LOGGER.debug("Submenu removido " + submenu.getLabel());
		}

		return itensSubmenuVisiveis;
	}
}