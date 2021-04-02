package org.ace.insurance.web.manage.enquires.life;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.LifePolicyAttachment;
import org.ace.insurance.life.policy.PolicyInsuredPersonAttachment;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.LifeProposalAttachment;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.web.util.FileHandler;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "LifePolicyAttachmentActionBean")
public class LifePolicyAttachmentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	/* Attachment */
	private final String DIR = "/upload/life-proposal/";
	private String temporyDir;
	private String lifeProposalId;
	private Map<String, String> policyUploadedFileMap;
	private Map<String, String> medicalCheckUpUploadedFileMap;
	private Map<String, String> personAttchmentMap;
	private ProposalInsuredPerson proposalInsuredPerson;
	private BancaassuranceProposal bancaassuranceProposal;
	private LifePolicy lifePolicy;
	private boolean showEntry;

	@PostConstruct
	public void init() {
		lifePolicy = (LifePolicy) getParam("lifePolicy");
		lifeProposalId = lifePolicy.getLifeProposal().getId();
		proposalInsuredPerson = lifePolicy.getLifeProposal().getProposalInsuredPersonList().get(0);
		temporyDir = "/temp/" + System.currentTimeMillis() + "/";
		createNewAttMap();
		changedTempFile();
		saveIntoTempFolder();
	}

	private void createNewAttMap() {
		policyUploadedFileMap = new HashMap<String, String>();
		personAttchmentMap = new HashMap<String, String>();
		medicalCheckUpUploadedFileMap = new HashMap<String, String>();
	}

	private void saveIntoTempFolder() {
		if (lifePolicy.getAttachmentList().size() > 0 || lifePolicy.getCustomerMedicalCheckUpAttachmentList().size() > 0
				|| lifePolicy.getPolicyInsuredPersonList().get(0).getAttachmentList().size() > 0) {
			String srcPath = getUploadPath() + DIR + lifeProposalId;
			String destPath = getUploadPath() + temporyDir + lifeProposalId;
			try {
				FileHandler.copyDirectory(srcPath, destPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String filePath = null;
			for (LifePolicyAttachment att : lifePolicy.getAttachmentList()) {
				filePath = att.getFilePath();
				filePath = filePath.replaceAll(DIR, temporyDir);
				att.setFilePath(filePath);
				policyUploadedFileMap.put(att.getName(), att.getFilePath());
			}

			for (Attachment medicalAtt : lifePolicy.getCustomerMedicalCheckUpAttachmentList()) {
				filePath = medicalAtt.getFilePath();
				filePath = filePath.replaceAll(DIR, temporyDir);
				medicalAtt.setFilePath(filePath);
				medicalCheckUpUploadedFileMap.put(medicalAtt.getName(), medicalAtt.getFilePath());
			}

			for (PolicyInsuredPersonAttachment att : lifePolicy.getPolicyInsuredPersonList().get(0).getAttachmentList()) {
				filePath = att.getFilePath();
				filePath = filePath.replaceAll(DIR, temporyDir);
				att.setFilePath(filePath);
				personAttchmentMap.put(att.getName(), att.getFilePath());
			}
		}
	}

	// when file remove and refresh page
	private void changedTempFile() {
		List<LifePolicyAttachment> listProposalAttList = new ArrayList<LifePolicyAttachment>();
		List<Attachment> attList = new ArrayList<Attachment>();
		List<PolicyInsuredPersonAttachment> insuAttList = new ArrayList<PolicyInsuredPersonAttachment>();
		for (LifePolicyAttachment attach : lifePolicy.getAttachmentList()) {
			if (attach.getFilePath().indexOf("temp") != -1) {
				String fileName = attach.getName();
				String filePath = DIR + lifeProposalId + "/" + fileName;
				listProposalAttList.add(new LifePolicyAttachment(fileName, filePath));
			}
		}
		if (listProposalAttList.size() > 0) {
			lifePolicy.setAttachmentList(new ArrayList<LifePolicyAttachment>());
			lifePolicy.getAttachmentList().addAll(listProposalAttList);
		}
		for (Attachment attach : lifePolicy.getCustomerMedicalCheckUpAttachmentList()) {
			if (attach.getFilePath().indexOf("temp") != -1) {
				String fileName = attach.getName();
				String filePath = DIR + lifeProposalId + "/" + fileName;
				attList.add(new Attachment(fileName, filePath));
			}
		}
		if (attList.size() > 0) {
			lifePolicy.setCustomerMedicalCheckUpAttachmentList(new ArrayList<Attachment>());
			lifePolicy.getCustomerMedicalCheckUpAttachmentList().addAll(attList);
		}
		for (PolicyInsuredPersonAttachment attach : lifePolicy.getPolicyInsuredPersonList().get(0).getAttachmentList()) {
			if (attach.getFilePath().indexOf("temp") != -1) {
				String fileName = attach.getName();
				String filePath = DIR + lifeProposalId + "/" + proposalInsuredPerson.getId() + "/" + fileName;
				insuAttList.add(new PolicyInsuredPersonAttachment(fileName, filePath));
			}
		}
		if (insuAttList.size() > 0) {
			lifePolicy.getPolicyInsuredPersonList().get(0).setAttachmentList(new ArrayList<PolicyInsuredPersonAttachment>());
			lifePolicy.getPolicyInsuredPersonList().get(0).getAttachmentList().addAll(insuAttList);
		}
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifePolicy");
	}

	public void handlePolicyAttachment(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");
		String filePath = temporyDir + lifeProposalId + "/" + fileName;
		policyUploadedFileMap.put(fileName, filePath);
		createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
	}

	public void handleMedicalCheckUpAttachment(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");
		String filePath = temporyDir + lifeProposalId + "/" + fileName;
		medicalCheckUpUploadedFileMap.put(fileName, filePath);
		createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
	}

	public void handleInsurePersonAttachment(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");
		String filePath = temporyDir + lifeProposalId + "/" + proposalInsuredPerson.getId() + "/" + fileName;
		personAttchmentMap.put(fileName, filePath);
		createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
	}

	public List<String> getPolicyAttachmentList() {
		return new ArrayList<String>(policyUploadedFileMap.values());
	}

	public List<String> getCustomerMedicalAttachmentList() {
		return new ArrayList<String>(medicalCheckUpUploadedFileMap.values());
	}

	public List<String> getPersonUploadedFileList() {
		return new ArrayList<String>(personAttchmentMap.values());
	}

	public void removePolicyUploadedFile(String filePath) {
		try {
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
			String fileName = getFileName(filePath);
			policyUploadedFileMap.remove(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeMedicalCheckUpUploadedFile(String filePath) {
		try {
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
			String fileName = getFileName(filePath);
			medicalCheckUpUploadedFileMap.remove(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeInsuPersonUploadedFile(String filePath) {
		try {
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
			String fileName = getFileName(filePath);
			personAttchmentMap.remove(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isEmptyAtt() {
		if (personAttchmentMap == null || personAttchmentMap.isEmpty()) {
			return true;
		}
		return false;
	}

	public void preparePersonAttachment() {
		showEntry = true;
	}

	public String uploadAttachment() {
		String result = null;
		try {
			loadAttachment();
			lifeProposalService.updateLifeProposal(lifePolicy.getLifeProposal());
			lifePolicyService.updatePolicyAttachment(this.lifePolicy);
			result = "lifePolicyEnquiry";
			moveUploadedFiles();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	private void loadAttachment() {
		if (lifePolicy.getAttachmentList().size() > 0) {
			lifePolicy.setAttachmentList(new ArrayList<LifePolicyAttachment>());
			lifePolicy.getLifeProposal().setAttachmentList(new ArrayList<LifeProposalAttachment>());
		}
		if (lifePolicy.getPolicyInsuredPersonList().get(0).getAttachmentList().size() > 0) {
			lifePolicy.getPolicyInsuredPersonList().get(0).setAttachmentList(new ArrayList<PolicyInsuredPersonAttachment>());
			lifePolicy.getLifeProposal().getProposalInsuredPersonList().get(0).setAttachmentList(new ArrayList<InsuredPersonAttachment>());
		}
		if (lifePolicy.getCustomerMedicalCheckUpAttachmentList().size() > 0) {
			lifePolicy.setCustomerMedicalCheckUpAttachmentList(new ArrayList<Attachment>());
			lifePolicy.getLifeProposal().setCustomerMedicalCheckUpAttachmentList(new ArrayList<Attachment>());
		}

		for (String fileName : policyUploadedFileMap.keySet()) {
			String filePath = DIR + lifeProposalId + "/" + fileName;
			lifePolicy.addLifePolicyAttachment(new LifePolicyAttachment(fileName, filePath));
			lifePolicy.getLifeProposal().addAttachment(new LifeProposalAttachment(fileName, filePath));
		}
		for (String fileName : personAttchmentMap.keySet()) {
			String filePath = DIR + lifeProposalId + "/" + proposalInsuredPerson.getId() + "/" + fileName;
			lifePolicy.getPolicyInsuredPersonList().get(0).addAttachment(new PolicyInsuredPersonAttachment(fileName, filePath));
			lifePolicy.getLifeProposal().getProposalInsuredPersonList().get(0).addAttachment(new InsuredPersonAttachment(fileName, filePath));
		}
		for (String fileName : medicalCheckUpUploadedFileMap.keySet()) {
			String filePath = DIR + lifeProposalId + "/" + fileName;
			lifePolicy.addCustomerMedicalChkUpAttachment(new Attachment(fileName, filePath));
			lifePolicy.getLifeProposal().addCustomerMedicalChkUpAttachment(new Attachment(fileName, filePath));
		}
	}

	private void moveUploadedFiles() {
		try {
			FileHandler.moveFiles(getUploadPath(), temporyDir + lifeProposalId, DIR + lifeProposalId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void openTemplateDialog() {
		putParam("lifePolicy", lifePolicy);
		putParam("workFlowList", getWorkFlowList(lifePolicy.getLifeProposal().getId()));
		openNewLifePolicyInfoTemplate();
	}

	public List<WorkFlowHistory> getWorkFlowList(String proposalId) {
		return workFlowService.findWorkFlowHistoryByRefNo(lifePolicy.getLifeProposal().getId());
	}

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public void setLifePolicy(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
	}

	public boolean isShowEntry() {
		return showEntry;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public void setBancaassuranceProposal(BancaassuranceProposal bancaassuranceProposal) {
		this.bancaassuranceProposal = bancaassuranceProposal;
	}

}
