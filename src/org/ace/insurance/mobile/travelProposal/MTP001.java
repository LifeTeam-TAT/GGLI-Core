package org.ace.insurance.mobile.travelProposal;

import java.util.List;

import org.ace.insurance.mobile.enums.ProposalStatus;
import org.ace.insurance.mobile.enums.ResponseStatus;

public class MTP001 {

	private String id;
	private String userId;
	private String productId;
	private String proposalNo;
	private String policyNo;
	private String transactionId;
	private String submittedDate;
	private ProposalStatus proposalStatus;
	private List<MIP001> insuredPersonList;
	private ResponseStatus responseStatus;

	public MTP001() {

	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getTransactioId() {
		return transactionId;
	}

	public void setTransactioId(String transactioId) {
		this.transactionId = transactioId;
	}

	/**
	 * @return the proposalNo
	 */
	public String getProposalNo() {
		return proposalNo;
	}

	/**
	 * @param proposalNo
	 *            the proposalNo to set
	 */
	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	/**
	 * @return the submittedDate
	 */
	public String getSubmittedDate() {
		return submittedDate;
	}

	/**
	 * @param submittedDate
	 *            the submittedDate to set
	 */
	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}

	/**
	 * @return the proposalStatus
	 */
	public ProposalStatus getProposalStatus() {
		return proposalStatus;
	}

	/**
	 * @param proposalStatus
	 *            the proposalStatus to set
	 */
	public void setProposalStatus(ProposalStatus proposalStatus) {
		this.proposalStatus = proposalStatus;
	}

	/**
	 * @return the inuredPersonList
	 */
	public List<MIP001> getInsuredPersonList() {
		return insuredPersonList;
	}

	/**
	 * @param inuredPersonList
	 *            the inuredPersonList to set
	 */
	public void setInsuredPersonList(List<MIP001> insuredPersonList) {
		this.insuredPersonList = insuredPersonList;
	}

	public ResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(ResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}
