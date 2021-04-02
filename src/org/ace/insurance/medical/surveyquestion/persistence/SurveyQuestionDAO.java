package org.ace.insurance.medical.surveyquestion.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.medical.productprocess.OptionType;
import org.ace.insurance.medical.productprocess.ProductProcessCriteriaDTO;
import org.ace.insurance.medical.productprocess.StudentAgeType;
import org.ace.insurance.medical.surveyquestion.ProductProcessQuestionLink;
import org.ace.insurance.medical.surveyquestion.SurveyQuestion;
import org.ace.insurance.medical.surveyquestion.persistence.interfaces.ISurveyQuestionDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/***************************************************************************************
 * @author HS
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose This class serves as the DAO to manipulate the
 *          <code>SurveyQuestion</code> object.
 * 
 * 
 ***************************************************************************************/

@Repository("SurveyQuestionDAO")
public class SurveyQuestionDAO extends BasicDAO implements ISurveyQuestionDAO {

	/**
	 * @see org.ace.insurance.medical.surveyquestion.persistence.interfaces.ISurveyQuestionDAO
	 *      #insert(org.ace.insurance.medical.surveyquestion.SurveyQuestion)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(SurveyQuestion surveyQuestion) throws DAOException {
		try {
			em.persist(surveyQuestion);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert SurveyQuestion", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.surveyquestion.persistence.interfaces.ISurveyQuestionDAO
	 *      #update(org.ace.insurance.medical.surveyquestion.SurveyQuestion)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(SurveyQuestion surveyQuestion) throws DAOException {
		try {
			em.merge(surveyQuestion);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update SurveyQuestion", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.surveyquestion.persistence.interfaces.ISurveyQuestionDAO
	 *      #delete(org.ace.insurance.medical.surveyquestion.SurveyQuestion)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(SurveyQuestion surveyQuestion) throws DAOException {
		try {
			surveyQuestion = em.merge(surveyQuestion);
			em.remove(surveyQuestion);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update SurveyQuestion", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.surveyquestion.persistence.interfaces.ISurveyQuestionDAO
	 *      #findAll()
	 * @return List of SurveyQuestion
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SurveyQuestion> findAll() throws DAOException {
		List<SurveyQuestion> result = null;
		try {
			Query q = em.createNamedQuery("SurveyQuestion.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of SurveyQuestion", pe);
		}
		return result;
	}

	/**
	 * @see org.ace.insurance.medical.surveyquestion.persistence.interfaces.ISurveyQuestionDAO
	 *      #findSurveyQuestionById(String)
	 * @return SurveyQuestion
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public SurveyQuestion findSurveyQuestionById(String surveyQuestionId) throws DAOException {
		SurveyQuestion result = null;
		try {
			Query q = em.createNamedQuery("SurveyQuestion.findById");
			q.setParameter("id", surveyQuestionId);
			result = (SurveyQuestion) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of SurveyQuestion", pe);
		}
		return result;
	}

	/**
	 * @see org.ace.insurance.medical.surveyquestion.persistence.interfaces.ISurveyQuestionDAO
	 *      #findSurveyQuestionById(String)
	 * @return List of SurveyQuestion
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductProcessQuestionLink> findPProQueByPPId(String productName, String processName,
			ProductProcessCriteriaDTO productProcessCriteriaDTO) throws DAOException {
		List<ProductProcessQuestionLink> result = null;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(
					"SELECT distinct(ppq) FROM ProductProcessQuestionLink ppq inner join ppq.productProcess pp inner join ppq.surveyQuestion sq  ");
			sb.append(
					"where pp.product.id = :productId and  pp.process.id = :processId and sq.deleteFlag = FALSE and pp.activeStatus = org.ace.insurance.medical.productprocess.ActiveStatus.ACTIVATE");

			if (productProcessCriteriaDTO.getStudentAgeType() != null) {
				sb.append(" And pp.productProcessCriteria.studentAgeType=:studentAgeType");
			}
			if (productProcessCriteriaDTO.getAge() != 0) {
				sb.append(" And pp.productProcessCriteria.minAge<=:age And pp.productProcessCriteria.maxAge>=:age");
			}
			if (productProcessCriteriaDTO.getSumInsured() != 0) {
				sb.append(" And pp.productProcessCriteria.sumInsured<=:sumInsured");
			}
			Query query = em.createQuery(sb.toString());
			query.setParameter("productId", productName);
			query.setParameter("processId", processName);

			if (productProcessCriteriaDTO.getStudentAgeType() != null) {
				query.setParameter("studentAgeType", productProcessCriteriaDTO.getStudentAgeType());
			}
			if (productProcessCriteriaDTO.getAge() != 0) {
				query.setParameter("age", productProcessCriteriaDTO.getAge());
			}
			if (productProcessCriteriaDTO.getSumInsured() != 0) {
				query.setParameter("sumInsured", productProcessCriteriaDTO.getSumInsured());
			}
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Faield to find find ProductProcessQuestionLink By Product'Id and Process'Id ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductProcessQuestionLink> findPProQueByPPIdByOptionTypeForSPCL(String productName, String processName,
			ProductProcessCriteriaDTO productProcessCriteriaDTO) throws DAOException {
		List<ProductProcessQuestionLink> result = null;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(
					"SELECT distinct(ppq) FROM ProductProcessQuestionLink ppq inner join ppq.productProcess pp inner join ppq.surveyQuestion sq  ");
			sb.append(
					"where pp.product.id = :productId and  pp.process.id = :processId and sq.deleteFlag = FALSE and pp.activeStatus = org.ace.insurance.medical.productprocess.ActiveStatus.ACTIVATE and pp.Optiontype =");
			if (productProcessCriteriaDTO.getPeriodMonth() >= 60 && productProcessCriteriaDTO.getSumInsured() >= 1000000
					&& productProcessCriteriaDTO.getAge() >= 18 && productProcessCriteriaDTO.getAge() <= 62) {
				sb.append("org.ace.insurance.medical.productprocess.OptionType." + OptionType.DECLARATION);
			} else if (productProcessCriteriaDTO.getPeriodMonth() >= 72
					&& productProcessCriteriaDTO.getPeriodMonth() <= 180
					&& productProcessCriteriaDTO.getSumInsured() >= 1000000 && productProcessCriteriaDTO.getAge() >= 40
					&& productProcessCriteriaDTO.getAge() <= 62) {
				sb.append("org.ace.insurance.medical.productprocess.OptionType." + OptionType.GP_A_OPTION_2_U_35);
			} else if (productProcessCriteriaDTO.getPeriodMonth() >= 36
					&& productProcessCriteriaDTO.getPeriodMonth() <= 180
					&& productProcessCriteriaDTO.getSumInsured() >= 1000000
					&& productProcessCriteriaDTO.getSumInsured() <= 5000000 && productProcessCriteriaDTO.getAge() >= 18
					&& productProcessCriteriaDTO.getAge() <= 62) {
				sb.append("org.ace.insurance.medical.productprocess.OptionType." + OptionType.GP_A_OPTION_2_A_35);
			} else if (productProcessCriteriaDTO.getPeriodMonth() >= 36
					&& productProcessCriteriaDTO.getPeriodMonth() <= 180
					&& productProcessCriteriaDTO.getSumInsured() >= 5000000
					&& productProcessCriteriaDTO.getSumInsured() <= 100000000
					&& productProcessCriteriaDTO.getAge() >= 18 && productProcessCriteriaDTO.getAge() <= 62) {
				sb.append("org.ace.insurance.medical.productprocess.OptionType." + OptionType.GP_B_OPTION_2_U_35);
			} else if (productProcessCriteriaDTO.getPeriodMonth() >= 36
					&& productProcessCriteriaDTO.getPeriodMonth() <= 180
					&& productProcessCriteriaDTO.getSumInsured() <= 100000000
					&& productProcessCriteriaDTO.getSumInsured() >= 200000000
					&& productProcessCriteriaDTO.getAge() >= 18 && productProcessCriteriaDTO.getAge() <= 62) {
				sb.append("org.ace.insurance.medical.productprocess.OptionType." + OptionType.GP_B_OPTION_2_A_35);
			} else if (productProcessCriteriaDTO.getPeriodMonth() >= 36
					&& productProcessCriteriaDTO.getPeriodMonth() <= 180
					&& productProcessCriteriaDTO.getSumInsured() <= 200000000
					&& productProcessCriteriaDTO.getSumInsured() >= 300000000
					&& productProcessCriteriaDTO.getAge() >= 18 && productProcessCriteriaDTO.getAge() <= 62) {
				sb.append("org.ace.insurance.medical.productprocess.OptionType." + OptionType.GP_C_OPTION_2_U_35);
			}

			Query query = em.createQuery(sb.toString());
			query.setParameter("productId", productName);
			query.setParameter("processId", processName);

			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Faield to find find ProductProcessQuestionLink By Product'Id and Process'Id ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductProcessQuestionLink> findPProQueByPPIdByOptionTypeForSTSPCL(String productName,
			String processName, ProductProcessCriteriaDTO productProcessCriteriaDTO) throws DAOException {
		List<ProductProcessQuestionLink> result = null;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(
					"SELECT distinct(ppq) FROM ProductProcessQuestionLink ppq inner join ppq.productProcess pp inner join ppq.surveyQuestion sq  ");
			sb.append(
					"where pp.product.id = :productId and  pp.process.id = :processId and sq.deleteFlag = FALSE and pp.activeStatus = org.ace.insurance.medical.productprocess.ActiveStatus.ACTIVATE and pp.Optiontype = ");
			if (productProcessCriteriaDTO.getSumInsured() >= 100000
					&& productProcessCriteriaDTO.getSumInsured() <= 5000000
					&& productProcessCriteriaDTO.getAge() >= 35) {
				sb.append("org.ace.insurance.medical.productprocess.OptionType." + OptionType.GP_B_OPTION_1_A_35);

			} else if (productProcessCriteriaDTO.getSumInsured() >= 5000000
					&& productProcessCriteriaDTO.getSumInsured() <= 10000000
					&& productProcessCriteriaDTO.getAge() >= 35) {
				sb.append("org.ace.insurance.medical.productprocess.OptionType." + OptionType.GP_B_OPTION_2_U_35);
			} else if (productProcessCriteriaDTO.getSumInsured() >= 10000000
					&& productProcessCriteriaDTO.getSumInsured() <= 30000000 && productProcessCriteriaDTO.getAge() >= 18
					&& productProcessCriteriaDTO.getAge() <= 64) {
				sb.append("org.ace.insurance.medical.productprocess.OptionType." + OptionType.GP_B_OPTION_2_A_35);
			} else if (productProcessCriteriaDTO.getSumInsured() >= 30000000
					&& productProcessCriteriaDTO.getSumInsured() <= 50000000 && productProcessCriteriaDTO.getAge() >= 18
					&& productProcessCriteriaDTO.getAge() <= 64) {
				sb.append("org.ace.insurance.medical.productprocess.OptionType." + OptionType.GP_C_OPTION_2_U_35);
			} else if (productProcessCriteriaDTO.getSumInsured() >= 50000000
					&& productProcessCriteriaDTO.getSumInsured() <= 100000000
					&& productProcessCriteriaDTO.getAge() >= 18 && productProcessCriteriaDTO.getAge() <= 64) {
				sb.append("org.ace.insurance.medical.productprocess.OptionType." + OptionType.GP_D_OPTION_2_U_35);
			}

			Query query = em.createQuery(sb.toString());
			query.setParameter("productId", productName);
			query.setParameter("processId", processName);

			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Faield to find find ProductProcessQuestionLink By Product'Id and Process'Id ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductProcessQuestionLink> findMedicalPProQueByPPIdMedical(String productName, String processName,
			StudentAgeType studentAgeType) throws DAOException {
		List<ProductProcessQuestionLink> result = null;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(
					"SELECT distinct(ppq) FROM ProductProcessQuestionLink ppq inner join ppq.productProcess pp inner join ppq.surveyQuestion sq  ");
			sb.append(
					"where pp.product.id = :productId and  pp.process.id = :processId and sq.deleteFlag = FALSE and pp.activeStatus = org.ace.insurance.medical.productprocess.ActiveStatus.ACTIVATE");
			if (studentAgeType != null) {
				sb.append(" And pp.studentAgeType=:studentAgeType");
			}
			Query query = em.createQuery(sb.toString());
			query.setParameter("productId", productName);
			query.setParameter("processId", processName);
			if (studentAgeType != null) {
				query.setParameter("studentAgeType", studentAgeType);
			}
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Faield to find find ProductProcessQuestionLink By Product'Id and Process'Id ", pe);
		}
		return result;
	}

	/**
	 * @see org.ace.insurance.medical.surveyquestion.persistence.interfaces.ISurveyQuestionDAO
	 *      #findSurveyQuestionBypp(String)
	 * @return List of SurveyQuestion
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SurveyQuestion> findSurveyQuestionBypp(String productProcessId) throws DAOException {
		List<SurveyQuestion> result = null;
		try {
			Query query = em.createQuery(
					" SELECT que FROM SurveyQuestion que INNER JOIN que.productProcessQuestionLinkList ppq WHERE ppq.productProcess.id = :ppId ");
			query.setParameter("ppId", productProcessId);
			result = query.getResultList();
		} catch (PersistenceException pe) {
			throw translate("Faield to find find ProductProcessQuestionLink By ProductProcess'Id ", pe);
		}
		return result;
	}

}
