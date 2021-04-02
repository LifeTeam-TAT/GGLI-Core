/***************************************************************************************
 * @author <<Myo Thiha Kyaw>>
 * @Date 2016-06-18
 * @Version 1.0
 * @Purpose <<For Travel Insurance>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

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
import org.ace.insurance.system.common.express.service.interfaces.IExpressService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.web.common.LazyDataModelUtil;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

@ViewScoped
@ManagedBean(name = "ManageExpressActionBean")
public class ManageExpressActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ExpressService}")
	private IExpressService expressService;

	public void setExpressService(IExpressService expressService) {
		this.expressService = expressService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	private boolean createNew;
	private Express express;
	private LazyDataModelUtil<Express> lazyModel;
	private List<Express> expressList;

	private ExpressCriteria criteria;
	private String selectedCriteria;
	private List<SelectItem> criteriaItemList;

	@PostConstruct
	public void init() {
		createNew = true;
		express = new Express();
		loadExpresss();

		// for search
		criteria = new ExpressCriteria();
		criteriaItemList = new ArrayList<SelectItem>();
		for (ExpressCriteriaItems criteriaItem : ExpressCriteriaItems.values()) {
			criteriaItemList.add(new SelectItem(criteriaItem.getLabel(), criteriaItem.getLabel()));
		}
	}

	public void loadExpresss() {
		expressList = expressService.findAllExpress();
		lazyModel = new LazyDataModelUtil<Express>(expressList);
	}

	public void createNewExpress() {
		createNew = true;
		express = new Express();
		PrimeFaces.current().resetInputs(":expressEntryForm");
	}

	public void prepareUpdateExpress(Express express) {
		createNew = false;
		this.express = express;
	}

	public void search() {
		criteria.setExpressCriteria(null);
		for (ExpressCriteriaItems criteriaItem : ExpressCriteriaItems.values()) {
			if (criteriaItem.toString().equals(selectedCriteria)) {
				criteria.setExpressCriteria(criteriaItem);
			}
		}
		expressList = expressService.findExpressByCriteria(criteria, 50);
		lazyModel = new LazyDataModelUtil<Express>(expressList);
	}

	public void reset() {
		loadExpresss();
		criteria.setExpressCriteria(null);
		setSelectedCriteria(null);
		criteria.setCriteriaValue(null);
	}

	public void addNewExpress() {
		try {
			Township township = express.getAddress().getTownship();
			if (township == null) {
				addInfoMessage(null, MessageId.REQUIRED_TOWNSHIP);
			} else {
				expressService.addNewExpress(express);
				loadExpresss();
				addInfoMessage(null, MessageId.INSERT_SUCCESS, express.getName());
				createNewExpress();
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateExpress() {
		try {
			expressService.updateExpress(express);
			loadExpresss();
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, express.getName());
			createNewExpress();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public String deleteExpress(Express express) {
		try {
			expressService.deleteExpress(express);
			loadExpresss();
			addInfoMessage(null, MessageId.DELETE_SUCCESS, express.getName());
			createNewExpress();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<Express> getExpressList() {
		return expressList;
	}

	public LazyDataModel<Express> getLazyModel() {
		return lazyModel;
	}

	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}

	public void setExpressList(List<Express> expressList) {
		this.expressList = expressList;
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		express.getAddress().setTownship(township);
	}

	public ExpressCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ExpressCriteria criteria) {
		this.criteria = criteria;
	}

	public String getSelectedCriteria() {
		return selectedCriteria;
	}

	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}

	public List<SelectItem> getCriteriaItemList() {
		return criteriaItemList;
	}

	public void setCriteriaItemList(List<SelectItem> criteriaItemList) {
		this.criteriaItemList = criteriaItemList;
	}
}
