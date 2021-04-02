package org.ace.insurance.payment.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.payment.AC001;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.persistence.interfacs.IStaffAgentCommissionDAO;
import org.ace.insurance.payment.service.interfaces.IStaffAgentCommissionService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.web.manage.agent.AgentEnquiryCriteria;
import org.ace.java.component.SystemException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;

@Service(value = "StaffAgentCommissionService")
public class StaffAgentCommissionService extends BaseService implements IStaffAgentCommissionService {

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "StaffAgentCommissionDAO")
	private IStaffAgentCommissionDAO staffagentCommissionDAO;

	@Override
	public void addNewAgentCommisssion(MedicalPolicy medicalPolicy, String chalanNo) throws SystemException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAgentCommisssion(MedicalPolicy medicalPolicy, AgentCommission agentComission)
			throws SystemException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Agent> findAgentByCommissionCriteria(AgentEnquiryCriteria agentEnquiryCriteria) throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AC001> findAgentCommissionByAgent(AgentEnquiryCriteria agentEnquiryCriteria) throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AgentCommission findAgentCommissionByChallanNo(String challanNo) throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AgentCommission findAgentCommissionByPolicyId(String id) throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}


}
