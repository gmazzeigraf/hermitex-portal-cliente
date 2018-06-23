package br.com.graflogic.hermitex.cliente.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

import br.com.graflogic.hermitex.cliente.data.entity.acesso.PermissaoAcesso;

/**
 * 
 * @author gmazz
 *
 */
public class PermissaoAcessoConverter implements Converter {

	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		DualListModel<PermissaoAcesso> dualList = (DualListModel<PermissaoAcesso>) ((PickList) component).getValue();

		PermissaoAcesso permissao = new PermissaoAcesso();
		permissao.setCodigo(value);

		if (dualList.getSource().contains(permissao)) {
			return dualList.getSource().get(dualList.getSource().indexOf(permissao));
		}

		if (dualList.getTarget().contains(permissao)) {
			return dualList.getTarget().get(dualList.getTarget().indexOf(permissao));
		}

		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		return ((PermissaoAcesso) value).getCodigo();
	}
}