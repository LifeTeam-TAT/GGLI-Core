package org.ace.insurance.web.manage.enquires;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ReceiptNoCriteria;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.report.TLF.TLFVoucherCriteria;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.apache.commons.lang3.StringUtils;

@ViewScoped
@ManagedBean(name = "PaymentOrderEnquiryActionBean")
public class PaymentOrderEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private TLFVoucherCriteria criteria;
	private String bpmsReceiptNo;
	private String receiptNo;
	private ReceiptNoCriteria receiptNoCriteria;
	private PolicyReferenceType referenceType;
	private IPolicy[] selectedPolicyList;
	private List<IPolicy> policyList;
	private List<Payment> searchPaymentList;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
	}

	@ManagedProperty(value = "#{GroupfarmerProposalService}")
	private IGroupfarmerProposalService groupFarmerProposalService;

	public void setGroupFarmerProposalService(IGroupfarmerProposalService groupFarmerProposalService) {
		this.groupFarmerProposalService = groupFarmerProposalService;
	}

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	@PostConstruct
	public void init() {
		policyList = new ArrayList<IPolicy>();
		receiptNoCriteria = new ReceiptNoCriteria();
		referenceType = PolicyReferenceType.values()[0];

	}

	public void search() {
		getUpdatedPolicys();
		if (policyList.size() == 0) {
			addWranningMessage(null, MessageId.NO_RECORDS_FOUND);
			return;
		}
	}

	private void getUpdatedPolicys() {
		policyList.clear();
		switch (referenceType) {

			case HEALTH_POLICY:
			case MICRO_HEALTH_POLICY:
			case HEALTH_POLICY_BILL_COLLECTION:
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
			case CRITICAL_ILLNESS_POLICY:
				List<MedicalPolicy> medicalPolicies = medicalPolicyService.findLifePolicyPOByReceiptNo(receiptNo, bpmsReceiptNo, referenceType);
				for (MedicalPolicy policy : medicalPolicies)
					policyList.add(policy);
				break;
			case STUDENT_LIFE_POLICY:
			case STUDENT_LIFE_POLICY_BILL_COLLECTION:
			case SNAKE_BITE_POLICY:
			case FARMER_POLICY:
			case PA_POLICY:
			case LIFE_POLICY:
			case LIFE_BILL_COLLECTION:
			case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
			case SHORT_ENDOWMENT_LIFE_POLICY:
			case SIMPLE_LIFE_POLICY:
			case SIMPLE_LIFE_BILL_COLLECTION:
				List<LifePolicy> lifePolicys = lifePolicyService.findLifePolicyPOByReceiptNo(receiptNo, bpmsReceiptNo, referenceType);
				for (LifePolicy policy : lifePolicys)
					policyList.add(policy);
				break;
			case PERSON_TRAVEL_POLICY:
				List<PersonTravelPolicy> personTravelPolicyList = personTravelPolicyService.findPersonTravelPolicyByPOByReceiptNo(receiptNo, bpmsReceiptNo);
				for (PersonTravelPolicy policy : personTravelPolicyList)
					policyList.add(policy);
				break;
			case GROUP_FARMER_PROPOSAL:
				List<GroupFarmerProposal> groupFarmerProposalList = groupFarmerProposalService.finGroupFarmerProposalByPOByReceiptNo(receiptNo, bpmsReceiptNo);
				for (GroupFarmerProposal proposal : groupFarmerProposalList) {
					policyList.add(proposal);
				}
				break;
			case TRAVEL_PROPOSAL:
				List<TravelProposal> travelProposalList = travelProposalService.findTravelProposalByPOByReceiptNo(receiptNo, bpmsReceiptNo);
				for (TravelProposal proposal : travelProposalList) {
					policyList.add(proposal);
				}
				break;
			default:
				break;
		}
	}

	public void reset() {
		bpmsReceiptNo = null;
		receiptNo = null;
		referenceType = PolicyReferenceType.values()[0];
		policyList = new ArrayList<IPolicy>();
	}

	public void resetPolicyCriteria() {
		receiptNoCriteria.setCriteriaValue("");
		searchPaymentList = new ArrayList<Payment>();
	}

	private boolean inputCheck(ReceiptNoCriteria receiptNoCriteria) {
		boolean result = true;
		if ((receiptNoCriteria.getReceiptNoCriteria() == null) || (receiptNoCriteria.getReceiptNoCriteria().toString().equals("Select"))) {
			getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select receipt No criteria.", "Please select receipt No criteria"));
			result = false;
		} else if (StringUtils.isBlank(receiptNoCriteria.getCriteriaValue())) {
			getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Criteria value is required.", "Criteria value is required."));
			result = false;
		}
		return result;
	}

	public void doClearing() {
		try {
			if (!validateCheck()) {
				return;
			}
			List<Payment> paymentList = new ArrayList<Payment>();
			for (IPolicy policy : selectedPolicyList) {
				paymentList.addAll(paymentService.findByReceiptNoandPolicyId(receiptNo, policy.getId()));
			}
			paymentService.activateTLFClearing(paymentList);
			addInfoMessage(null, MessageId.PAYMENT_ORDER_PROCESS_SUCCESS);
			getUpdatedPolicys();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	private boolean validateCheck() {
		boolean valid = true;
		if (selectedPolicyList.length == 0) {
			addWranningMessage(null, MessageId.ATLEAST_ONE_CHECK_REQUIRED);
			valid = false;
		}
		return valid;
	}

	public PolicyReferenceType[] getReferenceTypes() {
		return new PolicyReferenceType[] { PolicyReferenceType.LIFE_POLICY, PolicyReferenceType.PA_POLICY, PolicyReferenceType.PERSON_TRAVEL_POLICY,
				PolicyReferenceType.FARMER_POLICY, PolicyReferenceType.LIFE_BILL_COLLECTION, PolicyReferenceType.HEALTH_POLICY, PolicyReferenceType.MICRO_HEALTH_POLICY,
				PolicyReferenceType.CRITICAL_ILLNESS_POLICY, PolicyReferenceType.SNAKE_BITE_POLICY, PolicyReferenceType.MEDICAL_POLICY,
				PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY, PolicyReferenceType.GROUP_FARMER_PROPOSAL, PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION,
				PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION, PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION, PolicyReferenceType.TRAVEL_PROPOSAL,
				PolicyReferenceType.STUDENT_LIFE_POLICY, PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION,
				PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY, PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION,
				PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY, PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION,
				PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY, PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION,
				PolicyReferenceType.SIMPLE_LIFE_POLICY, PolicyReferenceType.SIMPLE_LIFE_BILL_COLLECTION};
	}

	public PaymentOrderDataModel getPaymentOrderDataModel() {
		return new PaymentOrderDataModel(policyList);
	}

	private String message;

	public String getMessage() {
		return message;
	}

	public List<Payment> getSearchPaymentList() {
		return searchPaymentList;
	}

	public ReceiptNoCriteria getReceiptNoCriteria() {
		return receiptNoCriteria;
	}

	public void setReceiptNoCriteria(ReceiptNoCriteria receiptNoCriteria) {
		this.receiptNoCriteria = receiptNoCriteria;
	}

	public PolicyReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(PolicyReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public List<IPolicy> getPolicyList() {
		return policyList;
	}

	public void setPolicyList(List<IPolicy> policyList) {
		this.policyList = policyList;
	}

	public IPolicy[] getSelectedPolicyList() {
		return selectedPolicyList;
	}

	public void setSelectedPolicyList(IPolicy[] selectedPolicyList) {
		this.selectedPolicyList = selectedPolicyList;
	}

	public TLFVoucherCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(TLFVoucherCriteria criteria) {
		this.criteria = criteria;
	}

	public String getBpmsReceiptNo() {
		return bpmsReceiptNo;
	}

	public void setBpmsReceiptNo(String bpmsReceiptNo) {
		this.bpmsReceiptNo = bpmsReceiptNo;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

}
