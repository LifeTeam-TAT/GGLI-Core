package org.ace.insurance.web.manage.mobile.travel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;

import org.ace.insurance.mobile.travelProposal.MTP002;
import org.ace.insurance.mobile.travelProposal.TravelProposalCriteria;
import org.ace.insurance.mobile.travelProposal.service.interfaces.IMobileTravelProposalService;
import org.ace.insurance.mobile.user.service.interfaces.IMobileUserService;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "MobileDataConverterActionBean")
public class MobileDataConverterActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{MobileTravelProposalService}")
	private IMobileTravelProposalService mobileTravelProposalService;

	public void setMobileTravelProposalService(IMobileTravelProposalService mobileTravelProposalService) {
		this.mobileTravelProposalService = mobileTravelProposalService;
	}

	@ManagedProperty(value = "#{MobileUserServices}")
	private IMobileUserService mobileUserService;

	public void setMobileUserService(IMobileUserService mobileUserService) {
		this.mobileUserService = mobileUserService;
	}

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
	}

	private TravelProposalCriteria travelProposalCriteria;
	private List<MTP002> mobileTravelProposalList;
	private List<MTP002> selectedMobileTravelList;
	private Date paymentDate;

	@PostConstruct
	public void init() {
		reset();
	}

	public void reset() {
		travelProposalCriteria = new TravelProposalCriteria();
		mobileTravelProposalList = new ArrayList<MTP002>();
		selectedMobileTravelList = new ArrayList<MTP002>();
		travelProposalCriteria.setStartDate(new Date());
		travelProposalCriteria.setEndDate(new Date());
		paymentDate = null;
	}

	public void search() {
		if (validation()) {
			mobileTravelProposalList = mobileTravelProposalService.findMobileTravelProposalByCriteria(travelProposalCriteria);
		}
	}

	private boolean validation() {
		boolean valid = true;
		String formID = "mobiledataqueryForm";

		if (travelProposalCriteria.getStartDate().after(travelProposalCriteria.getEndDate())) {
			addErrorMessage(formID + ":paymentEndDate", MessageId.INVALID_DATE);
			valid = false;
		}
		return valid;
	}

	public void convertToCore() {
		try {
			if (validateForConvert()) {
				personTravelPolicyService.convertDataToCore(selectedMobileTravelList, paymentDate);
				addInfoMessage(null, MessageId.MOBILE_DATA_CONVERT_PROCESS_SUCCESS, selectedMobileTravelList.size());
				reset();
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	private boolean validateForConvert() {
		boolean valid = true;
		String formID = "mobiledataqueryForm";
		if (selectedMobileTravelList.size() == 0) {
			addErrorMessage(formID + ":mobileDataTable", MessageId.ATLEAST_ONE_CHECK_REQUIRED);
			valid = false;
		}
		if (paymentDate == null) {
			addErrorMessage(formID + ":paymentDate", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		return valid;
	}

	public TravelProposalCriteria getTravelProposalCriteria() {
		return travelProposalCriteria;
	}

	public List<MTP002> getMobileTravelProposalList() {
		return mobileTravelProposalList;
	}

	public void setMobileTravelProposalList(List<MTP002> mobileTravelProposalList) {
		this.mobileTravelProposalList = mobileTravelProposalList;
	}

	public List<MTP002> getSelectedMobileTravelList() {
		return selectedMobileTravelList;
	}

	public void setSelectedMobileTravelList(List<MTP002> selectedMobileTravelList) {
		this.selectedMobileTravelList = selectedMobileTravelList;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

}
