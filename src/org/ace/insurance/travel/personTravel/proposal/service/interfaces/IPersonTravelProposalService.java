package org.ace.insurance.travel.personTravel.proposal.service.interfaces;

import java.util.List;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.travel.personTravel.proposal.PTPL001;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.component.SystemException;

public interface IPersonTravelProposalService {

	public void addNewPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO, String status, BancaassuranceProposal bancaassuranceProposal);

	public void updatePersonTravelProposal(PersonTravelProposal personTravelProposal, BancaassuranceProposal bancaassuranceProposal);

	public void deletePersonTravelProposal(PersonTravelProposal personTravelProposal);

	public List<PersonTravelProposal> findAllPersonTravelProposal();

	public PersonTravelProposal findPersonTravelProposalById(String id);

	public List<Payment> confirmPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch)
			throws SystemException;

	public void rejectPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO) throws SystemException;

	public void paymentPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO, List<Payment> payment, Branch branch, String status);

	public List<PTPL001> findPersonTravelDTOByCriteria(EnquiryCriteria criteria);

	public void issuePersonTravelProposal(PersonTravelProposal personTravelProposal);

}
