package org.ace.insurance.web.manage.enquires.medical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.life.bancassurance.policy.BancaassurancePolicy;
import org.ace.insurance.life.bancassurance.policy.service.interfaces.IBancaassurancePolicyService;
import org.ace.insurance.medical.policy.MED002;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.user.User;
import org.ace.insurance.web.manage.enquires.PolicyEnquiryCriteria;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.MedicalProposalFactory;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.primefaces.event.SelectEvent;

/**
 * @author PPA
 *
 */
@ViewScoped
@ManagedBean(name = "MedicalPolicyEnquiryActionBean")
public class MedicalPolicyEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{BancaassurancePolicyService}")
	private IBancaassurancePolicyService bancaassurancePolicyService;

	public void setBancaassurancePolicyService(IBancaassurancePolicyService bancaassurancePolicyService) {
		this.bancaassurancePolicyService = bancaassurancePolicyService;
	}

	private User user;
	private boolean accessBranches;
	private PolicyEnquiryCriteria criteria;
	private List<MED002> policyList;
	private MedicalPolicy policy;
	private MedProDTO medProDTO;
	private HealthType healthType;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		resetCriteria();
	}

	public void resetCriteria() {
		criteria = new PolicyEnquiryCriteria();
		if (user.isAccessAllBranch()) {
			accessBranches = true;
		} else {
			criteria.setBranch(user.getBranch());
		}
		policyList = new ArrayList<MED002>();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		criteria.setEndDate(new Date());
		this.healthType = null;
		policyList = new ArrayList<>();

	}

	public void findPolicyListByEnquiryCriteria() {

		if (null != healthType) {
			switch (healthType) {
				case CRITICALILLNESS:
					this.criteria.setProductIdList(Arrays.asList(ProductIDConfig.getIndividualCriticalIllness_Id(), ProductIDConfig.getGroupCriticalIllness_Id()));
					break;
				case HEALTH:
					this.criteria.setProductIdList(Arrays.asList(ProductIDConfig.getIndividualHealthInsuranceId(), ProductIDConfig.getGroupHealthInsuranceId()));
					break;
				case MEDICAL_INSURANCE:
					this.criteria.setProductIdList(Arrays.asList(ProductIDConfig.getMedicalInsurance()));
					break;
				case MICROHEALTH:
					this.criteria.setProductIdList(Arrays.asList(ProductIDConfig.getMicroHealthInsurance()));
					break;
				default:
					break;

			}
		}

		policyList = medicalPolicyService.findMedicalPolicyForPolicyEnquiry(criteria);
		sortLists(policyList);
	}

	public void clearList() {
		policyList = new ArrayList<>();
	}

	public void sortLists(List<MED002> farmerPolicyList) {
		Collections.sort(farmerPolicyList, new Comparator<MED002>() {
			@Override
			public int compare(MED002 obj1, MED002 obj2) {
				if (obj1.getPolicyNo().equals(obj2.getPolicyNo()))
					return -1;
				else
					return obj1.getPolicyNo().compareTo(obj2.getPolicyNo());
			}
		});
	}

	public void showPolicyDetail(String id) {
		policy = medicalPolicyService.findMedicalPolicyById(id);

	}

	public List<WorkFlowHistory> getWorkFlowList() {
		if (policy != null) {
			return workFlowService.findWorkFlowHistoryByRefNo(policy.getId());
		} else {
			return null;
		}
	}

	public MedProDTO getMedProDTO() {
		if (policy != null) {
			medProDTO = MedicalProposalFactory.getMedProDTO(policy.getMedicalProposal());
		}
		return medProDTO;
	}

	public String editMedicalPolicy(String id) {
		policy = medicalPolicyService.findMedicalPolicyById(id);
		BancaassurancePolicy bancaassurancePolicy = bancaassurancePolicyService.findBancaassurancePolicyByMedicalpolicyId(id);
		putParam("bancaassurancePolicy", bancaassurancePolicy);
		putParam("medicalPolicy", policy);
		outjectHealthType(healthType);
		return "editMedicalPolicy";

	}

	private void outjectHealthType(HealthType healthType) {
		putParam("HEALTHTYPE", healthType);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		criteria.setAgent(agent);
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		criteria.setSaleMan(saleMan);
	}

	public void returnReferral(SelectEvent event) {
		Customer referral = (Customer) event.getObject();
		criteria.setReferral(referral);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		criteria.setCustomer(customer);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		criteria.setOrganization(organization);
	}

	/**
	 * @return the isAccessBranches
	 */
	public boolean isAccessBranches() {
		return accessBranches;
	}

	/**
	 * @param isAccessBranches
	 *            the isAccessBranches to set
	 */
	public void setAccessBranches(boolean accessBranches) {
		this.accessBranches = accessBranches;
	}

	/**
	 * @return the criteria
	 */
	public PolicyEnquiryCriteria getCriteria() {
		return criteria;
	}

	/**
	 * @param criteria
	 *            the criteria to set
	 */
	public void setCriteria(PolicyEnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	/**
	 * @return the policy
	 */
	public MedicalPolicy getPolicy() {
		return policy;
	}

	/**
	 * @param policy
	 *            the policy to set
	 */
	public void setPolicy(MedicalPolicy policy) {
		this.policy = policy;
	}

	/**
	 * @return the policyList
	 */
	public List<MED002> getPolicyList() {
		return policyList;
	}

	public HealthType getHealthType() {
		return healthType;
	}

	public void setHealthType(HealthType healthType) {
		this.healthType = healthType;
	}

	public HealthType[] getHealthTypes() {
		return HealthType.values();
	}

}
