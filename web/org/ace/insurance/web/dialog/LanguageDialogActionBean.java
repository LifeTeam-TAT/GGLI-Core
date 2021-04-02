package org.ace.insurance.web.dialog;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.productinformation.Language;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "LanguageDialogActionBean")
@ViewScoped
public class LanguageDialogActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public void selectLanguage(Language language) {
		PrimeFaces.current().dialog().closeDynamic(language);
	}
}
