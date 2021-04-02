package org.ace.insurance.report.life;

import java.util.Date;
import java.util.List;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.LifeProductType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.snakebite.SnakeBiteBeneficiary;
import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.payment.Payment;

public class SnakeBitePolicyMonthlyReport implements ISorter{

	private static final long serialVersionUID = 1L;
	
	private float commission;
	private double sumInsured;
	private double premium;
	private String netpremium;
	private String policyId;
	private String customerName;
	private String policyNo;
	private String nrc;
	private String address;
	private String beneficialName;
	private String beneficialNrc;
	private String serviceCharges;
	private String beneficialaddress;
	private String agentCode;
	private String receiptNo;
	private String product;
	private String remark;
	private String paymentDate;
	private Date policyStartDate;
	private List<SnakeBiteBeneficiary> beneList;

	public SnakeBitePolicyMonthlyReport() {
	}

	public SnakeBitePolicyMonthlyReport(SnakeBitePolicy snakeBitePolicy,Payment p) {
		this.policyId = snakeBitePolicy.getId();
		this.customerName = snakeBitePolicy.getCustomer().getFullName();
		this.policyNo = snakeBitePolicy.getSnakeBitePolicyNo();
		this.nrc = snakeBitePolicy.getCustomer().getIdNo();
		this.address = snakeBitePolicy.getCustomer().getFullAddress();
		this.beneList = snakeBitePolicy.getSnakeBiteBeneficiaryList();
		this.sumInsured = snakeBitePolicy.getSumInsured();
		this.commission = snakeBitePolicy.getProduct().getFirstCommission();
		/*
		 * this.beneficialName = sb.getFullName(); this.beneficialNrc =
		 * sb.getIdNo(); this.beneficialaddress = sb.getFullAddress();
		 */
		this.serviceCharges =  "-";
		this.remark = " ";
		//prepare
		this.receiptNo = snakeBitePolicy.getReferenceNo();
		this.premium = snakeBitePolicy.getPremium();
		
		if(p.getPaymentDate() != null){
			this.paymentDate = Utils.getDateFormatString(p.getPaymentDate());
			this.netpremium = Utils.getCurrencyFormatString(p.getBasicPremium());
		}else {
			this.paymentDate = "-";
			this.netpremium = "-";
		}if(snakeBitePolicy.getAgent() != null) {
			this.agentCode = snakeBitePolicy.getAgent().getCodeNo();
		}else {
			this.agentCode = "-";
		}
		
	}

	public SnakeBitePolicyMonthlyReport(SnakeBitePolicy snakeBitePolicy,
			LifeProductType product, String receiptNo) {
		this.policyId = snakeBitePolicy.getId();
		this.address = snakeBitePolicy.getCustomer().getFullAddress();
		this.policyNo = snakeBitePolicy.getSnakeBitePolicyNo();
		this.sumInsured = snakeBitePolicy.getSumInsured();
		this.premium = snakeBitePolicy.getPremiumOfExcludeAgentCommission();
		this.receiptNo = snakeBitePolicy.getReferenceNo();// something ..!
		this.commission = snakeBitePolicy.getProduct().getFirstCommission();

		this.policyStartDate =snakeBitePolicy.getPolicyStartDate();

		if (snakeBitePolicy.getCustomer() != null) {
			this.customerName = snakeBitePolicy.getCustomer().getFullName();
		}

		if (snakeBitePolicy.getAgent() != null) {
			this.agentCode = snakeBitePolicy.getAgent().getCodeNo();
		}

		if (product == LifeProductType.SNAKE_BITE) {
			this.product = snakeBitePolicy.getProduct().getId();// lifePolicy.getPolicyInsuredPersonList().size();
		}

	}	

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public Date getPolicyStartDate() {
		return policyStartDate;
	}

	public void setPolicyStartDate(Date policyStartDate) {
		this.policyStartDate = policyStartDate;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}


	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}


	@Override
	public String getRegistrationNo() {
		return policyNo;
	}

	public String getNrc() {
		return nrc;
	}

	public void setNrc(String nRc) {
		nrc = nRc;
	}

	public String getServiceCharges() {
		return serviceCharges;
	}

	public void setServiceCharges(String serviceCharges) {
		this.serviceCharges = serviceCharges;
	}

	public String getBeneficialaddress() {
		return beneficialaddress;
	}

	public void setBeneficialaddress(String beneficialaddress) {
		this.beneficialaddress = beneficialaddress;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getBeneficialName() {
		return beneficialName;
	}

	public void setBeneficialName(String beneficialName) {
		this.beneficialName = beneficialName;
	}

	public String getBeneficialNrc() {
		return beneficialNrc;
	}

	public void setBeneficialNrc(String beneficialNrc) {
		this.beneficialNrc = beneficialNrc;
	}

	public String getNetpremium() {
		return netpremium;
	}

	public void setNetpremium(String netpremium) {
		this.netpremium = netpremium;
	}

	public List<SnakeBiteBeneficiary> getBeneList() {
		return beneList;
	}

	public void setBeneList(List<SnakeBiteBeneficiary> beneList) {
		this.beneList = beneList;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public float getCommission() {
		return commission;
	}

	public void setCommission(float commission) {
		this.commission = commission;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	
}
