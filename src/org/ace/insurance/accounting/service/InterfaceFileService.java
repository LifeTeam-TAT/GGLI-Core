/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.accounting.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.accounting.InterfaceFile;
import org.ace.insurance.accounting.UniqueKey;
import org.ace.insurance.accounting.persistence.interfaces.IInterfaceFileDAO;
import org.ace.insurance.accounting.service.interfaces.IInterfaceFileService;
import org.ace.insurance.accounting.service.interfaces.ITLFConverter;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.payment.service.interfaces.ITLFService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.ace.java.web.common.MessageId;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "InterfaceFileService")
public class InterfaceFileService extends BaseService implements IInterfaceFileService {

	private Logger logger = Logger.getLogger(this.getClass());

	@Resource(name = "InterfaceFileDAO")
	private IInterfaceFileDAO interfaceFileDAO;

	@Resource(name = "TLFConverter")
	private ITLFConverter tlfConverter;

	@Resource(name = "TLFService")
	private ITLFService tlfService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInterfaceFile(InterfaceFile interfaceFile) throws SystemException {
		try {
			interfaceFileDAO.update(interfaceFile);
		} catch (DAOException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in updateInterfaceFile method at " + this.getClass().getName());
			throw new SystemException(e.getErrorCode(), "Failed to update a InterfaceFile", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteInterfaceFile(InterfaceFile interfaceFile) throws SystemException {
		try {
			interfaceFileDAO.delete(interfaceFile);
		} catch (DAOException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in deleteInterfaceFile method at " + this.getClass().getName());
			throw new SystemException(e.getErrorCode(), "Failed to delete a InterfaceFile", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public InterfaceFile findInterfaceFileById(String id) throws SystemException {
		InterfaceFile result = null;
		try {
			result = interfaceFileDAO.findById(id);
		} catch (DAOException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in findInterfaceFileById method at " + this.getClass().getName());
			throw new SystemException(e.getErrorCode(), "Failed to find a InterfaceFile (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<InterfaceFile> findByCriteria(UniqueKey uniqueKey, Date importedDate, boolean onlyConvertable) throws SystemException {
		List<InterfaceFile> result = null;
		try {
			result = interfaceFileDAO.findByCriteria(uniqueKey, importedDate, onlyConvertable);
		} catch (DAOException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in findByCriteria method at " + this.getClass().getName());
			throw new SystemException(e.getErrorCode(), "Failed to find InterfaceFile by criteria " + uniqueKey, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public long findCountByDate(Date actionDate, boolean onlyConvertable) throws SystemException {
		long result;
		try {
			result = interfaceFileDAO.findCountByDate(actionDate, onlyConvertable);
		} catch (DAOException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in findCountByDate method at " + this.getClass().getName());
			throw new SystemException(e.getErrorCode(), "Failed to find InterfaceFile by actionDate " + actionDate, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public long findCountByCriteria(UniqueKey uniqueKey) throws SystemException {
		long result;
		try {
			result = interfaceFileDAO.findCountByCriteria(uniqueKey);
		} catch (DAOException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in findCountByCriteria method at " + this.getClass().getName());
			throw new SystemException(e.getErrorCode(), "Failed to find InterfaceFile by criteria " + uniqueKey, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int importInterfaceFiles(List<String> dataStringList, int dataRows, Date actionDate, String uploadedFileName) throws SystemException {
		int importCount = 0;
		try {
			// TODO handle
			Date pastOneMonthDate;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1);
			pastOneMonthDate = calendar.getTime();
			interfaceFileDAO.moveOldFiles(pastOneMonthDate, true);

			importCount = interfaceFileDAO.importInterfaceFiles(dataStringList, dataRows, actionDate, uploadedFileName);

		} catch (DAOException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in importInterfaceFiles method at " + this.getClass().getName());
			throw new SystemException(e.getErrorCode(), e.getMessage(), e);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, e.getMessage(), e);
		}
		return importCount;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<String> findConvertableFileCount() throws SystemException {
		List<String> result;
		try {
			result = interfaceFileDAO.findConvertableFileCount();
		} catch (DAOException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in findConvertableFileCount method at " + this.getClass().getName());
			throw new SystemException(e.getErrorCode(), "Failed to findConvertableFileCount");
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public long convertFiles(Date actionDate) throws SystemException, Exception {
		long result = 0;
		try {
			InterfaceFile interfaceFile;
			do {
				interfaceFile = interfaceFileDAO.findNextConvertibleFile(actionDate);
				if (interfaceFile != null) {
					tlfConverter.convertToTLF(interfaceFile, false, null);
					// if (converterDTO.getInterfaceFileList() != null) {
					// for (InterfaceFile interfaceFile2 :
					// converterDTO.getInterfaceFileList()) {
					// interfaceFile2.setConverted(true);
					// updateInterfaceFile(interfaceFile2);
					// }
					// }
					//
					// if (converterDTO.getTlfList() != null) {
					// for (TLF tlf : converterDTO.getTlfList()) {
					// tlfService.addNewTlf(tlf);
					// result++;
					// }
					// }
					// System.gc();
				} else {
					break;
				}
			} while (interfaceFile != null);

			result = tlfService.findConvertedTlf(actionDate);

		} catch (DAOException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in convertFiles method at " + this.getClass().getName());
			throw new SystemException(e.getErrorCode(), "Failed to convertFiles");
		} catch (SystemException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in convertFiles method at " + this.getClass().getName());
			throw e;
		} catch (Exception ee) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + ee.getClass().getName() + " Exception in convertFiles method at " + this.getClass().getName());
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "System error occured." + ee.getMessage(), ee);
		}
		// throw new SystemException(null, "NOT COMMIT YET");
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public long findCoReCountByCriteria(UniqueKey uniqueKey) throws SystemException {
		long result;
		try {
			result = interfaceFileDAO.findCoReCountByCriteria(uniqueKey);
		} catch (DAOException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in findInterfaceFileById method at " + this.getClass().getName());
			throw new SystemException(e.getErrorCode(), "Failed to findCoReCountByCriteria " + uniqueKey, e);
		}
		return result;
	}

	public String findPolicyProductCode(String rldgacct, Integer tranno) throws SystemException {
		String result;
		try {
			result = interfaceFileDAO.findPolicyProductCode(rldgacct, tranno);
		} catch (DAOException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in findInterfaceFileById method at " + this.getClass().getName());
			throw new SystemException(e.getErrorCode(), "Failed to findPolicyProductCode " + rldgacct + "," + tranno, e);
		}
		return result;
	}

}