package org.ace.insurance.web.manage.medical.initialreport.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;

import org.ace.insurance.medical.claim.ClaimInitialReport;
import org.ace.insurance.medical.claim.MedicalClaimCriteria;
import org.ace.insurance.medical.claim.service.interfaces.IClaimInitialReportService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageMedicalClaimInitialReportActionBean")
public class ManageMedicalClaimInitialReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_PARAM_SUFFIX = "_PARAM";
	private static final String MESSAGE_REQUEST_PARAM_SUFFIX = "_REQUEST_PARAM";

	@ManagedProperty(value = "#{ClaimInitialReportService}")
	private IClaimInitialReportService claimInitialReportService;

	public void setClaimInitialReportService(IClaimInitialReportService claimInitialReportService) {
		this.claimInitialReportService = claimInitialReportService;
	}

	private List<ClaimInitialReport> claimInitialReportList;
	private MedicalClaimCriteria criteria;
	private String product;

	@PostConstruct
	public void init() {
		claimInitialReportList = new ArrayList<ClaimInitialReport>();
		resetCriteria();
	}

	public void resetCriteria() {
		criteria = new MedicalClaimCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		product = "Individual Critical Illness";

	}

	public void search() {
		if (valid(criteria)) {
			claimInitialReportList = claimInitialReportService.findActiveClaimByCriteria(criteria);
		}
	}

	public boolean valid(MedicalClaimCriteria criteria) {
		boolean valid = true;
		String form = "medicalClaimInitialForm";
		if (criteria.getStartDate() != null) {
			if (criteria.getEndDate() != null) {
				valid = true;
			} else {
				addErrorMessage(form + ":endDate", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
		}
		if (criteria.getEndDate() != null) {
			if (criteria.getStartDate() != null) {
				valid = true;
			} else {
				addErrorMessage(form + ":startDate", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
		}
		return valid;
	}

	public String prepareUpdateClaimInitialReport(ClaimInitialReport claimInitialReport) {
		String result = null;
		putParam("claimInitialReport", claimInitialReport);
		result = "medicalClaimInitialReport";
		return result;
	}

	public String submitClaimRequest(ClaimInitialReport claimInitialReport) {
		putParam("medicalClaimInitialReport", claimInitialReport);
		return "medicalClaimRequest";
	}

	public void selectMedicalPolicyNo() {
		selectMedicalPolicyNo(product);
	}

	public void changeProduct(AjaxBehaviorEvent event) {
		criteria.setPolicyNo(null);
	}

	public void returnMedicalPolicyNo(SelectEvent event) {
		MedicalPolicy medicalPolicy = (MedicalPolicy) event.getObject();
		criteria.setPolicyNo(medicalPolicy.getPolicyNo());
	}

	public List<String> getProductList() {

		return Arrays.asList("Individual Critical Illness", "Group Critical Illness", "Individual Health", "Group Health", "Micro Health", "Medical");
	}

	public MedicalClaimCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MedicalClaimCriteria criteria) {
		this.criteria = criteria;
	}

	public List<ClaimInitialReport> getClaimInitialReportList() {
		return claimInitialReportList;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public void checkMessage(ComponentSystemEvent event) {
		ExternalContext extContext = getFacesContext().getExternalContext();
		String messageId = (String) extContext.getSessionMap().get(Constants.MESSAGE_ID);
		String proposalNo = (String) extContext.getSessionMap().get(Constants.PROPOSAL_NO);
		String requestNo = (String) extContext.getSessionMap().get(Constants.REQUEST_NO);
		if (messageId != null) {
			if (proposalNo != null) {
				addInfoMessage(null, messageId + MESSAGE_PARAM_SUFFIX, proposalNo);
				extContext.getSessionMap().remove(Constants.MESSAGE_ID);
				extContext.getSessionMap().remove(Constants.PROPOSAL_NO);
			} else {
				if (requestNo != null) {
					addInfoMessage(null, messageId + MESSAGE_REQUEST_PARAM_SUFFIX, requestNo);
					extContext.getSessionMap().remove(Constants.MESSAGE_ID);
					extContext.getSessionMap().remove(Constants.REQUEST_NO);
				} else {
					addInfoMessage(null, messageId);
					extContext.getSessionMap().remove(Constants.MESSAGE_ID);
				}
			}
		}
	}

}
