/**
 * 
 */
package org.ace.insurance.coinsurance.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.coinsurance.CoinsuranceAttachment;
import org.ace.insurance.coinsurance.CoinsuranceToOtherCompanyCriteria;
import org.ace.insurance.coinsurance.persistence.interfaces.ICoinsuranceDAO;
import org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService;
import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.interfaces.IInsuredItem;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuredProductGroup;
import org.ace.insurance.system.common.coinsurancecompany.service.interfaces.ICoinsuranceCompanyService;
import org.ace.insurance.web.util.FileHandler;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

/**
 * This serves as the implementation of the {@link ICoinsuranceService} to
 * manipulate the Co-insurance object.
 * 
 * @author ACN
 * @version 1.0.0
 * @Date 2013/05/07
 */
@Service(value = "CoinsuranceService")
public class CoinsuranceService extends BaseService implements ICoinsuranceService {

	@Resource(name = "CoinsuranceDAO")
	private ICoinsuranceDAO coinsuranceDAO;

	@Resource(name = "CoinsuranceCompanyService")
	private ICoinsuranceCompanyService coinsuranceCompanyService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	private void setPrefixForInsert(Coinsurance coinsurance) {
		String prefix = getPrefix(Coinsurance.class);
		coinsurance.setPrefix(prefix);

	}

	/**
	 * @see org.ace.insurance.coinsurance.service.interfaces.
	 *      ICoinsuranceService# add(org.ace.insurance.coinsurance.Coinsurance,
	 *      , org.ace.insurance.common.interfaces.IPolicy)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Coinsurance addNewCoinsurance(Coinsurance coinsurance, IPolicy policy, WorkFlowDTO workFlowDTO) {
		setPrefixForInsert(coinsurance);
		String coinsuranceNo = null;
		try {
			coinsuranceNo = customIDGenerator.getNextId(SystemConstants.COINSURANCE_NO, null, true);
			coinsurance.setCoinsuranceNo(coinsuranceNo);
			coinsurance = coinsuranceDAO.insert(coinsurance, policy);
			workFlowDTO.setReferenceNo(coinsurance.getId());
			workFlowDTOService.addNewWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Co-insurance (ID : " + coinsurance.getId() + ")", e);
		}
		return coinsurance;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void confirmCoinsurance(Coinsurance coinsurance) {
		try {
			String receiptNo = customIDGenerator.getNextId(SystemConstants.COINSURANCE_INVOICE_NO, null, true);
			coinsurance.setInvoiceNo(receiptNo);
			coinsurance.setInvoiceDate(new Date());
			coinsuranceDAO.update(coinsurance);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm Co-insurance", e);
		}
	}

	/**
	 * @see org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService#update(org.ace.insurance.coinsurance.Coinsurance)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Coinsurance coinsurance) {
		try {
			coinsuranceDAO.update(coinsurance);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update Co-insurance", e);
		}
	}

	/**
	 * @see org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService#delete(org.ace.insurance.coinsurance.Coinsurance)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Coinsurance coinsurance) {
		try {
			coinsuranceDAO.delete(coinsurance);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete Co-insurance", e);
		}
	}

	/**
	 * @see org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService#findById(java.lang.String)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Coinsurance findById(String id) {
		Coinsurance ret;
		try {
			ret = coinsuranceDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to retrieve the Co-insurance (ID : " + id + ")", e);
		}
		return ret;
	}

	/**
	 * @see org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService#findAll()
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findAll() {
		List<Coinsurance> ret = null;
		try {
			ret = coinsuranceDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to retrieve all Co-insurances", e);
		}
		return ret;
	}

	/**
	 * @see org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService#addNewCoinsurances(org.ace.insurance.coinsurance.Coinsurance)
	 */
	public List<Coinsurance> addNewCoinsurances(IPolicy policy, double agentCommission) {
		List<Coinsurance> coinsuranceList = new ArrayList<Coinsurance>();
		// TODO FIX
		/*
		 * FirePolicy firePolicy = null; if (policy instanceof FirePolicy) {
		 * firePolicy = (FirePolicy) policy; }
		 */

		double commissionPercent = 0;
		double premiumPercent = 0.0;
		ProductGroup productGroup = policy.getProductGroup();
		if (productGroup != null) {
			List<CoinsuredProductGroup> groups = coinsuranceCompanyService.findCoinsuredProductGroupsByProductGroupId(productGroup.getId());
			if (groups != null && !groups.isEmpty()) {
				// TODO FIX
				double policyTotalPremium = 0.0; // policy.getTotalTermPremium()
													// -
													// firePolicy.getTotalNcbPremium();
				int paymentTerm = retrieveMaxPolicyTerm(policy.getInsuredItems());
				double policyTotalSumInsured = policy.getTotalSumInsured();
				double netPremium = policyTotalPremium - agentCommission;

				premiumPercent = Utils.getTwoDecimalPercent(policy.getProductGroup().getMaxSumInsured(), policyTotalSumInsured);

				netPremium = netPremium - (netPremium * premiumPercent / 100);

				for (CoinsuredProductGroup group : groups) {
					CoinsuranceCompany company = group.getCoinsuranceCompany();
					double percentage = group.getPrecentage();
					commissionPercent = group.getCommissionPercent();
					double diffSumInsured = policyTotalSumInsured - group.getProductGroup().getMaxSumInsured();
					double resultSumInsured = diffSumInsured * convertPercent(percentage);
					double resultPremium = netPremium * convertPercent(percentage);
					Coinsurance coinsurance = new Coinsurance(policy.getPolicyNo(), paymentTerm, new Date(), resultSumInsured, resultPremium, policy.getInsuranceType(),
							CoinsuranceType.OUT, company, percentage, commissionPercent, policy.getBranch());
					String prefix = getPrefix(Coinsurance.class);
					coinsurance.setPrefix(prefix);
					// TODO FIX
					// coinsurance.setCoinsuranceFirePolicy(new
					// CoinsuranceFirePolicy(firePolicy.getPropertyLocation()));
					try {
						coinsuranceDAO.insert(coinsurance, policy);
						coinsuranceList.add(coinsurance);
					} catch (DAOException e) {
						throw new SystemException(e.getErrorCode(), "Faield to add a new Co-insurance (ID : " + coinsurance.getId() + ")", e);
					}
				}
			}
		}
		return coinsuranceList;
	}

	public void calculateHomeAmount(Coinsurance coinsurance) {
		double rate = 1.0;
		if (coinsurance.getCoinsuranceType().equals(CoinsuranceType.IN)) {
			rate = coinsurance.getRate();
		} else {
			if (CurrencyUtils.isUSD(coinsurance.getCurrency())) {
				rate = paymentDAO.findActiveRate();
			}
		}

		coinsurance.setRate(rate);
		coinsurance.setHomeNetPremium(rate * coinsurance.getNetPremium());
		coinsurance.setHomePremium(rate * coinsurance.getPremium());
		coinsurance.setHomeReceivedSumInsured(rate * coinsurance.getHomeReceivedSumInsured());
		coinsurance.setHomeSumInsured(rate * coinsurance.getSumInsuranced());
		coinsurance.setHomeNetPremium(rate * coinsurance.getNetPremium());
	}

	/**
	 * @see org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService#addNewCoinsurances(org.ace.insurance.coinsurance.Coinsurance)
	 */
	public List<Coinsurance> addNewCargoCoinsurances(IPolicy policy, double agentCommission) {
		List<Coinsurance> coinsuranceList = new ArrayList<Coinsurance>();
		double commissionPercent = 0;
		ProductGroup productGroup = policy.getProductGroup();
		if (productGroup != null) {
			List<CoinsuredProductGroup> groups = coinsuranceCompanyService.findCoinsuredProductGroupsByProductGroupId(productGroup.getId());
			if (groups != null && !groups.isEmpty()) {
				double policyTotalPremium = policy.getTotalTermPremium();
				int paymentTerm = retrieveMaxPolicyTerm(policy.getInsuredItems());
				double netPremium = policyTotalPremium - agentCommission;
				for (CoinsuredProductGroup group : groups) {
					CoinsuranceCompany company = group.getCoinsuranceCompany();
					double percentage = group.getPrecentage();
					commissionPercent = group.getCommissionPercent();
					double resultSumInsured = policy.getTotalSumInsured() * convertPercent(percentage);
					double resultPremium = netPremium * convertPercent(percentage);
					Coinsurance coinsurance = new Coinsurance(policy.getPolicyNo(), paymentTerm, new Date(), resultSumInsured, resultPremium, policy.getInsuranceType(),
							CoinsuranceType.OUT, company, percentage, commissionPercent, policy.getBranch());
					String prefix = getPrefix(Coinsurance.class);
					coinsurance.setPrefix(prefix);
					try {
						coinsuranceDAO.insert(coinsurance, policy);
						coinsuranceList.add(coinsurance);
					} catch (DAOException e) {
						throw new SystemException(e.getErrorCode(), "Faield to add a new Co-insurance (ID : " + coinsurance.getId() + ")", e);
					}
				}
			}
		}
		return coinsuranceList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addCoinsuranceAttachment(Coinsurance coinsurance) {
		try {
			String prefix = getPrefix(CoinsuranceAttachment.class);
			for (CoinsuranceAttachment att : coinsurance.getCoinsuranceAttachmentList()) {
				att.setPrefix(prefix);
			}
			coinsuranceDAO.addAttachment(coinsurance);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add Co-insurance Attachment", e);
		}
	}

	/**
	 * @see org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService#findByPolicyNo()
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findByPolicyNo(String policyNo) {
		List<Coinsurance> ret = null;
		try {
			ret = coinsuranceDAO.findByPolicyNO(policyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to retrieve all Co-insurances For Invoice", e);
		}
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findByCargoPolicyNo(String policyNo) {
		List<Coinsurance> ret = null;
		try {
			ret = coinsuranceDAO.findByCargoPolicyNo(policyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to retrieve all Co-insurances For Invoice", e);
		}
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Coinsurance findByPolicyByOutPolicyNo(String policyNo) {
		Coinsurance ret = null;
		try {
			ret = coinsuranceDAO.findByPolicyByOutPolicyNo(policyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to retrieve all Co-insurances For Invoice", e);
		}
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Coinsurance findINPolicyNo(String policyNo) {
		Coinsurance ret = null;
		try {
			ret = coinsuranceDAO.findINPolicyNo(policyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to retrieve all Co-insurances For Invoice", e);
		}
		return ret;
	}

	// This method seeks(divides and conquers) the maximum value of the payment
	// terms from the given insured item
	private int retrieveMaxPolicyTerm(List<IInsuredItem> insuredItems) {
		int paymentTerm = 0;
		if (insuredItems != null) {
			for (IInsuredItem insuredItem : insuredItems) {
				paymentTerm = Math.max(paymentTerm, insuredItem.getPaymentTerm());
			}

		}
		return paymentTerm;
	}

	// The method converts the given percent value (such as 20%, 50%, 80%) into
	// the actual percentage values (such as 0.20, 0.50, 0.80).
	// The conversion formula is
	// percentage = percent / 100
	// For example:
	// /0.20 = 20 / 100
	private double convertPercent(double percentage) {
		if (percentage < 0) {
			return 1.0;
		}
		double percent = 0.0;
		percent = (double) percentage / 100;
		return percent;
	}

	// for OutCoinsurance Commission Payment
	@Override
	public void generateOutCoinsuranceCommission(List<Coinsurance> coinsuranceList, String dirPath, String fileName, String premiumtype, String insurerName, Branch branch) {
		try {
			List jasperPrintList = new ArrayList();
			String currencyCode = CurrencyUtils.getCurrencyCode(null);
			for (Coinsurance c : coinsuranceList) {
				String coaName;
				String coaName2;
				String coaName3;
				String coaName4;
				TLF tlfForCoInsuCommDebit;
				TLF tlfForCoInsuCommCredit;
				TLF tlfForCoInsuPayCr;
				TLF tlfForCoInsuPayDr;
				double commission = 0.0;
				double premium = 0.0;
				String paymentCcoaCode1 = null;
				String paymentCcoaCode2 = null;
				String paymentCcoaCode3 = null;
				String paymentCcoaCode4 = null;
				commission = org.ace.insurance.common.Utils.getPercentOf(c.getCommissionPercent(), c.getPremium());
				premium = c.getPremium() - commission;
				if (c.getInsuranceType() != null && c.getInsuranceType().equals(InsuranceType.LIFE)) {
					paymentCcoaCode1 = paymentService.findCheckOfAccountCode(COACode.LIFE_SUNDRY, branch, currencyCode);
				}

				// type 1
				tlfForCoInsuPayCr = new TLF();
				tlfForCoInsuPayCr.setCoaId(paymentCcoaCode1);// coa code 1
				tlfForCoInsuPayCr.setHomeAmount(premium);

				if (c.getPaymentChannel() != null) {
					if (c.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
						// paymentCcoaCode2 =
						// paymentService.findCheckOfAccountCode("CHEQUE",
						// branch);
						paymentCcoaCode2 = c.getCompanybank().getAcode();
					} else {
						paymentCcoaCode2 = paymentService.findCheckOfAccountCode("CASH", branch, currencyCode);
					}
				}

				tlfForCoInsuPayDr = new TLF();
				tlfForCoInsuPayDr.setCoaId(paymentCcoaCode2);// coa code 2
				tlfForCoInsuPayDr.setHomeAmount(premium);

				// type 3 CoInsuCommReceived_Debit
				if (c.getInsuranceType() != null && c.getInsuranceType().equals(InsuranceType.LIFE)) {
					paymentCcoaCode3 = paymentService.findCheckOfAccountCode(COACode.LIFE_SUNDRY, branch, currencyCode);
				}

				// type 3
				tlfForCoInsuCommDebit = new TLF();
				tlfForCoInsuCommDebit.setCoaId(paymentCcoaCode3);// coa code 3
				tlfForCoInsuCommDebit.setHomeAmount(commission);

				// type 4 CoInsuCommReceived_Credit
				if (c.getInsuranceType() != null && c.getInsuranceType().equals(InsuranceType.LIFE)) {
					paymentCcoaCode4 = paymentService.findCheckOfAccountCode(COACode.LIFE_COINSURANCE_COMMISSION, branch, currencyCode);
				}

				// type 4
				tlfForCoInsuCommCredit = new TLF();
				tlfForCoInsuCommCredit.setCoaId(paymentCcoaCode4);// coa code 4
				tlfForCoInsuCommCredit.setHomeAmount(commission);
				// coa name1
				coaName = paymentService.findCOAAccountNameByCode(tlfForCoInsuPayCr.getCoaId());
				// coa name2
				coaName2 = paymentService.findCOAAccountNameByCode(tlfForCoInsuPayDr.getCoaId());
				// coa name3
				coaName3 = paymentService.findCOAAccountNameByCode(tlfForCoInsuCommDebit.getCoaId());
				// coa name4
				coaName4 = paymentService.findCOAAccountNameByCode(tlfForCoInsuCommCredit.getCoaId());

				Map paramMap = new HashMap();
				paramMap.put("invoiceno", c.getInvoiceNo());
				paramMap.put("accountCode", tlfForCoInsuPayCr.getCoaId());
				paramMap.put("accountCode2", tlfForCoInsuPayDr.getCoaId());
				paramMap.put("accountCode3", tlfForCoInsuCommDebit.getCoaId());
				paramMap.put("accountCode4", tlfForCoInsuCommCredit.getCoaId());
				paramMap.put("accountName", coaName);
				paramMap.put("accountName2", coaName2);
				paramMap.put("accountName3", coaName3);
				paramMap.put("accountName4", coaName4);
				paramMap.put("premium", premiumtype);
				paramMap.put("sumInsured", c.getSumInsuranced());
				paramMap.put("insuredName", insurerName);
				paramMap.put("amount", tlfForCoInsuPayCr.getHomeAmount());
				paramMap.put("amount2", tlfForCoInsuCommDebit.getHomeAmount());
				paramMap.put("companyName", c.getCoinsuranceCompany().getName());

				InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/coinsurance/coinsurance.jrxml");
				JasperReport jreport = JasperCompileManager.compileReport(inputStream);
				JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
				jasperPrintList.add(jprint);
			}

			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			FileHandler.forceMakeDirectory(dirPath);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream(dirPath + fileName));
			exporter.exportReport();

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate CoinsuranceCommission", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findFireCoinsuranceByCriteria(CoinsuranceToOtherCompanyCriteria criteria) {
		List<Coinsurance> result = null;
		try {
			result = coinsuranceDAO.findFireCoinsuranceByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a Coinsurance (ID : " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findCargoCoinsuranceByCriteria(CoinsuranceToOtherCompanyCriteria criteria) {
		List<Coinsurance> result = null;
		try {
			result = coinsuranceDAO.findCargoCoinsuranceByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a Coinsurance (ID : " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findMotorCoinsuranceByCriteria(CoinsuranceToOtherCompanyCriteria criteria) {
		List<Coinsurance> result = null;
		try {
			result = coinsuranceDAO.findMotorCoinsuranceByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a Coinsurance (ID : " + e);
		}
		return result;
	}

	private class COACode {
		public static final String MOTOR_SUNDRY = "MOTOR_SUNDRY";
		public static final String MOTOR_COINSURANCE_COMMISSION = "MOTOR_COINSURANCE_COMMISSION";
		public static final String FIRE_SUNDRY = "FIRE_SUNDRY";
		public static final String FIRE_COINSURANCE_COMMISSION = "FIRE_COINSURANCE_COMMISSION";
		public static final String LIFE_SUNDRY = "LIFE_SUNDRY";
		public static final String LIFE_COINSURANCE_COMMISSION = "LIFE_COINSURANCE_COMMISSION";
	}
}
