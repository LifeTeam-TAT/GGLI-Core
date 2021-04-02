package org.ace.insurance.web.manage.report.studentLife;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.report.common.StudentLifeCriteria;
import org.ace.insurance.report.life.service.interfaces.ILifePolicyMonthlyReportService;
import org.ace.insurance.report.studentLife.BCStudentLifeView;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "BcStudentLifeMonthlyReportActionBean")
public class BcStudentLifeMonthlyReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifePolicyMonthlyReportService}")
	private ILifePolicyMonthlyReportService lifePolicyMonthlyReportService;

	public void setLifePolicyMonthlyReportService(ILifePolicyMonthlyReportService lifePolicyMonthlyReportService) {
		this.lifePolicyMonthlyReportService = lifePolicyMonthlyReportService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private List<BCStudentLifeView> bcStudentLifeViewList;
	private StudentLifeCriteria studentLifeCriteria;
	private Product product;
	private User user;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		product = productService.findProductById(ProductIDConfig.getStudentLifeId());
		reset();
	}

	public void search() {
		try {
			bcStudentLifeViewList = lifePolicyMonthlyReportService.findBCStudentLifePolicyMonthlyReportByCriteria(studentLifeCriteria);
			sortLists(bcStudentLifeViewList);
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public void sortLists(List<BCStudentLifeView> bcStudentLifeViewList) {
		Collections.sort(bcStudentLifeViewList, new Comparator<BCStudentLifeView>() {
			@Override
			public int compare(BCStudentLifeView obj1, BCStudentLifeView obj2) {
				if (obj1.getPolicyNo().equals(obj2.getPolicyNo()))
					return -1;
				else
					return obj1.getPolicyNo().compareTo(obj2.getPolicyNo());
			}
		});
	}

	public void reset() {
		studentLifeCriteria = new StudentLifeCriteria();
		studentLifeCriteria.setStartMonthType(-1);
		studentLifeCriteria.setEndMonthType(-1);
		studentLifeCriteria.setDaily(true);
		bcStudentLifeViewList = new ArrayList<BCStudentLifeView>();
	}

	public void exportExcel() {
		if (bcStudentLifeViewList.size() > 0) {
			ExternalContext ec = getFacesContext().getExternalContext();
			ec.responseReset();
			ec.setResponseContentType("application/vnd.ms-excel");
			String fileName = studentLifeCriteria.isDaily() ? "StudentLife_Renewal_Daily_Report.xlsx" : "StudentLife_Renewal_Monthly_Report.xlsx";
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				OutputStream op = ec.getResponseOutputStream();
				BcStudentLifeMonthlyReportExcel reportExcel = new BcStudentLifeMonthlyReportExcel();
				reportExcel.setStudentLifeCriteria(studentLifeCriteria);
				reportExcel.generate(op, bcStudentLifeViewList);
				getFacesContext().responseComplete();
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export BCStudentLife.xlsx", e);
			}
		} else {
			addErrorMessage("bcStudentLifeForm:lifePolicyInfoTable", MessageId.THERE_IS_NO_DATA);
		}

	}

	public void changeType() {
		studentLifeCriteria.setStartDate(null);
		studentLifeCriteria.setEndDate(null);
		studentLifeCriteria.setStartMonthType(-1);
		studentLifeCriteria.setEndMonthType(-1);
		studentLifeCriteria.setStartDate(null);
		studentLifeCriteria.setYear(0);
		PrimeFaces.current().resetInputs("bcStudentLifeForm:criteriaPanel");
	}

	public List<PaymentType> getPaymentTypes() {
		if (product == null) {
			return new ArrayList<PaymentType>();
		} else {
			return product.getPaymentTypeList();
		}
	}

	// Entity

	public void selectBranch() {
		selectBranchByEntityIdAndBranchId(studentLifeCriteria.getEntity() == null ? "" : studentLifeCriteria.getEntity().getId(), user.getBranch().getId());
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		studentLifeCriteria.setEntity(entity);
		studentLifeCriteria.setBranch(null);
		studentLifeCriteria.setSalePoint(null);
	}

	public void removeEntity() {
		studentLifeCriteria.setEntity(null);
		studentLifeCriteria.setBranch(null);
		studentLifeCriteria.setSalePoint(null);
	}

	///
	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		studentLifeCriteria.setBranch(branch);
		studentLifeCriteria.setSalePoint(null);
	}

	public void selectSalePoint() {
		selectSalePointByBranch(studentLifeCriteria.getBranch() == null ? "" : studentLifeCriteria.getBranch().getId());
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		studentLifeCriteria.setSalePoint(salePoint);
	}

	public StudentLifeCriteria getStudentLifeCriteria() {
		return studentLifeCriteria;
	}

	public void setStudentLifeCriteria(StudentLifeCriteria studentLifeCriteria) {
		this.studentLifeCriteria = studentLifeCriteria;
	}

	public List<BCStudentLifeView> getBcStudentLifeViewList() {
		return bcStudentLifeViewList;
	}

	public Product getProduct() {
		return product;
	}

	public void setEntityNull() {
		studentLifeCriteria.setBranch(null);
		studentLifeCriteria.setSalePoint(null);
		studentLifeCriteria.setEntity(null);
	}

	public void setBranchNull() {
		studentLifeCriteria.setBranch(null);
		studentLifeCriteria.setSalePoint(null);
	}

}
