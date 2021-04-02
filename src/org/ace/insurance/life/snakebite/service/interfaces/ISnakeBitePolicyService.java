package org.ace.insurance.life.snakebite.service.interfaces;

import java.util.List;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.life.snakebite.SnakeBitePolicyCriteria;
import org.ace.insurance.life.snakebite.SnakeBitePolicyDTO;
import org.ace.insurance.life.snakebite.SnakeBitePolicyNoCriteria;
import org.ace.insurance.life.snakebite.SnakeBitePolicySearch;
import org.ace.insurance.payment.Payment;

public interface ISnakeBitePolicyService {
	public SnakeBitePolicy addNewSnakeBitePolicy(SnakeBitePolicy snakeBitePolicy);

	public SnakeBitePolicy updateSnakeBitePolicy(SnakeBitePolicy snakeBitePolicy);

	public void deleteSnakeBitePolicy(SnakeBitePolicy snakeBitePolicy);

	public SnakeBitePolicy findSnakeBitePolicyById(String id);

	public List<SnakeBitePolicy> findSnakeBitePolicy(List<String> proposalIdList);

	public List<SnakeBitePolicy> findAllSnakeBitePolicy();

	public void calculatePremium(SnakeBitePolicy snakeBitePolicy);

	public List<SnakeBitePolicy> findSnakeBitePolicyByCriteria(SnakeBitePolicyCriteria snakeBitePolicyCriteria);

	public void paymentSnakeBitePolicy(List<SnakeBitePolicy> snakeBitePolicyList, Payment payment);

	public List<Payment> confirmSnakeBitePolicy(List<SnakeBitePolicy> snakeBitePolicyList, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO);

	public List<SnakeBitePolicy> findSnakeBitePolicyByReceiptNo(String receiptNo);

	public SnakeBitePolicyDTO findSnakeBitePolicyForPaymentDashboard(String referenceNo);

	public List<String> findBookNo();

	public List<SnakeBitePolicySearch> findSnakeBitePolicyNoByCriteria(SnakeBitePolicyNoCriteria criteria);

	public SnakeBitePolicy findSnakeBitePolicyByPolicyNo(String snakeBitePolicyNo);
}
