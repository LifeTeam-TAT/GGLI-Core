package org.ace.insurance.web.manage.life.surrender.proposal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.claimproduct.ClaimProduct;
import org.ace.insurance.claimproduct.service.interfaces.IClaimProductService;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.surrender.LifeSurrenderKeyFactor;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.life.surrender.PaymentTrackDTO;
import org.ace.insurance.life.surrender.service.interfaces.ILifeSurrenderProposalService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.joda.time.Period;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "EditLifeSurrenderProposalActionBean")
public class EditLifeSurrenderProposalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{LifeSurrenderProposalService}")
	private ILifeSurrenderProposalService lifeSurrenderProposalService;

	public void setLifeSurrenderProposalService(ILifeSurrenderProposalService lifeSurrenderProposalService) {
		this.lifeSurrenderProposalService = lifeSurrenderProposalService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{ClaimProductService}")
	private IClaimProductService claimProductService;

	public void setClaimProductService(IClaimProductService claimProductService) {
		this.claimProductService = claimProductService;
	}

	private User user;
	private LifeSurrenderProposal lifeSurrenderProposal;
	private List<PaymentTrackDTO> paymentList;
	private String remark;
	private String paymentTerm;
	private User responsiblePerson;
	private boolean isExtendPayment;
	private int paymentYear;
	private int paymentMonth;
	private Period paymentPeriod;
	private double surrenderAmount;
	private boolean isEndowmentSurrender;

	@PostConstruct
	public void init() {
		initializeInjection();
		LifePolicy lifePolicy = lifeSurrenderProposal.getLifePolicy();
		paymentPeriod = Utils.getPeriod(lifePolicy.getActivedPolicyStartDate(), lifePolicy.getActivedPolicyEndDate(), false, true);
		paymentList = paymentService.findPaymentTrack(lifePolicy.getPolicyNo());
		double receivedPremium = 0;
		for (PaymentTrackDTO paymentTrack : paymentList) {
			receivedPremium += paymentTrack.getPremium().doubleValue();
		}

		lifeSurrenderProposal.setReceivedPremium(receivedPremium);
		loadPaymentConfig();
		isEndowmentSurrender = lifeSurrenderProposal.getClaimProduct().equals(KeyFactorChecker.getLifeSurrenderProductId());

	}

	private void initializeInjection() {
		user = (User) getParam(Constants.LOGIN_USER);
		lifeSurrenderProposal = (LifeSurrenderProposal) getParam("surrenderProposal");
	}

	@PreDestroy
	public void destory() {
		removeParam(Constants.LOGIN_USER);
		removeParam("surrenderProposal");

	}

	public void returnLifePolicy(SelectEvent event) {
		LifePolicy lifePolicy = (LifePolicy) event.getObject();
		double receivedPremium = 0.0;
		if (checkLifePolicy(lifePolicy)) {
			lifeSurrenderProposal.setLifePolicy(lifePolicy);
			lifeSurrenderProposal.setPolicyNo(lifePolicy.getPolicyNo());
			lifeSurrenderProposal.setSumInsured(lifePolicy.getSumInsured());
			paymentPeriod = Utils.getPeriod(lifePolicy.getActivedPolicyStartDate(), lifePolicy.getActivedPolicyEndDate(), false, true);
			paymentList = paymentService.findPaymentTrack(lifePolicy.getPolicyNo());
			for (PaymentTrackDTO paymentTrack : paymentList) {
				receivedPremium += paymentTrack.getPremium().doubleValue();
			}

			lifeSurrenderProposal.setReceivedPremium(receivedPremium);
			loadPaymentConfig();
		}

	}

	public String updateSurrenderProposal() {
		String result = null;
		WorkFlowDTO workFlowDTO = null;
		try {
			PolicyInsuredPerson insured = lifeSurrenderProposal.getLifePolicy().getInsuredPersonInfo().get(0);
			String age = Integer.toString(insured.getAge());
			String policyPeriod = Integer.toString(insured.getPeriodYears());
			List<LifeSurrenderKeyFactor> keyFactorList = new ArrayList<>();
			for (KeyFactor keyFactor : lifeSurrenderProposal.getClaimProduct().getKeyFactors()) {
				if (KeyFactorChecker.isSurrenderAge(keyFactor))
					keyFactorList.add(new LifeSurrenderKeyFactor(age, keyFactor));

				if (KeyFactorChecker.isPolicyPeriod(keyFactor))
					keyFactorList.add(new LifeSurrenderKeyFactor(policyPeriod, keyFactor));

				if (KeyFactorChecker.isPaymentYear(keyFactor))
					keyFactorList.add(new LifeSurrenderKeyFactor(paymentYear + "", keyFactor));
			}

			lifeSurrenderProposal.setLifeSurrenderKeyFactors(keyFactorList);
			if (isEndowmentSurrender)
				workFlowDTO = new WorkFlowDTO(null, remark, WorkflowTask.APPROVAL, WorkflowReferenceType.LIFESURRENDER_PROPOSAL, user, responsiblePerson);
			else
				workFlowDTO = new WorkFlowDTO(null, remark, WorkflowTask.APPROVAL, WorkflowReferenceType.SHORTENDOWMENTLIFESURRENDER_PROPOSAL, user, responsiblePerson);

			lifeSurrenderProposalService.updateLifeSurrenderProposal(lifeSurrenderProposal, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeSurrenderProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}

		return result;
	}

	public boolean checkIsExtendPayment() {
		boolean valid = true;
		paymentMonth = paymentPeriod.getMonths();
		if (paymentMonth > 0) {
			valid = true;
		} else {
			valid = false;
		}
		return valid;
	}

	public void loadPaymentConfig() {
		int paymentType = 0;
		double basicPremium = 0;
		double lifePremium = 0.0;
		paymentMonth = paymentPeriod.getMonths();
		paymentYear = paymentPeriod.getYears();
		if (isExtendPayment) {
			paymentType = lifeSurrenderProposal.getLifePolicy().getPaymentType().getMonth();
			basicPremium = (Double) paymentList.get(0).getPremium().doubleValue();
			switch (paymentType) {
				case 1:
					lifePremium = basicPremium * (12 - paymentMonth);
					break;
				case 3:
					lifePremium = basicPremium * (12 - paymentMonth) / paymentType;
					break;
				case 6:
					lifePremium = basicPremium * (12 - paymentMonth) / paymentType;
					break;
				case 12:
					lifePremium = basicPremium;
					break;
				default:
					break;
			}

			paymentYear = paymentYear + 1;
			paymentMonth = 0;
		}

		calculatePaymentTerm();
		lifeSurrenderProposal.setLifePremium(lifePremium);

	}

	public void selectUser() {
		selectUser(WorkflowTask.APPROVAL, WorkFlowType.LIFESURRENDER);
	}

	private boolean checkLifePolicy(LifePolicy lifePolicy) {
		boolean result = true;

		boolean havePendingPayment = paymentService.havePendingPayment(lifePolicy.getPolicyNo());
		if (havePendingPayment) {
			addInfoMessage(null, MessageId.INVALID_LIFE_BILL_COLLECTION);
			return false;
		}

		PolicyInsuredPerson person = lifePolicy.getPolicyInsuredPersonList().get(0);
		ClaimProduct claimProduct = null;
		if (isEndowmentSurrender) {
			claimProduct = claimProductService.findClaimProductById(KeyFactorChecker.getLifeSurrenderProductId());
		} else {
			claimProduct = claimProductService.findClaimProductById(KeyFactorChecker.getShortTermEndowmentSurrenderProductId());
		}

		lifeSurrenderProposal.setClaimProduct(claimProduct);
		Period period = Utils.getPeriod(person.getStartDate(), person.getEndDate(), false, true);
		int policyYear = period.getYears();
		period = Utils.getPeriod(lifePolicy.getActivedPolicyStartDate(), lifePolicy.getActivedPolicyEndDate(), false, true);
		int paymentYear = period.getYears();
		if (policyYear >= 13) {
			result = paymentYear >= claimProduct.getLongTermActivatedYear() ? true : false;
			if (!result)
				addInfoMessage(null, MessageId.INVALID_LIFE_SURRENDER_PERIOD, lifePolicy.getPolicyNo(), policyYear, claimProduct.getLongTermActivatedYear());

		} else {
			result = paymentYear >= claimProduct.getShortTermActivatedYear() ? true : false;
			if (!result)
				addInfoMessage(null, MessageId.INVALID_LIFE_SURRENDER_PERIOD, lifePolicy.getPolicyNo(), policyYear, claimProduct.getShortTermActivatedYear());

		}

		return result;
	}

	public LifeSurrenderProposal getLifeSurrenderProposal() {
		return lifeSurrenderProposal;
	}

	public List<PaymentTrackDTO> getPaymentList() {
		return paymentList;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public double getSurrenderAmount() {
		return surrenderAmount;
	}

	public boolean isExtendPayment() {
		return isExtendPayment;
	}

	public void setExtendPayment(boolean isExtendPayment) {
		this.isExtendPayment = isExtendPayment;
	}

	public void selectLifePolicy() {
		String productId = null;
		if (isEndowmentSurrender) {
			productId = KeyFactorChecker.getPublicLifeID();
		} else {
			productId = KeyFactorChecker.getShortTermEndowmentID();
		}
		selectLifePolicy(productId);
	}

	private void calculatePaymentTerm() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(paymentYear + " YEARS ");
		if (paymentMonth > 0)
			buffer.append(paymentMonth == 1 ? " AND" + paymentMonth + " MONTH" : paymentMonth + " MONTHS");
		paymentTerm = buffer.toString();
	}

	public String getPaymentTerm() {
		return paymentTerm;
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		lifeSurrenderProposal.setBranch(branch);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		lifeSurrenderProposal.setSaleMan(saleMan);
	}

}
