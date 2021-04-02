/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.accounting.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.accounting.BatchKey;
import org.ace.insurance.accounting.InterfaceFile;
import org.ace.insurance.accounting.UniqueKey;
import org.ace.insurance.accounting.persistence.interfaces.IInterfaceFileDAO;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.web.common.MessageId;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Repository("InterfaceFileDAO")
public class InterfaceFileDAO extends BasicDAO implements IInterfaceFileDAO {
	// no insert method because interface file are inserted with native query
	// down below
	// and creation of id and createdDate is handled by SQL server
	// and createdUserId by query

	private Logger logger = Logger.getLogger(this.getClass());

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(InterfaceFile InterfaceFile) throws DAOException {
		try {
			em.merge(InterfaceFile);
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in update method at " + this.getClass().getName());
			throw translate("Failed to update InterfaceFile", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(InterfaceFile InterfaceFile) throws DAOException {
		try {
			InterfaceFile = em.merge(InterfaceFile);
			em.remove(InterfaceFile);
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in delete method at " + this.getClass().getName());
			throw translate("Failed to update InterfaceFile", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public InterfaceFile findById(String id) throws DAOException {
		InterfaceFile result = null;
		try {
			result = em.find(InterfaceFile.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in findById method at " + this.getClass().getName());
			throw translate("Failed to find InterfaceFile", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<InterfaceFile> findByCriteria(UniqueKey uniqueKey, Date importedDate, boolean onlyConvertable) throws DAOException {
		List<InterfaceFile> result = null;
		try {

			logger.info(MessageId.CSC_FETCH + " : " + " batctrcde " + uniqueKey.getBatctrcde() + " , batcbatch " + uniqueKey.getBatcbatch()
					+ " , rldgacct " + uniqueKey.getRldgacct() + " , tranno " + uniqueKey.getTranno() + " , sacscode " + uniqueKey.getSacscode()
					+ " , sacstype " + uniqueKey.getSacstype() + " , rdocnum " + uniqueKey.getRdocnum() + " , " + " importedDate " + importedDate
					+ " , onlyConvertable " + onlyConvertable);

			StringBuffer queryString = new StringBuffer(" Select c from InterfaceFile c where " + " c.uniqueKey.batcpfx = :batcpfx AND "
					+ " c.uniqueKey.batccoy = :batccoy AND " + " c.uniqueKey.batcbrn = :batcbrn AND " + " c.uniqueKey.batcactyr = :batcactyr AND "
					+ " c.uniqueKey.batcactmn = :batcactmn ");

			if (uniqueKey.getBatctrcde() != null) {
				queryString.append(" AND c.uniqueKey.batctrcde = :batctrcde ");
			}
			if (uniqueKey.getBatcbatch() != null) {
				queryString.append(" AND c.uniqueKey.batcbatch = :batcbatch ");
			}
			if (uniqueKey.getRldgacct() != null) {
				queryString.append(" AND c.uniqueKey.rldgacct = :rldgacct ");
			}
			if (uniqueKey.getTranno() != null) {
				queryString.append(" AND c.uniqueKey.tranno = :tranno ");
			}

			if (uniqueKey.getSacscode() != null) {
				queryString.append(" AND c.uniqueKey.sacscode =:sacscode ");
			}

			if (uniqueKey.getSacstype() != null) {
				queryString.append(" AND c.uniqueKey.sacstype =:sacstype ");
			}

			if (uniqueKey.getRdocnum() != null) {
				queryString.append(" AND c.uniqueKey.rdocnum =:rdocnum ");
			}
			if (importedDate != null) {
				queryString.append(" AND c.importedDate >= :start AND c.importedDate <= :end");
			}

			if (onlyConvertable) {
				queryString.append(" AND c.isConverted = FALSE ");
			}

			// if (isPayment) {
			// queryString.append(" AND c.uniqueKey.rldgacct IS NOT NULL AND
			// c.uniqueKey.sacscode != :cashSacscode ");
			// }

			Query query = em.createQuery(queryString.toString());

			query.setParameter("batcpfx", uniqueKey.getBatcpfx());
			query.setParameter("batccoy", uniqueKey.getBatccoy());
			query.setParameter("batcbrn", uniqueKey.getBatcbrn());
			query.setParameter("batcactyr", uniqueKey.getBatcactyr());
			query.setParameter("batcactmn", uniqueKey.getBatcactmn());

			if (uniqueKey.getBatctrcde() != null) {
				query.setParameter("batctrcde", uniqueKey.getBatctrcde());
			}
			if (uniqueKey.getBatcbatch() != null) {
				query.setParameter("batcbatch", uniqueKey.getBatcbatch());
			}
			if (uniqueKey.getRldgacct() != null) {
				query.setParameter("rldgacct", uniqueKey.getRldgacct());
			}
			if (uniqueKey.getTranno() != null) {
				query.setParameter("tranno", uniqueKey.getTranno());
			}

			if (uniqueKey.getSacscode() != null) {
				query.setParameter("sacscode", uniqueKey.getSacscode());
			}

			if (uniqueKey.getSacstype() != null) {
				query.setParameter("sacstype", uniqueKey.getSacstype());
			}

			if (uniqueKey.getRdocnum() != null) {
				query.setParameter("rdocnum", uniqueKey.getRdocnum());
			}

			if (importedDate != null) {
				query.setParameter("start", Utils.resetStartDate(importedDate));
				query.setParameter("end", Utils.resetEndDate(importedDate));
			}

			// if (isPayment) {
			// query.setParameter("cashSacscode", SacsCode.CN.name());
			// }

			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(
					MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in findByCriteria method at " + this.getClass().getName());
			throw translate("Failed to find by criteria of InterfaceFile.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public long findCountByDate(Date actionDate, boolean onlyConvertable) throws DAOException {
		long result;
		try {
			StringBuffer queryString = new StringBuffer(
					" Select COUNT(c) from InterfaceFile c WHERE c.importedDate >= :start AND c.importedDate <= :end ");
			if (onlyConvertable) {
				queryString.append(" AND c.isConverted = FALSE ");
			}
			Query query = em.createQuery(queryString.toString());
			query.setParameter("start", Utils.resetStartDate(actionDate));
			query.setParameter("end", Utils.resetEndDate(actionDate));
			result = (long) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(
					MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in findCountByDate method at " + this.getClass().getName());
			throw translate("Failed to find by actionDate.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public long findCountByCriteria(UniqueKey uniqueKey) throws DAOException {
		long result;
		try {
			StringBuffer queryString = new StringBuffer(" Select COUNT(c) from InterfaceFile c where " + " c.uniqueKey.batcpfx = :batcpfx AND "
					+ " c.uniqueKey.batccoy = :batccoy AND " + " c.uniqueKey.batcbrn = :batcbrn AND " + " c.uniqueKey.batcactyr = :batcactyr AND "
					+ " c.uniqueKey.batcactmn = :batcactmn ");

			if (uniqueKey.getBatctrcde() != null) {
				queryString.append(" AND c.uniqueKey.batctrcde = :batctrcde ");
			}
			if (uniqueKey.getBatcbatch() != null) {
				queryString.append(" AND c.uniqueKey.batcbatch = :batcbatch ");
			}
			if (uniqueKey.getRldgacct() != null) {
				queryString.append(" AND c.uniqueKey.rldgacct = :rldgacct ");
			}
			if (uniqueKey.getTranno() != null) {
				queryString.append(" AND c.uniqueKey.tranno = :tranno ");
			}

			if (uniqueKey.getSacscode() != null) {
				queryString.append(" AND c.uniqueKey.sacscode =:sacscode ");
			}

			if (uniqueKey.getSacstype() != null) {
				queryString.append(" AND c.uniqueKey.sacstype =:sacstype ");
			}

			Query query = em.createQuery(queryString.toString());

			query.setParameter("batcpfx", uniqueKey.getBatcpfx());
			query.setParameter("batccoy", uniqueKey.getBatccoy());
			query.setParameter("batcbrn", uniqueKey.getBatcbrn());
			query.setParameter("batcactyr", uniqueKey.getBatcactyr());
			query.setParameter("batcactmn", uniqueKey.getBatcactmn());

			if (uniqueKey.getBatctrcde() != null) {
				query.setParameter("batctrcde", uniqueKey.getBatctrcde());
			}
			if (uniqueKey.getBatcbatch() != null) {
				query.setParameter("batcbatch", uniqueKey.getBatcbatch());
			}
			if (uniqueKey.getRldgacct() != null) {
				query.setParameter("rldgacct", uniqueKey.getRldgacct());
			}
			if (uniqueKey.getTranno() != null) {
				query.setParameter("tranno", uniqueKey.getTranno());
			}

			if (uniqueKey.getSacscode() != null) {
				query.setParameter("sacscode", uniqueKey.getSacscode());
			}

			if (uniqueKey.getSacstype() != null) {
				query.setParameter("sacstype", uniqueKey.getSacstype());
			}

			result = (long) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in findCountByCriteria method at "
					+ this.getClass().getName());
			throw translate("Failed to find by criteria of InterfaceFile.", pe);
		}
		return result;
	}

	@Resource
	private PlatformTransactionManager transactionManager;

	@Transactional(propagation = Propagation.REQUIRED)
	public int importInterfaceFiles(List<String> dataStringList, int rows, Date actionDate, String uploadedFileName) {
		int importCount = 0;

		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = null;

		try {
			// TODO add column uploaded filename
			status = transactionManager.getTransaction(def);
			String queryString = "INSERT INTO " + TableName.INTERFACE_FILE
					+ " (CREATEDUSERID,IMPORTEDDATE,VERSION,BATCPFX,BATCCOY,BATCBRN,BATCACTYR,BATCACTMN,BATCTRCDE,BATCBATCH,RDOCPFX,RDOCCOY,RDOCNUM,RLDGPFX,RLDGCOY,RLDGACCT,CHDRPFX,CHDRCOY,"
					+ "CHDRNUM,ORIGCURR,ACCTCURR,CRATE,CNTTYPE,TRANNO,CNTBRANCH,CHDRSTCDA,CHDRSTCDB,CHDRSTCDC,CHDRSTCDD,CHDRSTCDE,POSTMONTH,POSTYEAR,"
					+ "TRANDATE,TRANTIME,EFFDATE,SACSCODE,SACSTYPE,TRANAMT,GENLCUR, GENLPFX,GENLCOY,GLCODE,ACCPFX,ACCCOY,ACCNUM,CCDATE,EXDATE,STATREASN,RITYPE) "
					+ "VALUES ( '" + userProcessService.getLoginUser().getId() + "' , '" + Utils.getDatabaseDateString(actionDate) + "' , 1 , ";
			for (String data : dataStringList) {
				Query query = em.createNativeQuery(queryString + data + " );");
				query.executeUpdate();
				importCount++;
			}

			if (rows != importCount) {
				if (rows > importCount) {
					transactionManager.rollback(status);
					throw new DAOException(ErrorCode.INT_FILE_COUNT_MISMATCH,
							"Only " + importCount + "records out of " + rows + " were about to be imported. The transaction was rolled back.");
				} else {
					transactionManager.rollback(status);
					throw new DAOException(ErrorCode.INT_FILE_COUNT_MISMATCH,
							"Duplicate records were about to be imported. The transaction was rolled back.");
				}

			}

			// TODO Find if data with uncommitted batchkey exist
			// TODO need to find only committed value fetch query
			// List<BatchKey> keys = getUncommitedBatchKeyList(actionDate);

			// overall
			double totalAmount = sumTranAmountByDate(actionDate, null);
			if (totalAmount != 0) {
				transactionManager.rollback(status);
				throw new DAOException(ErrorCode.OVERAL_SUM_NOT_ZERO, "Total Sum of Transaction Amount for overall is not zero.");
			} else {
				List<BatchKey> keys = getUncommitedBatchKeyList(actionDate);
				for (BatchKey key : keys) {
					totalAmount = sumTranAmountByDate(actionDate, key);
					if (totalAmount != 0) {
						transactionManager.rollback(status);
						throw new DAOException(ErrorCode.GROUP_SUM_NOT_ZERO,
								"Total Sum of Transaction Amount for " + key.toString() + " is not zero.");
					}
				}
			}

			transactionManager.commit(status);
		} catch (PersistenceException pe) {
			transactionManager.rollback(status);
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in importInterfaceFiles method at "
					+ this.getClass().getName());
			throw translate("Failed to find by criteria of InterfaceFile.", pe);
		}
		return importCount;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void moveOldFiles(Date pastOneMonthDate, boolean onlyConverted) throws DAOException, Exception {
		try {
			// TODO add column uploaded filename
			String queryString = "INSERT INTO " + TableName.INTERFACE_FILE_HISTORY + " SELECT * FROM " + TableName.INTERFACE_FILE
					+ " WHERE IMPORTEDDATE <= '" + Utils.getDatabaseDateString(pastOneMonthDate) + "'";
			if (onlyConverted) {
				queryString += "AND IS_CONVERTED = 1 ";
			}
			queryString += ";";

			Query query = em.createNativeQuery(queryString);
			query.executeUpdate();
			
			
			
			queryString = "INSERT INTO "+TableName.INTERFACE_FILE_HIST_TLF_REF+
						" SELECT * FROM "+TableName.INTERFACE_FILE_TLF_REF+" WHERE INTERFACE_FILE_ID IN ( "+ 
						" SELECT ID FROM " + TableName.INTERFACE_FILE +
						" WHERE IMPORTEDDATE <= '" + Utils.getDatabaseDateString(pastOneMonthDate)+"' ";
			if (onlyConverted) {
				queryString += "AND IS_CONVERTED = 1 ";
			}			
			queryString+= " );";
			query = em.createNativeQuery(queryString);
			query.executeUpdate();
			

			queryString = "DELETE FROM "+TableName.INTERFACE_FILE_TLF_REF+" WHERE INTERFACE_FILE_ID IN ( "+ 
					" SELECT ID FROM " + TableName.INTERFACE_FILE +
					" WHERE IMPORTEDDATE <= '" + Utils.getDatabaseDateString(pastOneMonthDate)+"' ";
			if (onlyConverted) {
				queryString += "AND IS_CONVERTED = 1 ";
			}			
			queryString+= " );";
			query = em.createNativeQuery(queryString);
			query.executeUpdate();
		
			
			queryString = "DELETE FROM " + TableName.INTERFACE_FILE + " WHERE IMPORTEDDATE <= '" + Utils.getDatabaseDateString(pastOneMonthDate)
					+ "'";
			if (onlyConverted) {
				queryString += "AND IS_CONVERTED = 1 ";
			}
			queryString += ";";

			query = em.createNativeQuery(queryString);
			query.executeUpdate();

		} catch (PersistenceException pe) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in moveOldFiles method at " + this.getClass().getName());
			throw translate("Failed to move files older than one month to history.", pe);
		} catch (Exception e) {
			throw e;
		}
	}

	// this method serves to sum the transaction amount of UNCOMMITTED
	// interface files to check if they are valid or not.
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
	private double sumTranAmountByDate(Date actionDate, BatchKey batchKey) {
		double result = 1;
		try {
			StringBuffer queryString = new StringBuffer(
					"SELECT SUM(c.tranamt) FROM InterfaceFile c WHERE c.isConverted = FALSE AND c.importedDate >= :start AND c.importedDate <= :end ");
			if (batchKey != null) {
				queryString.append(" AND c.uniqueKey.batcpfx =:batcpfx ");
				queryString.append(" AND c.uniqueKey.batccoy =:batccoy ");
				queryString.append(" AND c.uniqueKey.batcbrn =:batcbrn ");
				queryString.append(" AND c.uniqueKey.batcactyr =:batcactyr ");
				queryString.append(" AND c.uniqueKey.batcactmn =:batcactmn ");
				queryString.append(" AND c.uniqueKey.batctrcde =:batctrcde ");
				queryString.append(" AND c.uniqueKey.batcbatch =:batcbatch ");
			}

			Query query = em.createQuery(queryString.toString());
			query.setParameter("start", Utils.resetStartDate(actionDate));
			query.setParameter("end", Utils.resetEndDate(actionDate));

			if (batchKey != null) {
				query.setParameter("batcpfx", batchKey.getBatcpfx());
				query.setParameter("batccoy", batchKey.getBatccoy());
				query.setParameter("batcbrn", batchKey.getBatcbrn());
				query.setParameter("batcactyr", batchKey.getBatcactyr());
				query.setParameter("batcactmn", batchKey.getBatcactmn());
				query.setParameter("batctrcde", batchKey.getBatctrcde());
				query.setParameter("batcbatch", batchKey.getBatcbatch());
			}

			result = (double) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in sumTranAmountByDate method at "
					+ this.getClass().getName());
			throw translate("Failed to find by criteria of InterfaceFile.", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
	private List<BatchKey> getUncommitedBatchKeyList(Date actionDate) {
		List<BatchKey> result;
		try {
			StringBuffer queryString = new StringBuffer(
					" SELECT NEW org.ace.insurance.accounting.BatchKey(c.uniqueKey.batcpfx,c.uniqueKey.batccoy,c.uniqueKey.batcbrn,c.uniqueKey.batcactyr,c.uniqueKey.batcactmn,c.uniqueKey.batctrcde,c.uniqueKey.batcbatch) FROM InterfaceFile c ");
			queryString.append(" WHERE c.isConverted = FALSE AND c.importedDate >= :start AND c.importedDate <= :end ");
			queryString.append(
					" GROUP BY c.uniqueKey.batcpfx,c.uniqueKey.batccoy,c.uniqueKey.batcbrn,c.uniqueKey.batcactyr,c.uniqueKey.batcactmn,c.uniqueKey.batctrcde,c.uniqueKey.batcbatch ");
			Query query = em.createQuery(queryString.toString());
			query.setParameter("start", Utils.resetStartDate(actionDate));
			query.setParameter("end", Utils.resetEndDate(actionDate));
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in getUncommitedBatchKeyList method at "
					+ this.getClass().getName());
			throw translate("Failed to find by criteria of InterfaceFile.", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<String> findConvertableFileCount() throws DAOException {
		List<String> result = null;
		try {
			StringBuffer queryString = new StringBuffer(
					"SELECT c.importedDate,COUNT(c) FROM InterfaceFile c where c.isConverted = FALSE GROUP BY c.importedDate ORDER BY c.importedDate ASC");
			Query query = em.createQuery(queryString.toString());
			@SuppressWarnings("unchecked")
			List<Object[]> objectList = query.getResultList();
			if (objectList != null && objectList.size() > 0) {
				result = new ArrayList<>();
				for (Object[] obj : objectList) {
					result.add(Utils.getDateFormatString((Date) obj[0]) + " - " + Long.toString((long) obj[1]));
				}
			}
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in findConvertableFileCount method at "
					+ this.getClass().getName());
			throw translate("Failed to find by criteria of InterfaceFile.", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int findConvertableGroupCount() throws DAOException {
		int convertableFileCount;
		try {
			StringBuffer queryString = new StringBuffer("SELECT COUNT(c) FROM InterfaceFile c where c.isConverted = FALSE");
			Query query = em.createQuery(queryString.toString());
			convertableFileCount = Integer.parseInt(Long.toString((long) query.getSingleResult()));
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in findConvertableGroupCount method at "
					+ this.getClass().getName());
			throw translate("Failed to find by criteria of InterfaceFile.", pe);
		}
		return convertableFileCount;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public InterfaceFile findNextConvertibleFile(Date actionDate) {
		InterfaceFile interfaceFile = null;
		try {
			StringBuffer queryString = new StringBuffer(
					"SELECT c FROM InterfaceFile c where c.isConverted = FALSE AND c.importedDate >= :start AND c.importedDate <= :end ORDER BY c.id ASC");
			Query query = em.createQuery(queryString.toString());
			query.setParameter("start", Utils.resetStartDate(actionDate));
			query.setParameter("end", Utils.resetEndDate(actionDate));
			@SuppressWarnings("unchecked")
			List<InterfaceFile> list = query.setMaxResults(1).getResultList();
			if (list != null && list.size() == 1)
				interfaceFile = list.get(0);
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in findNextConvertibleFile method at "
					+ this.getClass().getName());
			throw translate("Failed to find by criteria of InterfaceFile.", pe);
		}
		return interfaceFile;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public long findCoReCountByCriteria(UniqueKey uniqueKey) throws DAOException {
		long result;
		try {
			StringBuffer queryString = new StringBuffer(" Select COUNT(c) from InterfaceFile c where " + " c.uniqueKey.batcpfx = :batcpfx AND "
					+ " c.uniqueKey.batccoy = :batccoy AND " + " c.uniqueKey.batcbrn = :batcbrn AND " + " c.uniqueKey.batcactyr = :batcactyr AND "
					+ " c.uniqueKey.batcactmn = :batcactmn ");

			if (uniqueKey.getBatctrcde() != null) {
				queryString.append(" AND c.uniqueKey.batctrcde = :batctrcde ");
			}
			if (uniqueKey.getBatcbatch() != null) {
				queryString.append(" AND c.uniqueKey.batcbatch = :batcbatch ");
			}
			if (uniqueKey.getRldgacct() != null) {
				queryString.append(" AND c.uniqueKey.rldgacct = :rldgacct ");
			}
			if (uniqueKey.getTranno() != null) {
				queryString.append(" AND c.uniqueKey.tranno = :tranno ");
			}

			// if (uniqueKey.getSacscode() != null) {
			// queryString.append(" AND c.uniqueKey.sacscode =:sacscode ");
			// }
			// sacscode for coinsurance and reinsurance which are CO and RP
			queryString.append(" AND c.uniqueKey.sacscode IN ('CO','RP') ");

			if (uniqueKey.getSacstype() != null) {
				queryString.append(" AND c.uniqueKey.sacstype =:sacstype ");
			}

			Query query = em.createQuery(queryString.toString());

			query.setParameter("batcpfx", uniqueKey.getBatcpfx());
			query.setParameter("batccoy", uniqueKey.getBatccoy());
			query.setParameter("batcbrn", uniqueKey.getBatcbrn());
			query.setParameter("batcactyr", uniqueKey.getBatcactyr());
			query.setParameter("batcactmn", uniqueKey.getBatcactmn());

			if (uniqueKey.getBatctrcde() != null) {
				query.setParameter("batctrcde", uniqueKey.getBatctrcde());
			}
			if (uniqueKey.getBatcbatch() != null) {
				query.setParameter("batcbatch", uniqueKey.getBatcbatch());
			}
			if (uniqueKey.getRldgacct() != null) {
				query.setParameter("rldgacct", uniqueKey.getRldgacct());
			}
			if (uniqueKey.getTranno() != null) {
				query.setParameter("tranno", uniqueKey.getTranno());
			}

			// if (uniqueKey.getSacscode() != null) {
			// query.setParameter("sacscode", uniqueKey.getSacscode());
			// }

			if (uniqueKey.getSacstype() != null) {
				query.setParameter("sacstype", uniqueKey.getSacstype());
			}

			result = (long) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in findCoReCountByCriteria method at "
					+ this.getClass().getName());
			throw translate("Failed to findCoReCountByCriteria.", pe);
		}
		return result;
	}

	// TODO
	// this won't fetch productCode for CN because cn doesn't have rldgacct
	// (mostly , sometimes it has rldgacct but that's not policy no , it is a
	// "payment group number")
	// possible to find rldgacct by rdocnum but in case of above "payment group
	// number" case or multi payment case , rldgacct will duplicate
	// TLDR okay for PP,S not for CN
	public String findPolicyProductCode(String rldgacct, Integer tranno) throws DAOException {
		String result = null;
		try {
			List<String> rldgacctList = new ArrayList<>();
			rldgacctList.add(rldgacct);

			StringBuffer queryString = new StringBuffer(
					" Select DISTINCT c.cnttype from InterfaceFile c where c.uniqueKey.rldgacct IN :rldgacctList AND c.cnttype IS NOT NULL ");

			if (tranno != null && tranno.intValue() > 0) {
				queryString.append(" AND c.uniqueKey.tranno = :tranno ");
			}

			Query query = em.createQuery(queryString.toString());

			query.setParameter("rldgacctList", rldgacctList);
			if (tranno != null && tranno.intValue() > 0) {
				query.setParameter("tranno", tranno);
			}

			// if (isPayment) {
			// query.setParameter("cashSacscode", SacsCode.CN.name());
			// }

			result = (String) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + pe.getClass().getName() + " in findPolicyProductCode method at "
					+ this.getClass().getName());
			throw translate("Failed to findPolicyProductCode for " + rldgacct + " - " + tranno, pe);
		}
		return result;
	}

}
