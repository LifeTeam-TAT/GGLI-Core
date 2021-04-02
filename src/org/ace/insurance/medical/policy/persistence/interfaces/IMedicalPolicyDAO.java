/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.medical.policy.persistence.interfaces;

import java.util.List;

import org.ace.insurance.common.NotificationCriteria;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.medical.claim.ClaimStatus;
import org.ace.insurance.medical.claim.MedicalPolicyCriteria;
import org.ace.insurance.medical.policy.MED002;
import org.ace.insurance.medical.policy.MPL001;
import org.ace.insurance.medical.policy.MPL002;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.web.manage.enquires.PolicyEnquiryCriteria;
import org.ace.insurance.web.manage.life.billcollection.BillCollectionDTO;
import org.ace.insurance.web.manage.life.billcollection.PolicyNotificationDTO;
import org.ace.insurance.web.manage.medical.proposal.PolicyGuardianDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface IMedicalPolicyDAO {
	public void insert(MedicalPolicy medicalPolicy) throws DAOException;

	public void update(MedicalPolicy medicalPolicy) throws DAOException;

	public void delete(MedicalPolicy medicalPolicy) throws DAOException;

	public List<String> findMedPolicyInsuredPersonById(String id) throws DAOException;

	public MedicalPolicy findById(String id) throws DAOException;

	public MedicalPolicy findByProposalId(String proposalId) throws DAOException;

	public List<MedicalPolicy> findAll() throws DAOException;

	public MedicalPolicy findMedicalPolicyByPolicyNo(String policyNo) throws DAOException;

	public List<MED002> findMedicalPolicyForPolicyEnquiry(PolicyEnquiryCriteria criteria) throws DAOException;

	public List<MPL002> findMedicalPolicyForClaimByCriteria(MedicalPolicyCriteria criteria) throws DAOException;

	public List<MPL002> findMedicalPolicyForClaimByProduct(String product) throws DAOException;

	public void updateBeneficiaryClaimStatusByBeneficiaryNo(String id, ClaimStatus status) throws DAOException;

	public void updatePolicyInsuBeneByBeneficiaryNo(MedicalPolicyInsuredPersonBeneficiaries medicalPolicyInsuredPersonBeneficiaries) throws DAOException;

	public void updatePolicyGuardian(PolicyGuardianDTO policyGuardianDTO) throws DAOException;

	public void updatePolicyInsuredPerson(MedicalPolicyInsuredPerson medicalPolicyInsuredPerson) throws DAOException;

	public List<MedicalPolicy> findByCustomer(Customer customer) throws DAOException;

	public void updateBillCollection(MedicalPolicy medicalPolicy) throws DAOException;

	public List<BillCollectionDTO> findBillCollectionByCriteria(PolicyCriteria criteria) throws DAOException;

	public List<MPL001> findPolicyByCriteria(MedicalPolicyCriteria criteria) throws DAOException;

	public List<PolicyNotificationDTO> findPolicyNotificationByCriteria(NotificationCriteria criteria) throws DAOException;

	public List<MedicalPolicy> findPaymentOrderByReceiptNo(String receiptNo, String bpmsReceiptNo, PolicyReferenceType policyReferenceType) throws DAOException;

}
