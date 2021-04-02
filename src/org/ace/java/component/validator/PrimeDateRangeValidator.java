package org.ace.java.component.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.ace.insurance.common.utils.DateUtils;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.component.calendar.Calendar;

@FacesValidator("primeDateRangeValidator")
public class PrimeDateRangeValidator extends BaseBean implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}
		Object minDateValue = component.getAttributes().get("minDate");
		Object maxDateValue = component.getAttributes().get("maxDate");
		if (minDateValue == null && maxDateValue == null) {
			return;
		}
		Date minDate = (Date) minDateValue;
		Date maxDate = (Date) maxDateValue;
		Date inputDate = (Date) value;

		if (minDate != null && inputDate.before(minDate)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
					getMessage(MessageId.DATE_MIN_RANGE, ((Calendar) component).getLabel(), DateUtils.getDateFormatString(minDate))));
		} else if (maxDate != null && inputDate.after(maxDate)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
					getMessage(MessageId.DATE_MAX_RANGE, ((Calendar) component).getLabel(), DateUtils.getDateFormatString(minDate))));
		} else {
			return;
		}
	}
}