package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.service.interfaces.ISalePointService;
import org.ace.insurance.system.common.township.Township;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageSalePointActionBean")
public class ManageSalePointActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{SalePointService}")
	private ISalePointService salePointService;

	public void setSalePointService(ISalePointService salePointService) {
		this.salePointService = salePointService;
	}

	private SalePoint salePoint;
	private String oldsalePointName;
	private List<SalePoint> salePointList;
	private boolean createNew;

	@PostConstruct
	public void init() {
		createNew = true;
		salePoint = new SalePoint();
		loadSalePoint();
	}

	public void createNewSalePoint() {
		createNew = true;
		salePoint = new SalePoint();
		PrimeFaces.current().resetInputs(":salePointEntryForm");
	}

	public void loadSalePoint() {
		salePointList = salePointService.findAllSalePoint();
	}

	public void prepareUpdateSalePoint(SalePoint salePoint) {
		createNew = false;
		oldsalePointName = salePoint.getName();
		this.salePoint = salePoint;
	}

	public void addNewSalePoint() {
		try {
			if (!isAlreadyExist()) {
				salePointService.addNewSalePoint(salePoint);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, salePoint.getName());
				salePointList.add(salePoint);
				createNewSalePoint();
			} else {
				addWranningMessage(null, MessageId.ALREADY_ADD_SALEPOINT, "This Sale Point");
			}
			loadSalePoint();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateSalePoint() {
		try {
			if (!isAlreadyExist()) {
				salePointService.updateSalePoint(salePoint);
				addInfoMessage(null, MessageId.UPDATE_SUCCESS, salePoint.getName());
				createNewSalePoint();
			} else {
				addWranningMessage(null, MessageId.ALREADY_ADD_SALEPOINT, "This Sale Point");
			}
			loadSalePoint();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public boolean isAlreadyExist() {
		return salePointService.isAlreadyExist(salePoint);
	}

	public String deleteSalePoint(SalePoint salePoint) {
		try {
			salePointService.deleteSalePoint(salePoint);
			salePointList.remove(salePoint);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, salePoint.getName());
			createNewSalePoint();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		salePoint.setTownship(township);
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public List<SalePoint> getSalePointList() {
		return salePointList;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public String getOldsalePointName() {
		return oldsalePointName;
	}

	

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		salePoint.setBranch(branch);
	}

	public void removeBranch()
	{
		salePoint.setBranch(null);
	}
}
