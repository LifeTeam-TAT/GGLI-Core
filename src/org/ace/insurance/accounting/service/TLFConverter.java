package org.ace.insurance.accounting.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.ace.insurance.accounting.BatchTranCode;
import org.ace.insurance.accounting.CSCAcname;
import org.ace.insurance.accounting.CSCImportConstants;
import org.ace.insurance.accounting.CalculationMethod;
import org.ace.insurance.accounting.CoReStatus;
import org.ace.insurance.accounting.GLSign;
import org.ace.insurance.accounting.InterfaceFile;
import org.ace.insurance.accounting.PairedKey;
import org.ace.insurance.accounting.TLFConversionFormat;
import org.ace.insurance.accounting.UniqueKey;
import org.ace.insurance.accounting.service.interfaces.IInterfaceFileService;
import org.ace.insurance.accounting.service.interfaces.ITLFConversionFormatService;
import org.ace.insurance.accounting.service.interfaces.ITLFConverter;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.Utils;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.payment.constant.Status;
import org.ace.insurance.payment.constant.TranCode;
import org.ace.insurance.payment.enums.DoubleEntry;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.payment.service.interfaces.ITLFService;
import org.ace.insurance.system.common.agent.COACode;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.web.common.MessageId;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("TLFConverter")
public class TLFConverter implements ITLFConverter {
	private Logger logger = Logger.getLogger(this.getClass());

	@Resource(name = "CSC_IMPORT_CONSTANTS")
	private Properties properties;

	@Resource(name = "CSC_NARRATION")
	private Properties narrationProperties;

	@Resource(name = "BranchService")
	private IBranchService branchService;

	@Resource(name = "CurrencyService")
	private ICurrencyService currencyService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "InterfaceFileService")
	private IInterfaceFileService interfaceFileService;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "TLFConversionFormatService")
	private ITLFConversionFormatService tlfConversionFormatService;

	@Resource(name = "BankService")
	private IBankService bankService;

	@Resource(name = "TLFService")
	private ITLFService tlfService;

	private List<String> cashCodeList;
	private List<String> bankCodeList;
	private List<String> paymentCodeList;
	private Integer lastNChar;
	private int loopIndex = 0;

	// recursive , ignore skip for converting interface files with skip true ,
	// converterDto to assign converted TLFs in recursive
	public void convertToTLF(InterfaceFile interfaceFile, boolean ignoreSkip, String eNo) throws SystemException, Exception {
		try {
			loopIndex++;
			logger.info(" Loop #" + loopIndex + " on #" + interfaceFile.getId() + " - " + interfaceFile.getUniqueKey().asTLFNO() + " - "
					+ interfaceFile.getUniqueKey().getSacscode() + " - " + interfaceFile.getUniqueKey().getSacstype());
			if (interfaceFile != null) {
				if (cashCodeList == null) {
					String cashCodes = properties.getProperty(CSCImportConstants.CSC_CASH_GLCODE_INDENTIFIER);
					cashCodeList = Arrays.asList(cashCodes.split("\\s*,\\s*"));
				}

				if (bankCodeList == null) {
					String bankCodes = properties.getProperty(CSCImportConstants.CSC_BANK_GLCODE_INDENTIFIER);
					bankCodeList = Arrays.asList(bankCodes.split("\\s*,\\s*"));
				}

				if (paymentCodeList == null) {
					String paymentCodes = properties.getProperty(CSCImportConstants.CSC_PAYMENT_INDENTIFIER);
					paymentCodeList = Arrays.asList(paymentCodes.split("\\s*,\\s*"));
				}

				if (lastNChar == null) {
					lastNChar = Integer.parseInt(properties.getProperty(CSCImportConstants.CSC_BANK_CODE_LAST_N_INDEX));
				}

				// find if the TLF conversion format is for coinsurance,
				// reinsurance or normal
				CoReStatus coReStatus = getCoReStatus(interfaceFile);

				// then find the tlf conversion format
				TLFConversionFormat conversionFormat = tlfConversionFormatService.findTlfConversionFormat(interfaceFile.getUniqueKey(), interfaceFile.getCnttype(), coReStatus);

				// only convert to tlf if
				if (ignoreSkip || !conversionFormat.isSkip()) {
					handleConversion(interfaceFile, conversionFormat, eNo);
					// switch (conversionFormat.getConversionType()) {
					// case DUAL:
					// resultDto = handleDual(interfaceFile, resultDto,
					// conversionFormat);
					// break;
					//
					// case PAYMENT:
					// resultDto = handlePayment(interfaceFile, resultDto,
					// conversionFormat);
					// break;
					// case NONE:
					// default:
					// break;
					// }
				} else {
					if (!interfaceFile.isConverted()) {
						interfaceFile.setConverted(true);
						interfaceFileService.updateInterfaceFile(interfaceFile);
					}
				}
			}
		} catch (SystemException se) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + se.getClass().getName() + " Exception in convertToTLF method ");
			throw se;
		} catch (Exception ee) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + ee.getClass().getName() + " Exception in convertToTLF method ");
			throw new SystemException(ErrorCode.SYSTEM_ERROR, ee.getMessage(), ee);
		}
	}

	private void handleConversion(InterfaceFile interfaceFile, TLFConversionFormat conversionFormat, String eNo) throws SystemException, Exception {
		try {
			Branch branch = branchService.findByCSCBranchCode(interfaceFile.getUniqueKey().getBatcbrn());

			String currencyCode = currencyService.findCurrencyByCSCCurCode(interfaceFile.getGenlcur()).getCurrencyCode();

			// to reuse eNo in recursive case (paired key cases)
			if (eNo == null) {
				eNo = customIDGenerator.getNextIdWithBranchCode(SystemConstants.CSC_ENO, interfaceFile.getCnttype(), branch, false);
			}

			String TLFNO = interfaceFile.getUniqueKey().asTLFNO();

			boolean isReverse = getReverse(interfaceFile, conversionFormat);

			for (CSCAcname acname : conversionFormat.getAcnames()) {
				GLSign other = conversionFormat.getGlSign();
				if (isReverse) {
					other = other.getReverse();
				}
				if (other.equals(acname.getGlSign()) || GLSign.ALL.equals(acname.getGlSign())) {
					createTLF(interfaceFile, conversionFormat, branch, acname, currencyCode, eNo, TLFNO, null, null, false);
				}
			}

			// because payment CN have rdocnum it will get the other interfaces
			// files with same rdocnum
			// and payment CN HAVE rldgacct,so rldgacct must be set to null coz
			// CN
			// and PP rldgacct are not same
			if (conversionFormat.getPairedKeys() != null && conversionFormat.getPairedKeys().size() > 0) {
				for (PairedKey pairedKey : conversionFormat.getPairedKeys()) {
					UniqueKey uniqueKey = createUniqueKey(interfaceFile, pairedKey);
					// because skip is marked converted at else block in
					// convertToTLF method
					List<InterfaceFile> interfaceFileList = interfaceFileService.findByCriteria(uniqueKey, interfaceFile.getImportedDate(), false);
					if (interfaceFileList != null && !interfaceFileList.isEmpty()) {
						for (InterfaceFile interfaceFile2 : interfaceFileList) {
							// RECURSIVE , might prone to errors on wrong data
							convertToTLF(interfaceFile2, true, eNo);
						}
					} else {
						if (!pairedKey.isOptional()) {
							logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : InterfaceFile not found for Paired key : " + pairedKey.getBatchTranCode() + ", "
									+ pairedKey.getSacscode() + ", " + pairedKey.getSacstype() + " for ID : " + conversionFormat.getId() + ".");
							throw new SystemException(ErrorCode.NO_INTERFACE_FILE, "InterfaceFile not found for Paired key : " + pairedKey.getBatchTranCode() + ", "
									+ pairedKey.getSacscode() + ", " + pairedKey.getSacstype() + " for ID : " + conversionFormat.getId() + ".");
						}
					}
					interfaceFileList = null;
					uniqueKey = null;
				}
			}
		} catch (SystemException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in handleConversion method ");
			throw e;
		} catch (Exception ee) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + ee.getClass().getName() + " Exception in handleConversion method ");
			throw new SystemException(ErrorCode.SYSTEM_ERROR, ee.getMessage(), ee);
		}
	}

	/*
	 * senderBranch is for interbranch , it is used to determine if tlf is
	 * interbranch and status of tlf , set null otherwise
	 */
	private void createTLF(InterfaceFile interfaceFile, TLFConversionFormat conversionFormat, Branch branch, CSCAcname acname, String currencyCode, String eNo, String TLFNO,
			Branch senderBranch, Branch recipientBranch, boolean interbranchCash) throws SystemException, Exception {
		try {
			double localAmount = handleLocalAmount(acname, interfaceFile);
			double rate = interfaceFile.getCrate();
			double homeAmount = scaleDouble(Utils.multiply(localAmount, rate));
			String narration = getNarration(homeAmount, interfaceFile.getUniqueKey());

			TLF tlf = new TLF();
			if (senderBranch != null && recipientBranch != null) {
				tlf.setBranchId(recipientBranch.getBranchCode());
			} else {
				tlf.setBranchId(branch.getBranchCode());
			}

			tlf.setCurrencyId(currencyCode);

			String acode = null;

			String errorString = "";
			// if senderBranch is not null then interbranch
			if (senderBranch != null) {
				// acname is null here
				// the reason acode is not find by senderBranch is because
				// senderbranch is only use to determine if interbranch or
				// status
				if (!acname.getAcname().equals(COACode.INTERBRANCH_SUNDRY) && !acname.getAcname().equals(properties.getProperty(CSCImportConstants.CSC_PAYMENT_ACNAME).trim())) {
					acode = paymentService.findCheckOfAccountCode(COACode.INTER_BRANCH_CSC, senderBranch, currencyCode);
					errorString = "for " + COACode.INTER_BRANCH_CSC + ", " + senderBranch.getBranchCode() + ", " + currencyCode;
				} else {
					acode = paymentService.findCheckOfAccountCode(acname.getAcname(), branch, currencyCode);
					errorString = "for " + acname.getAcname() + ", " + branch.getBranchCode() + ", " + currencyCode;
				}
			} else if (acname.getAcname().equals(COACode.PAYMENT_VARIABLE_ACNAME)) {
				boolean isBank = false;
				String glCashBankCode = Utils.getLastNCharFromString(interfaceFile.getGlcode(), lastNChar).trim();
				for (String bankCode : bankCodeList) {
					if (glCashBankCode.equals(bankCode.trim())) {
						isBank = true;
						break;
					}
				}
				if (isBank) {
					Bank bank = bankService.findByCSCBankCode(glCashBankCode);
					acode = bank.getAcode();
					errorString = " for CSC Bank Code : " + glCashBankCode + " .";
				} else {
					boolean isCash = false;
					for (String cashCode : cashCodeList) {
						if (glCashBankCode.equals(cashCode.trim())) {
							isCash = true;
							break;
						}
					}
					if (isCash) {
						// String cscCashCode =
						// Utils.getLastNCharFromString(interfaceFile.getGlcode(),
						// lastNChar
						// );
						acode = paymentService.findCheckOfAccountCode(COACode.CASH, branch, currencyCode);
						errorString = " for Cash Account : " + COACode.CASH + ", " + branch.getBranchCode() + ", " + currencyCode + " .";
					} else {
						errorString = " for GLCODE : " + interfaceFile.getGlcode() + " and acname " + acname.getAcname();
					}
				}
			} else {
				acode = paymentService.findCheckOfAccountCode(acname.getAcname(), branch, currencyCode);
				errorString = "for " + acname.getAcname() + ", " + branch.getBranchCode() + ", " + currencyCode;
			}

			// if INTER_BRANCH add two more TLFs
			if (conversionFormat.isInterbranchCheck() && senderBranch == null) {
				String glCodeBranchCode = interfaceFile.getGlcode().trim().substring(0, 2);
				String batchBranchCode = interfaceFile.getUniqueKey().getBatcbrn().trim();
				if (!glCodeBranchCode.equals(batchBranchCode)) {

					// 10 in 10L-xxx is recipient branch
					// because cash is paid at login branch
					Branch recipientTlfBranch = branchService.findByCSCBranchCode(glCodeBranchCode);
					Branch senderTlfBranch = branchService.findByCSCBranchCode(batchBranchCode);
					// recursive
					// leave acname here because it is going to be used to
					// determine
					// status of interbranch tlf ,
					// but gonna use senderBranch null or not to determine
					// acname (
					// coz acname for interbranch will always be
					// INTER_BRANCH_CSC )
					createTLF(interfaceFile, conversionFormat, branch, acname, currencyCode, eNo, TLFNO, senderTlfBranch, recipientTlfBranch, false);

					// branch here is the branch of for example FG,S and it has
					// the sender branch
					createTLF(interfaceFile, conversionFormat, branch, acname, currencyCode, eNo, TLFNO, recipientTlfBranch, senderTlfBranch, false);

					CSCAcname interMisc = new CSCAcname(acname);

					String paymentTranCodes = properties.getProperty(CSCImportConstants.CSC_PAYMENT_TRANCODE);
					List<String> paymentTranCodeList = Arrays.asList(paymentTranCodes.split("\\s*,\\s*"));

					if (paymentTranCodeList.contains(interfaceFile.getUniqueKey().getBatctrcde().trim())) {
						interMisc.setAcname(properties.getProperty(CSCImportConstants.CSC_PAYMENT_ACNAME).trim());
					} else {
						interMisc.setAcname(COACode.INTERBRANCH_SUNDRY);
					}

					// same
					interMisc.setStatus(acname.getStatus());
					createTLF(interfaceFile, conversionFormat, senderTlfBranch, interMisc, currencyCode, eNo, TLFNO, senderTlfBranch, senderTlfBranch, false);
					// reverse.. reverse is liable to become cash i.e. ccv
					interMisc.setStatus(acname.getStatus().equals(DoubleEntry.CREDIT) ? DoubleEntry.DEBIT : DoubleEntry.CREDIT);
					createTLF(interfaceFile, conversionFormat, senderTlfBranch, interMisc, currencyCode, eNo, TLFNO, senderTlfBranch, senderTlfBranch, isCash(interfaceFile));

					tlf.setBranchId(recipientTlfBranch.getBranchCode());
				}

			}

			if (acode != null) {
				tlf.setCoaId(acode);
			} else {
				throw new SystemException(ErrorCode.ACODE_FETCH_FAILED, " Failed to retreive acode" + errorString);
			}

			// tlf.setCustomerId("IMPORTED FROM CSC");

			tlf.setEnoNo(eNo);

			tlf.setHomeAmount(homeAmount);

			tlf.setLocalAmount(localAmount);

			tlf.setNarration(narration);

			tlf.setPaid(true);

			tlf.setRate(rate);

			if (interfaceFile.getUniqueKey().getRdocnum() == null) {
				tlf.setReferenceNo(interfaceFile.getUniqueKey().getRldgacct() + "-" + interfaceFile.getUniqueKey().getTranno());
			} else {
				tlf.setReferenceNo(interfaceFile.getUniqueKey().getRdocnum());
			}

			tlf.setReferenceType(PolicyReferenceType.CSC_IMPORT);

			tlf.setSettlementDate(interfaceFile.getTrandate());

			tlf.setTlfNo(TLFNO);

			if (senderBranch != null) {
				// status of acname is the status of sender tlf
				tlf = setInterbranchStatus(tlf, interfaceFile, conversionFormat, acname, branch, senderBranch, interbranchCash);
			} else {
				if (!tlf.getBranchId().equals(branch.getBranchCode())) {
					tlf = setInterbranchStatus(tlf, interfaceFile, conversionFormat, acname, branch, senderBranch, interbranchCash);
				} else {
					tlf = setStatus(tlf, interfaceFile, conversionFormat, acname);
				}
			}

			if (interfaceFile.getUniqueKey().getBatctrcde().trim().equals(BatchTranCode.T413.name())) {
				tlf.setRenewal(true);
			} else {
				tlf.setRenewal(false);
			}

			tlf.setInterfaceFile(interfaceFile);
			tlfService.addNewTlf(tlf);
			if (!interfaceFile.isConverted()) {
				interfaceFile.setConverted(true);
				interfaceFileService.updateInterfaceFile(interfaceFile);
			}
		} catch (SystemException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in createTLF method ");
			throw e;
		} catch (Exception ee) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + ee.getClass().getName() + " Exception in createTLF method ");
			throw new SystemException(ErrorCode.SYSTEM_ERROR, ee.getMessage(), ee);
		}
	}

	private boolean isCash(InterfaceFile interfaceFile) throws SystemException, Exception {
		boolean isCash = false;
		try {
			String glCashBankCode = Utils.getLastNCharFromString(interfaceFile.getGlcode(), lastNChar);
			for (String paymentCode : paymentCodeList) {
				if (interfaceFile.getGlcode().trim().contains(paymentCode)) {
					for (String cashCode : cashCodeList) {
						if (glCashBankCode.trim().equals(cashCode.trim())) {
							isCash = true;
							break;
						}
					}
				}
			}
			if (!isCash) {
				UniqueKey uniqueKey = new UniqueKey(interfaceFile.getUniqueKey());
				uniqueKey.setSacscode(null);
				uniqueKey.setSacstype(null);
				uniqueKey.setRldgacct(null);
				uniqueKey.setTranno(null);
				List<InterfaceFile> interfaceFileList = interfaceFileService.findByCriteria(uniqueKey, interfaceFile.getImportedDate(), false);
				if (interfaceFileList != null) {
					for (InterfaceFile intF : interfaceFileList) {
						for (String paymentCode : paymentCodeList) {
							if (intF.getGlcode().trim().contains(paymentCode)) {
								glCashBankCode = Utils.getLastNCharFromString(intF.getGlcode(), lastNChar);
								for (String cashCode : cashCodeList) {
									if (glCashBankCode.equals(cashCode.trim())) {
										isCash = true;
										break;
									}
								}
							}
						}
					}
				}
				interfaceFileList = null;
				uniqueKey = null;
			}
		} catch (SystemException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in isCash method ");
			throw e;
		} catch (Exception ee) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + ee.getClass().getName() + " Exception in isCash method ");
			throw new SystemException(ErrorCode.SYSTEM_ERROR, ee.getMessage(), ee);
		}
		return isCash;
	}

	private CoReStatus getCoReStatus(InterfaceFile interfaceFile) throws SystemException, Exception {
		CoReStatus coReStatus;
		try {
			UniqueKey uniqueKey = new UniqueKey(interfaceFile.getUniqueKey());
			uniqueKey.setSacstype(null);
			uniqueKey.setSacscode(CoReStatus.CO.getLabel());
			long coCount = interfaceFileService.findCountByCriteria(uniqueKey);
			if (coCount > 0) {
				coReStatus = CoReStatus.CO;
			} else {
				uniqueKey.setSacscode(CoReStatus.RE.getLabel());
				long reCount = interfaceFileService.findCountByCriteria(uniqueKey);
				if (reCount > 0) {
					coReStatus = CoReStatus.RE;
				} else {
					coReStatus = CoReStatus.N;
				}
			}
			uniqueKey = null;
		} catch (SystemException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in getCoReStatus method ");
			throw e;
		} catch (Exception ee) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + ee.getClass().getName() + " Exception in getCoReStatus method ");
			throw new SystemException(ErrorCode.SYSTEM_ERROR, ee.getMessage(), ee);
		}

		return coReStatus;
	}

	private TLF setStatus(TLF tlf, InterfaceFile interfaceFile, TLFConversionFormat conversionFormat, CSCAcname acname) throws SystemException, Exception {
		try {
//			String status = null;
//			String tranCode = null;
			String trantypeId = null;
			boolean isReverse = getReverse(interfaceFile, conversionFormat);

			// if glcode is cash then cash else transfer
			boolean isCash = acname.isForceTran() ? false : isCash(interfaceFile);

			if ((acname.getStatus().equals(DoubleEntry.CREDIT) && !isReverse) || (acname.getStatus().equals(DoubleEntry.DEBIT) && isReverse)) {
//				status = isCash ? Status.CCV : Status.TCV;
//				tranCode = isCash ? TranCode.CSCREDIT : TranCode.TRCREDIT;
				trantypeId = isCash? KeyFactorChecker.getCSCredit():KeyFactorChecker.getTRCredit();
			} else {
//				status = isCash ? Status.CDV : Status.TDV;
//				tranCode = isCash ? TranCode.CSDEBIT : TranCode.TRDEBIT;
				trantypeId = isCash? KeyFactorChecker.getCSDebit():KeyFactorChecker.getTRDebit();
			}
//			tlf.setStatus(status);
//			tlf.setTranCode(tranCode);
			tlf.setTranTypeId(trantypeId);
		} catch (Exception ee) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + ee.getClass().getName() + " Exception in setStatus method ");
			throw new SystemException(ErrorCode.SYSTEM_ERROR, ee.getMessage(), ee);
		}

		return tlf;
	}

	private TLF setInterbranchStatus(TLF tlf, InterfaceFile interfaceFile, TLFConversionFormat conversionFormat, CSCAcname acname, Branch branch, Branch senderBranch,
			boolean interbranchCash) throws SystemException, Exception {
		try {
//			String status = null;
//			String tranCode = null;
			String trantypeId = null;

			DoubleEntry doubleEntry = acname.getStatus();

			// here the double entry is the status of (for example) FG,S
			// and it is for sender tlf
			// and the reverse is for recipient tlf
			// if sender branch and branch (which is transaction branch of tlf
			// here )
			// are not equal then it is recipient tlf
			// else sender tlf
			if (senderBranch != null && branch.getBranchCode().equals(senderBranch.getBranchCode())) {
				// reverse the status
				doubleEntry = (doubleEntry.equals(DoubleEntry.DEBIT)) ? DoubleEntry.CREDIT : DoubleEntry.DEBIT;
			}
			// if it is the same can be left as it is

			boolean isReverse = getReverse(interfaceFile, conversionFormat);

			// UniqueKey uniqueKey = new
			// UniqueKey(interfaceFile.getUniqueKey());
			// uniqueKey.setSacscode(null);
			// uniqueKey.setSacstype(null);
			// uniqueKey.setRldgacct(null);
			// uniqueKey.setTranno(null);

			boolean isCash = acname.isForceTran() ? false : isCash(interfaceFile);

			// two middle tlfs are liable to become cash
			if ((acname.getAcname().equals(COACode.INTERBRANCH_SUNDRY) || acname.getAcname().equals(properties.getProperty(CSCImportConstants.CSC_PAYMENT_ACNAME).trim())) && isCash
					&& interbranchCash) {
				if ((doubleEntry.equals(DoubleEntry.CREDIT) && !isReverse) || (doubleEntry.equals(DoubleEntry.DEBIT) && isReverse)) {
//					status = Status.CCV;
//					tranCode = TranCode.CSCREDIT;
					trantypeId = KeyFactorChecker.getCSCredit();
				} else {
//					status = Status.CDV;
//					tranCode = TranCode.CSDEBIT;
					trantypeId = KeyFactorChecker.getCSDebit();
				}
			} else {
				// else always transfer
				if ((doubleEntry.equals(DoubleEntry.CREDIT) && !isReverse) || (doubleEntry.equals(DoubleEntry.DEBIT) && isReverse)) {
//					status = Status.TCV;
//					tranCode = TranCode.TRCREDIT;
					trantypeId = KeyFactorChecker.getTRCredit();
				} else {
//					status = Status.TDV;
//					tranCode = TranCode.TRDEBIT;
					trantypeId = KeyFactorChecker.getTRDebit();
				}
			}
//			tlf.setStatus(status);
//			tlf.setTranCode(tranCode);
			tlf.setTranTypeId(trantypeId);

		} catch (Exception ee) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + ee.getClass().getName() + " Exception in setInterbranchStatus method ");
			throw new SystemException(ErrorCode.SYSTEM_ERROR, ee.getMessage(), ee);
		}

		return tlf;
	}

	private boolean getReverse(InterfaceFile interfaceFile, TLFConversionFormat conversionFormat) throws SystemException, Exception {
		GLSign glSign = (interfaceFile.getTranamt() < 0) ? GLSign.NEGATIVE : GLSign.POSITIVE;
		boolean isReverse = (conversionFormat.getGlSign().equals(glSign)) ? false : true;
		return isReverse;
	}

	private double handleLocalAmount(CSCAcname acname, InterfaceFile interfaceFile) throws SystemException, Exception {
		double localAmount = Utils.nagateIfNagative(interfaceFile.getTranamt());
		try {
			switch (acname.getCalculationType()) {
				case CALCULATE:
					for (PairedKey pairedKey : acname.getPairedKeys()) {
						UniqueKey uniqueKey = createUniqueKey(interfaceFile, pairedKey);

						// usually should return single result , with the
						// exception of coinsurance
						List<InterfaceFile> interfaceFileList = interfaceFileService.findByCriteria(uniqueKey, interfaceFile.getImportedDate(), false);
						if (interfaceFileList != null && !interfaceFileList.isEmpty()) {
							for (InterfaceFile interfaceFile2 : interfaceFileList) {
								if (pairedKey.getCalculationMethod() == null) {
									throw new SystemException(ErrorCode.NO_PARIED_KEY_CALCULATION_METHOD,
											"There is no CalculationMethod for pairedKey : " + pairedKey.getBatchTranCode() + ", " + pairedKey.getSacscode() + ", "
													+ pairedKey.getSacstype() + " , paired key ID : " + pairedKey.getId() + ".");
								} else if (pairedKey.getCalculationMethod().equals(CalculationMethod.ADDITION)) {
									localAmount += Utils.nagateIfNagative(interfaceFile2.getTranamt());
								} else if (pairedKey.getCalculationMethod().equals(CalculationMethod.SUBSTRACTION)) {
									localAmount -= Utils.nagateIfNagative(interfaceFile2.getTranamt());
								}
							}
						} else {
							if (!pairedKey.isOptional()) {
								throw new SystemException(ErrorCode.NO_INTERFACE_FILE, "InterfaceFile not found for Paired key : " + pairedKey.getBatchTranCode() + ", "
										+ pairedKey.getSacscode() + ", " + pairedKey.getSacstype() + " for ID : " + pairedKey.getId() + ".");
							}
						}
						interfaceFileList = null;
						uniqueKey = null;
					}
					break;
				case DIRECT:
					break;
				default:
					break;
			}
		} catch (SystemException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in handleLocalAmount method ");
			throw e;
		} catch (Exception ee) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + ee.getClass().getName() + " Exception in handleLocalAmount method ");
			throw new SystemException(ErrorCode.SYSTEM_ERROR, ee.getMessage(), ee);
		}

		return scaleDouble(localAmount);
	}

	private UniqueKey createUniqueKey(InterfaceFile interfaceFile, PairedKey pairedKey) throws SystemException, Exception {
		UniqueKey uniqueKey = new UniqueKey(interfaceFile.getUniqueKey());
		if (pairedKey.isSamePolicyNo()) {
			uniqueKey.setRdocnum(null);
		} else {
			uniqueKey.setRldgacct(null);
			uniqueKey.setTranno(null);
		}
		uniqueKey.setBatctrcde(pairedKey.getBatchTranCode());
		uniqueKey.setSacscode(pairedKey.getSacscode());
		uniqueKey.setSacstype(pairedKey.getSacstype());
		return uniqueKey;
	}

	private double scaleDouble(double amount) throws SystemException, Exception {
		double result = new BigDecimal(amount).setScale(Integer.parseInt(properties.getProperty(CSCImportConstants.GLOBAL_DECIMAL_SCALE)), RoundingMode.HALF_UP).doubleValue();
		return result;
	}

	private String getNarration(double homeAmount, UniqueKey uniqueKey) throws SystemException, Exception {
		String result = "";
		try {
			result += homeAmount;
			String sacscode = (narrationProperties.getProperty(uniqueKey.getSacscode().trim()) == null) ? "SACSCODE : " + uniqueKey.getSacscode().trim()
					: narrationProperties.getProperty(uniqueKey.getSacscode().trim()).trim();
			String sacstype = (narrationProperties.getProperty(uniqueKey.getSacstype().trim()) == null) ? "SACSTYPE : " + uniqueKey.getSacstype().trim()
					: narrationProperties.getProperty(uniqueKey.getSacstype().trim()).trim();
			result += " for " + sacscode + " " + sacstype;

			String policy = null;
			if (uniqueKey.getRldgacct() != null) {
				policy = " Entity No : " + uniqueKey.getRldgacct().trim();
			}
			if (uniqueKey.getRdocnum() != null) {
				if (policy != null) {
					policy += ",";
				} else {
					policy = "";
				}
				policy += " Receipt No : " + uniqueKey.getRdocnum().trim();
			}

			if (policy != null) {
				result += " of" + policy;
			}

			String batctrcde = (narrationProperties.getProperty(uniqueKey.getBatctrcde().trim()) == null) ? "BATCTRCDE : " + uniqueKey.getBatctrcde().trim()
					: narrationProperties.getProperty(uniqueKey.getBatctrcde().trim()).trim();

			result += " " + batctrcde + ".";

		} catch (Exception ee) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + ee.getClass().getName() + " Exception in getNarration method ");
			throw new SystemException(ErrorCode.SYSTEM_ERROR, ee.getMessage(), ee);
		}
		return result;
	}
}
