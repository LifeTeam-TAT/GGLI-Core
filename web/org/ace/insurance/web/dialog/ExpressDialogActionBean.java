package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.ace.insurance.common.ExpressCriteria;
import org.ace.insurance.common.ExpressCriteriaItems;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.express.Express001;
import org.ace.insurance.system.common.express.service.interfaces.IExpressService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "ExpressDialogActionBean")
@ViewScoped
public class ExpressDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value = "#{ExpressService}")
	private IExpressService expressService;

	public void setExpressService(IExpressService expressService) {
		this.expressService = expressService;
	}

	private List<Express001> expressList;
	private String selectedCriteria;
	private List<SelectItem> expressCriteriaItemList;
	private ExpressCriteria expressCriteria;

	@PostConstruct
	public void init() {
		expressCriteria = new ExpressCriteria();
		expressCriteriaItemList = new ArrayList<SelectItem>();
		for (ExpressCriteriaItems criteriaItem : ExpressCriteriaItems.values()) {
			expressCriteriaItemList.add(new SelectItem(criteriaItem.getLabel(), criteriaItem.getLabel()));
		}
		expressList = expressService.findExpressByCriteria001(expressCriteria, 50);
	}

	public List<Express001> getExpressList() {
		return expressList;
	}

	public void selectExpress(Express001 express001) {
		Express express = expressService.findExpressById(express001.getId());
		PrimeFaces.current().dialog().closeDynamic(express);
	}

	public void search() {
		expressCriteria.setExpressCriteria(null);
		for (ExpressCriteriaItems criteriaItem : ExpressCriteriaItems.values()) {
			if (criteriaItem.toString().equals(selectedCriteria)) {
				expressCriteria.setExpressCriteria(criteriaItem);
			}
		}
		expressList = expressService.findExpressByCriteria001(expressCriteria, 50);
	}

	public String getSelectedCriteria() {
		return selectedCriteria;
	}

	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}

	public List<SelectItem> getExpressCriteriaItemList() {
		return expressCriteriaItemList;
	}

	public void setExpressCriteriaItemList(List<SelectItem> expressCriteriaItemList) {
		this.expressCriteriaItemList = expressCriteriaItemList;
	}

	public ExpressCriteria getExpressCriteria() {
		return expressCriteria;
	}

	public void setExpressCriteria(ExpressCriteria expressCriteria) {
		this.expressCriteria = expressCriteria;
	}

	public void setExpressList(List<Express001> expressList) {
		this.expressList = expressList;
	}

	public void reset() {
		expressCriteria.setExpressCriteria(null);
		expressList = expressService.findExpressByCriteria001(expressCriteria, 50);
		setSelectedCriteria(null);
		expressCriteria.setCriteriaValue(null);
	}
}
