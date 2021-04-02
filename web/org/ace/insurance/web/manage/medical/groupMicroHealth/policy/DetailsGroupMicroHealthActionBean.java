package org.ace.insurance.web.manage.medical.groupMicroHealth.policy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.medical.groupMicroHealth.proposal.service.interfaces.IGroupMicroHealthService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.system.productinformation.Language;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.document.medical.MedicalUnderwritingDocFactory;
import org.ace.insurance.web.manage.enquires.medical.groupMicroHealth.GroupMicroHealthCriteria;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;

@ViewScoped
@ManagedBean(name = "DetailsGroupMicroHealthActionBean")
public class DetailsGroupMicroHealthActionBean extends BaseBean {

	@ManagedProperty(value = "#{GroupMicroHealthService}")
	private IGroupMicroHealthService groupMicroHealthService;

	public void setGroupMicroHealthService(IGroupMicroHealthService groupMicroHealthService) {
		this.groupMicroHealthService = groupMicroHealthService;
	}

	@ManagedProperty(value = "#{MedicalProposalService}")
	private IMedicalProposalService medicalProposalService;

	public void setMedicalProposalService(IMedicalProposalService medicalProposalService) {
		this.medicalProposalService = medicalProposalService;
	}

	private List<GroupMicroHealthDTO> groupMicroHealthDTOList;

	private User user;
	private final String reportName = "Medical";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";
	private GroupMicroHealthCriteria criteria;

	@PostConstruct
	public void init() {
		initializeInjection();
		resetCriteria();
		loadGroupMicroHealthDTO();
	}

	public void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
	}

	public void resetCriteria() {
		criteria = new GroupMicroHealthCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		criteria.setProposalNo("");
		loadGroupMicroHealthDTO();
	}

	public void loadGroupMicroHealthDTO() {
		List<GroupMicroHealthDTO> groupMicroHealthDTOList = new ArrayList<>();
		groupMicroHealthDTOList = groupMicroHealthService.findAllPaymentCompleteDTO(criteria);
		for (GroupMicroHealthDTO groupMicroHealthDTO : groupMicroHealthDTOList) {
			List<MedicalProposal> medicalProposalList = new ArrayList<>();
			medicalProposalList = medicalProposalService.findMedicalProposalByGroupMicroHealthId(groupMicroHealthDTO.getId());
			groupMicroHealthDTO.setMedicalProposalList(medicalProposalList);
		}
		this.groupMicroHealthDTOList = groupMicroHealthDTOList;
	}

	public String createNewMedProposal(GroupMicroHealthDTO groupMicroHealthDTO) {
		putParam("HEALTHTYPE", HealthType.MICROHEALTH);
		putParam("groupMicroHealthDTO", groupMicroHealthDTO);
		return "detailsMedProposalEntry";
	}

	public void confirmCompleteProcess(GroupMicroHealthDTO groupMicroHealthDTO) {
		if (groupMicroHealthDTO.getMedicalProposalList().size() > 0) {
			if (validateComplete(groupMicroHealthDTO)) {

				for (MedicalProposal medProposal : groupMicroHealthDTO.getMedicalProposalList()) {
					WorkFlowDTO workFlowDTO = new WorkFlowDTO(medProposal.getId(), null, WorkflowTask.ISSUING, WorkflowReferenceType.MICRO_HEALTH_PROPOSAL, user, user);
					medicalProposalService.createGroupMicroHealtPolicy(medProposal, workFlowDTO);
				}
				groupMicroHealthDTO.setShowIssueButton(true);
			} else {
				addErrorMessage("detailsForm" + ":groupMicroHealthTable", MessageId.TOTAL_PREMIUM_AND_INSUREDPERSON_MUST_BE_SAME);
			}
		}

	}

	private boolean validateComplete(GroupMicroHealthDTO groupMicroHealthDTO) {
		double totalProposalPremium = 0.0;
		int totalProposalInsuredPerson = 0;
		for (MedicalProposal medProposal : groupMicroHealthDTO.getMedicalProposalList()) {
			totalProposalPremium += medProposal.getTermPremium();
			totalProposalInsuredPerson += medProposal.getMedicalProposalInsuredPersonList().size();
		}

		if (totalProposalPremium == groupMicroHealthDTO.getTotalPremium() && totalProposalInsuredPerson == groupMicroHealthDTO.getNumberOfInsuredPerson()) {
			return true;
		} else {
			return false;
		}

	}

	public String prepareEditMicroHealthProposal(MedicalProposal medicalProposal, GroupMicroHealthDTO groupMicroHealthDTO) {
		outjectLifeProposal(medicalProposal);
		putParam("groupMicroHealthDTO", groupMicroHealthDTO);
		putParam("HEALTHTYPE", HealthType.MICROHEALTH);
		return "detailsMedProposalEntry";

	}

	private void outjectLifeProposal(MedicalProposal medicalProposal) {
		putParam("editMedicalProposal", medicalProposal);
	}

	public void generatePolicyIssue(MedicalProposal medicalProposal) {
		MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyByProposalId(medicalProposal.getId());
		MedicalUnderwritingDocFactory.generateMedicalPolicyIssue(medicalPolicy, null, dirPath, fileName, HealthType.MICROHEALTH, Language.MYANMAR);
		PrimeFaces.current().executeScript("PF('pdfDialog').show()");
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(getSystemPath() + "/pdf-report/Medical");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public List<GroupMicroHealthDTO> getGroupMicroHealthDTOList() {
		return groupMicroHealthDTOList;
	}

	public GroupMicroHealthCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(GroupMicroHealthCriteria criteria) {
		this.criteria = criteria;
	}

}
