package org.ace.insurance.common;

import javax.faces.context.FacesContext;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.ace.java.web.common.Constants;

@Converter(autoApply = true)
public class UpperCaseConverter implements AttributeConverter<String, Object> {

	@Override
	public String convertToDatabaseColumn(String attribute) {
		if (attribute != null && !attribute.isEmpty()) {
			if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.LOGIN_USER) != null) {
				attribute = attribute.toUpperCase();
			}
		}
		return attribute;
	}

	@Override
	public String convertToEntityAttribute(Object dbData) {
		String result = dbData == null ? null : dbData.toString();
		if (dbData != null) {
			if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.LOGIN_USER) != null) {
				result = dbData.toString().toUpperCase();
			}
		}
		return result;
	}

}
