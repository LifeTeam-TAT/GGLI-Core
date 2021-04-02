/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.accounting.persistence;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.accounting.CoReStatus;
import org.ace.insurance.accounting.TLFConversionFormat;
import org.ace.insurance.accounting.UniqueKey;
import org.ace.insurance.accounting.persistence.interfaces.ITLFConversionFormatDAO;
import org.ace.insurance.accounting.service.interfaces.IInterfaceFileService;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.web.common.MessageId;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("TLFConversionFormatDAO")
public class TLFConversionFormatDAO extends BasicDAO implements ITLFConversionFormatDAO {

	private Logger logger = Logger.getLogger(this.getClass());

	@Resource(name = "InterfaceFileService")
	private IInterfaceFileService interfaceFileService;

	@Transactional(propagation = Propagation.REQUIRED)
	public TLFConversionFormat findTlfConversionFormat(UniqueKey uniqueKey, String cnttype, CoReStatus coReStatus) throws DAOException {
		TLFConversionFormat result = null;
		try {

			logger.info(MessageId.CSC_FETCH + " : " + " batctrcde " + uniqueKey.getBatctrcde() + " , batcbatch " + uniqueKey.getBatcbatch()
					+ " , rldgacct " + uniqueKey.getRldgacct() + " , tranno " + uniqueKey.getTranno() + " , sacscode " + uniqueKey.getSacscode()
					+ " , sacstype " + uniqueKey.getSacstype() + " , rdocnum " + uniqueKey.getRdocnum() + " , " + " cnttype " + cnttype
					+ " , coReStatus " + coReStatus);

			StringBuffer queryString = new StringBuffer(
					" Select c from TLFConversionFormat c where c.batctrcde = :batctrcde AND c.sacscode = :sacscode AND "
							+ " c.sacstype = :sacstype AND " + "  (c.cnttype ='ALL' OR c.cnttype = :cnttype) AND "
							+ "  (c.coReStatus =:coReStatusALL OR c.coReStatus = :coReStatus) ");
			Query query = em.createQuery(queryString.toString());

			query.setParameter("batctrcde", uniqueKey.getBatctrcde());
			query.setParameter("sacscode", uniqueKey.getSacscode());
			query.setParameter("sacstype", uniqueKey.getSacstype());
			query.setParameter("cnttype", cnttype);
			query.setParameter("coReStatusALL", CoReStatus.ALL);
			query.setParameter("coReStatus", coReStatus);

			// MUST RETURN SINGLE RESULT , MORE THAN ONE = DATA IS WRONG
			result = (TLFConversionFormat) query.getSingleResult();
			em.flush();
		} catch (NoResultException nre) {
			// try {
			// if (cnttype == null) {
			// // find cnttype of the policy by policy no
			// cnttype =
			// interfaceFileService.findPolicyProductCode(uniqueKey.getRldgacct(),
			// uniqueKey.getTranno());
			//
			// StringBuffer queryString = new StringBuffer(
			// " Select c from TLFConversionFormat c where c.batctrcde =
			// :batctrcde AND c.sacscode = :sacscode AND "
			// + " c.sacstype = :sacstype AND " + " (c.cnttype ='ALL' OR
			// c.cnttype = :cnttype) AND "
			// + " (c.coReStatus =:coReStatusALL OR c.coReStatus = :coReStatus)
			// ");
			// Query query = em.createQuery(queryString.toString());
			//
			// query.setParameter("batctrcde", uniqueKey.getBatctrcde());
			// query.setParameter("sacscode", uniqueKey.getSacscode());
			// query.setParameter("sacstype", uniqueKey.getSacstype());
			// query.setParameter("cnttype", cnttype);
			// query.setParameter("coReStatusALL", CoReStatus.ALL);
			// query.setParameter("coReStatus", coReStatus);
			//
			// // MUST RETURN SINGLE RESULT , MORE THAN ONE = DATA IS WRONG
			// result = (TLFConversionFormat) query.getSingleResult();
			// em.flush();
			// } else {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + nre.getClass().getName() + " Exception in findTlfConversionFormat method at "
					+ this.getClass().getName());
			throw translate("There is no TlfConversionFormat for " + uniqueKey.getBatctrcde() + ", " + uniqueKey.getSacscode() + ", "
					+ uniqueKey.getSacstype() + ", " + cnttype + ", " + coReStatus + " .", nre);
			// }
			// } catch (NoResultException nre2) {
			// logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " +
			// nre2.getClass().getName() + " Exception in
			// findTlfConversionFormat
			// method at "
			// + this.getClass().getName());
			// throw translate("There is no TlfConversionFormat for " +
			// uniqueKey.getBatctrcde() + ", " + uniqueKey.getSacscode() + ", "
			// + uniqueKey.getSacstype() + ", " + cnttype + ", " + coReStatus +
			// "
			// .", nre2);
			// }
		} catch (PersistenceException pe) {
			throw translate("Failed to findTlfConversionFormat.", pe);
		}
		return result;
	}
}
