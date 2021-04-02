package org.ace.insurance.proxy.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.snakebite.SnakeBitePolicyDTO;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.proxy.CGO001;
import org.ace.insurance.proxy.CIN001;
import org.ace.insurance.proxy.CIN002;
import org.ace.insurance.proxy.GF001;
import org.ace.insurance.proxy.LCB001;
import org.ace.insurance.proxy.LCLD001;
import org.ace.insurance.proxy.LCP001;
import org.ace.insurance.proxy.LIF001;
import org.ace.insurance.proxy.LPP001;
import org.ace.insurance.proxy.LSP001;
import org.ace.insurance.proxy.MCL001;
import org.ace.insurance.proxy.MED001;
import org.ace.insurance.proxy.MEDCLM002;
import org.ace.insurance.proxy.MEDICAL002;
import org.ace.insurance.proxy.TRA001;
import org.ace.insurance.proxy.WorkflowCriteria;
import org.ace.insurance.proxy.persistence.interfaces.IProxyDAO;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.staff.Staff;
import org.ace.insurance.web.manage.agent.payment.AgentCommissionDTO;
import org.ace.insurance.web.manage.agent.payment.StaffCommissionDTO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unchecked")
@Repository("ProxyDAO")
public class ProxyDAO extends BasicDAO implements IProxyDAO {
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<CGO001> find_CGO001(WorkflowCriteria wfc) throws DAOException {
		List<CGO001> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW org.ace.insurance.proxy.CGO001");
			buffer.append("(f.id, f.proposalNo, c.initialId, c.name, o.name, f.submittedDate, w.createdDate, f.currency.currencyCode, SUM(fp.proposedSumInsured))");
			buffer.append(" FROM CargoProposal  f");
			buffer.append(" LEFT OUTER JOIN f.customer c ");
			buffer.append(" LEFT OUTER JOIN f.organization o ");
			buffer.append(" JOIN f.shipVehicleInfoProposalList fb JOIN fb.cargoProposalProductInfoList fp ");
			buffer.append(", WorkFlow w ");
			buffer.append(" WHERE f.id = w.referenceNo AND  ");
			if (wfc.getProposalType() != null) {
				buffer.append(" f.proposalType =:proposalType AND ");
			}
			if (wfc.getResponsiblePerson() != null) {
				buffer.append(" w.responsiblePerson.id = :responsiblePersonId AND ");
			}
			buffer.append(" w.workflowTask = :workflowTask AND w.referenceType = :referenceType  ");
			buffer.append(" GROUP BY f.id, f.proposalNo, c.initialId, c.name, o.name,f.submittedDate, w.createdDate ,f.currency.currencyCode ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			if (wfc.getProposalType() != null) {
				query.setParameter("proposalType", wfc.getProposalType());
			}
			if (wfc.getResponsiblePerson() != null) {
				query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			}

			result = query.getResultList();
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find CGO001 by WorkflowCriteria", pe);
		}
		RegNoSorter<CGO001> regNoSorter = new RegNoSorter<CGO001>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LIF001> find_LIF001(WorkflowCriteria wfc) throws DAOException {
		List<LIF001> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW org.ace.insurance.proxy.LIF001");
			buffer.append("(l.id, l.proposalNo, c.initialId, c.name, o.name, l.submittedDate, w.createdDate, SUM(ip.proposedSumInsured))");
			buffer.append(" FROM LifeProposal l ");
			buffer.append(" LEFT OUTER JOIN l.customer c ");
			buffer.append(" LEFT OUTER JOIN l.organization o ");
			buffer.append(" JOIN l.proposalInsuredPersonList ip ");
			buffer.append(", WorkFlow w ");
			buffer.append(" WHERE l.id = w.referenceNo AND ");
			if (wfc.getProposalType() != null) {
				buffer.append(" l.proposalType=:proposalType AND ");
			}
			if (wfc.getResponsiblePerson() != null) {
				buffer.append(" w.responsiblePerson.id = :responsiblePersonId AND ");
			}

			buffer.append(" w.workflowTask = :workflowTask AND w.referenceType = :referenceType ");
			buffer.append(" GROUP BY l.id, l.proposalNo, c.initialId, c.name, o.name, l.submittedDate, w.createdDate ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			if (wfc.getProposalType() != null) {
				query.setParameter("proposalType", wfc.getProposalType());
			}
			if (wfc.getResponsiblePerson() != null) {
				query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			}

			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LIF001 by WorkflowCriteria", pe);
		}
		// RegNoSorter<LIF001> regNoSorter = new RegNoSorter<LIF001>(result);
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MED001> find_MED001(WorkflowCriteria wfc) throws DAOException {
		List<MED001> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT m.id, m.proposalNo, c.initialId, c.name, m.submittedDate, w.createdDate, SUM( p.unit) , SUM(p.basicPlusUnit) ");
			buffer.append(" FROM MedicalProposal m LEFT OUTER JOIN m.customer c JOIN m.medicalProposalInsuredPersonList p  , WorkFlow w ");
			buffer.append(" WHERE m.id = w.referenceNo");
			if (wfc.getProposalType() != null) {
				buffer.append(" AND m.proposalType =:proposalType");
			}
			if (wfc.getResponsiblePerson() != null) {
				buffer.append(" AND w.responsiblePerson.id = :responsiblePersonId");
			}
			buffer.append(" AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType");
			buffer.append(" GROUP BY m.id, m.proposalNo, c.initialId, c.name, m.submittedDate, w.createdDate");
			Query query = em.createQuery(buffer.toString());
			if (wfc.getProposalType() != null) {
				query.setParameter("proposalType", wfc.getProposalType());
			}
			if (wfc.getResponsiblePerson() != null) {
				query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			}
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			List<Object> objList = query.getResultList();
			result = new ArrayList<MED001>();
			String id = null;
			String proposalNo = null;
			String customerName = null;
			Date submittedDate = null;
			Date createdDate = null;
			int unit = 0;
			int basicPlusUnit = 0;
			int totalUnit = 0;
			String initialId = "";
			Name name = null;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				proposalNo = (String) objArray[1];
				if (objArray[2] != null) {
					initialId = (String) objArray[2];
				}

				name = (Name) objArray[3];
				customerName = initialId;
				if (null != name) {
					customerName += name.getFullName();
				}
				submittedDate = (Date) objArray[4];
				createdDate = (Date) objArray[5];
				unit = ((Long) objArray[6]).intValue();
				basicPlusUnit = ((Long) objArray[7]).intValue();
				totalUnit = unit + basicPlusUnit;
				result.add(new MED001(id, proposalNo, customerName, submittedDate, createdDate, totalUnit));
			}
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find MED001 by WorkflowCriteria", pe);
		}
		RegNoSorter<MED001> regNoSorter = new RegNoSorter<MED001>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MCL001> findDamagedVehicleClaim(WorkflowCriteria wfc) throws DAOException {
		List<MCL001> result = null;
		try {
			StringBuffer buffer = new StringBuffer();

			// 1. Join with MotorPolicy
			buffer.append(
					"SELECT dv.id, dv.claim.claimReferenceNo, dv.claim.policy.policyNo, dv.claim.policy.customer, dv.claim.policy.organization, dv.claim.submittedDate, w.createdDate, dv.damageValue, dv.approvedValue , dv.claim.policy.currency.currencyCode"
							+ " FROM DamagedVehicle dv, WorkFlow w" + " WHERE dv.id = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
							+ " AND w.responsiblePerson.id = :responsiblePersonId");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			List<Object> objList = query.getResultList();
			em.flush();
			result = new ArrayList<MCL001>();
			String id = null;
			String claimReferenceNo = null;
			String policyNo = null;
			String customerName = null;
			Customer customer = null;
			Organization organization = null;

			Date submittedDate = null;
			Date createdDate = null;
			double damageValue = 0.0;
			double approvedValue = 0.0;
			String cur = null;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				claimReferenceNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				customerName = null;
				customer = (Customer) objArray[3];
				organization = (Organization) objArray[4];
				if (customer != null) {
					customerName = customer.getFullName();
				} else {
					customerName = organization.getName();
				}
				submittedDate = (Date) objArray[5];
				createdDate = (Date) objArray[6];
				damageValue = (Double) objArray[7];
				approvedValue = (Double) objArray[8];
				cur = (String) objArray[9];
				result.add(new MCL001(id, claimReferenceNo, policyNo, customerName, null, submittedDate, createdDate, damageValue, approvedValue, cur));
			}

			// 2. Join with MotorPolicyHistory
			buffer = new StringBuffer();
			buffer.append("SELECT dv.id, dv.claim.claimReferenceNo, dv.claim.policyHistory.policyNo, dv.claim.policyHistory.customer, "
					+ " dv.claim.policyHistory.organization, dv.claim.submittedDate, w.createdDate, dv.damageValue, dv.approvedValue ,dv.claim.policyHistory.currency.currencyCode"
					+ " FROM DamagedVehicle dv, WorkFlow w WHERE dv.id = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
					+ " AND w.responsiblePerson.id = :responsiblePersonId");
			query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			objList = query.getResultList();
			em.flush();
			String id1 = null;
			String claimReferenceNo1 = null;
			String policyNo1 = null;
			String customerName1 = null;
			Customer customer1 = null;
			Organization organization1 = null;
			Date submittedDate1 = null;
			Date createdDate1 = null;
			double damageValue1 = 0.0;
			double approvedValue1 = 0.0;
			String cur1 = null;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id1 = (String) objArray[0];
				claimReferenceNo1 = (String) objArray[1];
				policyNo1 = (String) objArray[2];
				customerName1 = null;
				customer1 = (Customer) objArray[3];
				organization1 = (Organization) objArray[4];
				if (customer1 != null) {
					customerName1 = customer1.getFullName();
				} else {
					customerName1 = organization1.getName();
				}
				submittedDate1 = (Date) objArray[5];
				createdDate1 = (Date) objArray[6];
				damageValue1 = (Double) objArray[7];
				approvedValue1 = (Double) objArray[8];
				cur1 = (String) objArray[9];
				result.add(new MCL001(id1, claimReferenceNo1, policyNo1, customerName1, null, submittedDate1, createdDate1, damageValue1, approvedValue1, cur1));
			}

		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Damage Vehicle for dashBoard by WorkflowCriteria", pe);
		}
		RegNoSorter<MCL001> regNoSorter = new RegNoSorter<MCL001>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MCL001> findWindScreenClaim(WorkflowCriteria wfc) throws DAOException {
		List<MCL001> result = null;
		try {
			// 1. Join with MotorPolicy
			StringBuffer buffer = new StringBuffer();
			buffer.append(
					"SELECT p.id, p.claim.claimReferenceNo, p.claim.policy.policyNo, p.claim.policy.customer, p.claim.policy.organization,  p.claim.submittedDate, w.createdDate, p.damageValue, p.approvedValue ,p.claim.policy.currency.currencyCode"
							+ " FROM WindScreen p , WorkFlow w" + " WHERE p.id = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
							+ " AND w.responsiblePerson.id = :responsiblePersonId");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			List<Object> objList = query.getResultList();
			em.flush();
			result = new ArrayList<MCL001>();
			String id = null;
			String claimReferenceNo = null;
			String policyNo = null;
			String customerName = null;
			Customer customer = null;
			Organization organization = null;
			Date submittedDate = null;
			Date createdDate = null;
			double claimAmount = 0.0;
			double approvedValue = 0.0;
			String cur = null;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				claimReferenceNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				customerName = null;
				customer = (Customer) objArray[3];
				organization = (Organization) objArray[4];
				if (customer != null) {
					customerName = customer.getFullName();
				} else {
					customerName = organization.getName();
				}
				submittedDate = (Date) objArray[5];
				createdDate = (Date) objArray[6];
				claimAmount = (Double) objArray[7];
				approvedValue = (Double) objArray[8];
				cur = (String) objArray[9];
				result.add(new MCL001(id, claimReferenceNo, policyNo, customerName, null, submittedDate, createdDate, claimAmount, approvedValue, cur));
			}

			// 2. Join with MotorPolicy History
			buffer = new StringBuffer();
			buffer.append("SELECT p.id, p.claim.claimReferenceNo, p.claim.policyHistory.policyNo, p.claim.policyHistory.customer, "
					+ "	p.claim.policyHistory.organization,  p.claim.submittedDate, w.createdDate, p.damageValue, p.approvedValue , p.claim.policyHistory.currency.currencyCode"
					+ " FROM WindScreen p , WorkFlow w WHERE p.id = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
					+ " AND w.responsiblePerson.id = :responsiblePersonId");
			query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			objList = query.getResultList();
			em.flush();
			String id1 = null;
			String claimReferenceNo1 = null;
			String policyNo1 = null;
			String customerName1 = null;
			Customer customer1 = null;
			Organization organization1 = null;
			Date submittedDate1 = null;
			Date createdDate1 = null;
			double claimAmount1 = 0.0;
			double approvedValue1 = 0.0;
			String cur1 = null;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id1 = (String) objArray[0];
				claimReferenceNo1 = (String) objArray[1];
				policyNo1 = (String) objArray[2];
				customerName1 = null;
				customer1 = (Customer) objArray[3];
				organization1 = (Organization) objArray[4];
				if (customer1 != null) {
					customerName1 = customer1.getFullName();
				} else {
					customerName1 = organization1.getName();
				}
				submittedDate1 = (Date) objArray[5];
				createdDate1 = (Date) objArray[6];
				claimAmount1 = (Double) objArray[7];
				approvedValue1 = (Double) objArray[8];
				cur1 = (String) objArray[9];
				result.add(new MCL001(id1, claimReferenceNo1, policyNo1, customerName1, null, submittedDate1, createdDate1, claimAmount1, approvedValue1, cur1));
			}
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find WindScreen Claim by WorkflowCriteria", pe);
		}
		RegNoSorter<MCL001> regNoSorter = new RegNoSorter<MCL001>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MCL001> findTowingClaim(WorkflowCriteria wfc) throws DAOException {
		List<MCL001> result = null;
		try {
			// 1. Join with MotorPolicy
			StringBuffer buffer = new StringBuffer();
			buffer.append(
					"SELECT d.id, d.claim.claimReferenceNo, d.claim.policy.policyNo, d.claim.policy.customer, d.claim.policy.organization, d.claim.submittedDate, w.createdDate, d.towigCharges, d.approvedValue, d.claim.policy.currency.currencyCode "
							+ " FROM Towing d, WorkFlow w WHERE d.id = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
							+ " AND w.responsiblePerson.id = :responsiblePersonId");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			List<Object> objList = query.getResultList();
			em.flush();
			result = new ArrayList<MCL001>();
			String id = null;
			String claimReferenceNo = null;
			String policyNo = null;
			String customerName = null;
			Customer customer = null;
			Organization organization = null;
			Date submittedDate = null;
			Date createdDate = null;
			double towingCharges = 0.0;
			double approvedValue = 0.0;
			String cur = null;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				claimReferenceNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				customerName = null;
				customer = (Customer) objArray[3];
				organization = (Organization) objArray[4];
				if (customer != null) {
					customerName = customer.getFullName();
				} else {
					customerName = organization.getName();
				}
				submittedDate = (Date) objArray[5];
				createdDate = (Date) objArray[6];
				towingCharges = (Double) objArray[7];
				approvedValue = (Double) objArray[8];
				cur = (String) objArray[9];
				result.add(new MCL001(id, claimReferenceNo, policyNo, customerName, null, submittedDate, createdDate, towingCharges, approvedValue, cur));
			}

			// 2. Join with MotorPolicy History
			buffer = new StringBuffer();
			buffer.append("SELECT d.id, d.claim.claimReferenceNo, d.claim.policyHistory.policyNo, d.claim.policyHistory.customer, "
					+ " d.claim.policyHistory.organization, d.claim.submittedDate, w.createdDate, d.towigCharges, d.approvedValue, d.claim.policyHistory.currency.currencyCode "
					+ " FROM Towing d, WorkFlow w" + " WHERE d.id = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
					+ " AND w.responsiblePerson.id = :responsiblePersonId");
			query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			objList = query.getResultList();
			em.flush();
			String id1 = null;
			String claimReferenceNo1 = null;
			String policyNo1 = null;
			String customerName1 = null;
			Customer customer1 = null;
			Organization organization1 = null;
			Date submittedDate1 = null;
			Date createdDate1 = null;
			double towingCharges1 = 0.0;
			double approvedValue1 = 0.0;
			String cur1 = null;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id1 = (String) objArray[0];
				claimReferenceNo1 = (String) objArray[1];
				policyNo1 = (String) objArray[2];
				customerName1 = null;
				customer1 = (Customer) objArray[3];
				organization1 = (Organization) objArray[4];
				if (customer1 != null) {
					customerName1 = customer1.getFullName();
				} else {
					customerName1 = organization1.getName();
				}
				submittedDate1 = (Date) objArray[5];
				createdDate1 = (Date) objArray[6];
				towingCharges1 = (Double) objArray[7];
				approvedValue1 = (Double) objArray[8];
				cur1 = (String) objArray[9];
				result.add(new MCL001(id1, claimReferenceNo1, policyNo1, customerName1, null, submittedDate1, createdDate1, towingCharges1, approvedValue1, cur1));
			}
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Towing Claim by WorkflowCriteria", pe);
		}
		RegNoSorter<MCL001> regNoSorter = new RegNoSorter<MCL001>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MCL001> findThirdPartyPropertyClaim(WorkflowCriteria wfc) throws DAOException {
		List<MCL001> result = null;
		try {
			// 1. Join with MotorPolicy
			StringBuffer buffer = new StringBuffer();
			buffer.append(
					"SELECT d.id, d.claim.claimReferenceNo, d.claim.policy.policyNo, d.claim.policy.customer, d.claim.policy.organization, d.claim.submittedDate, w.createdDate, d.damageValue, d.approvedValue,"
							+ "  d.claim.policy.currency.currencyCode , d.otherPart, d.vehiclePart "
							+ " FROM ThirdPartyProperty d, WorkFlow w WHERE d.id = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
							+ " AND w.responsiblePerson.id = :responsiblePersonId");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			String id = null;
			String claimReferenceNo = null;
			String policyNo = null;
			String customerName = null;
			Customer customer = null;
			Organization organization = null;
			Date submittedDate = null;
			Date createdDate = null;
			double damageValue = 0;
			double approvedValue = 0;
			String cur = null;
			String otherPart = null;
			String partName = null;

			List<Object> objList = query.getResultList();
			em.flush();
			result = new ArrayList<MCL001>();
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				claimReferenceNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				customerName = null;
				customer = (Customer) objArray[3];
				organization = (Organization) objArray[4];
				if (customer != null) {
					customerName = customer.getFullName();
				} else {
					customerName = organization.getName();
				}
				submittedDate = (Date) objArray[5];
				createdDate = (Date) objArray[6];
				damageValue = (Double) objArray[7];
				approvedValue = (Double) objArray[8];
				cur = (String) objArray[9];
				otherPart = (String) objArray[10];
				result.add(new MCL001(id, claimReferenceNo, policyNo, customerName, null, submittedDate, createdDate, damageValue, approvedValue, cur, partName));
			}

			// 2. Join with MotorPolicy History
			buffer = new StringBuffer();
			buffer.append("SELECT d.id, d.claim.claimReferenceNo, d.claim.policyHistory.policyNo, d.claim.policyHistory.customer, "
					+ " d.claim.policyHistory.organization, d.claim.submittedDate, w.createdDate, d.damageValue, d.approvedValue, "
					+ " d.claim.policyHistory.currency.currencyCode, d.otherPart, d.vehiclePart FROM ThirdPartyProperty d, WorkFlow w"
					+ " WHERE d.id = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType" + " AND w.responsiblePerson.id = :responsiblePersonId");
			query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			objList = query.getResultList();
			em.flush();
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				claimReferenceNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				customerName = null;
				customer = (Customer) objArray[3];
				organization = (Organization) objArray[4];
				if (customer != null) {
					customerName = customer.getFullName();
				} else {
					customerName = organization.getName();
				}
				submittedDate = (Date) objArray[5];
				createdDate = (Date) objArray[6];
				damageValue = (Double) objArray[7];
				approvedValue = (Double) objArray[8];
				cur = (String) objArray[9];
				otherPart = (String) objArray[10];
				result.add(new MCL001(id, claimReferenceNo, policyNo, customerName, null, submittedDate, createdDate, damageValue, approvedValue, cur, partName));
			}
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find ThardPartyProperty Claim by WorkflowCriteria", pe);
		}
		RegNoSorter<MCL001> regNoSorter = new RegNoSorter<MCL001>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MCL001> findPersonCasualtyClaim(WorkflowCriteria wfc) throws DAOException {
		List<MCL001> result = null;
		try {
			// 1. Join with MotorPolicy
			StringBuffer buffer = new StringBuffer();
			buffer.append(
					"SELECT d.id, d.claim.claimReferenceNo, d.claim.policy.policyNo, d.claim.policy.customer, d.claim.policy.organization, d.name, d.claim.submittedDate, w.createdDate, d.claimedAmount, d.approvedValue, d.claim.policy.currency.currencyCode "
							+ " FROM PersonCasualty d, WorkFlow w WHERE d.id = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
							+ " AND w.responsiblePerson.id = :responsiblePersonId");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			List<Object> objList = query.getResultList();
			em.flush();
			result = new ArrayList<MCL001>();
			String id = null;
			String claimReferenceNo = null;
			String policyNo = null;
			String customerName = null;
			Customer customer = null;
			Organization organization = null;

			String personCasualtyName = null;
			Date submittedDate = null;
			Date createdDate = null;
			double claimedAmount = 0.0;
			double approvedAmount = 0.0;
			String cur = null;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				claimReferenceNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				customerName = null;
				customer = (Customer) objArray[3];
				organization = (Organization) objArray[4];
				if (customer != null) {
					customerName = customer.getFullName();
				} else {
					customerName = organization.getName();
				}
				personCasualtyName = (String) objArray[5];
				submittedDate = (Date) objArray[6];
				createdDate = (Date) objArray[7];
				claimedAmount = (Double) objArray[8];
				approvedAmount = (Double) objArray[9];
				cur = (String) objArray[10];
				result.add(new MCL001(id, claimReferenceNo, policyNo, customerName, personCasualtyName, submittedDate, createdDate, claimedAmount, approvedAmount, cur));
			}

			// 2. Join with MotorPolicy History
			buffer = new StringBuffer();
			buffer.append("SELECT d.id, d.claim.claimReferenceNo, d.claim.policyHistory.policyNo, d.claim.policyHistory.customer, "
					+ " d.claim.policyHistory.organization, d.name, d.claim.submittedDate, w.createdDate, d.claimedAmount, d.approvedValue, d.claim.policyHistory.currency.currencyCode "
					+ " FROM PersonCasualty d, WorkFlow w WHERE d.id = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
					+ " AND w.responsiblePerson.id = :responsiblePersonId");
			query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			objList = query.getResultList();
			em.flush();
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				claimReferenceNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				customerName = null;
				customer = (Customer) objArray[3];
				organization = (Organization) objArray[4];
				if (customer != null) {
					customerName = customer.getFullName();
				} else {
					customerName = organization.getName();
				}
				personCasualtyName = (String) objArray[5];
				submittedDate = (Date) objArray[6];
				createdDate = (Date) objArray[7];
				claimedAmount = (Double) objArray[8];
				approvedAmount = (Double) objArray[9];
				cur = (String) objArray[10];
				result.add(new MCL001(id, claimReferenceNo, policyNo, customerName, personCasualtyName, submittedDate, createdDate, claimedAmount, approvedAmount, cur));
			}
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find PersonCasualty Claim by WorkflowCriteria", pe);
		}
		RegNoSorter<MCL001> regNoSorter = new RegNoSorter<MCL001>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MCL001> findMedicalExpenseClaim(WorkflowCriteria wfc) throws DAOException {
		List<MCL001> result = null;
		try {
			// 1. Join with MotorPolicy
			StringBuffer buffer = new StringBuffer();
			buffer.append(
					"SELECT d.id, d.claim.claimReferenceNo, d.claim.policy.policyNo, d.claim.policy.customer, d.claim.policy.organization, d.claim.submittedDate, w.createdDate, d.expenseAmount, d.approvedValue, d.claim.policy.currency.currencyCode "
							+ " FROM MedicalExpense d, WorkFlow w WHERE d.id = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
							+ " AND w.responsiblePerson.id = :responsiblePersonId");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			List<Object> objList = query.getResultList();
			em.flush();
			result = new ArrayList<MCL001>();
			String id = null;
			String claimReferenceNo = null;
			String policyNo = null;
			String customerName = null;
			Customer customer = null;
			Organization organization = null;
			Date submittedDate = null;
			Date createdDate = null;
			double claimedAmount = 0.0;
			double approvedAmount = 0.0;
			double expenseAmount = 0.0;
			String cur = null;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				claimReferenceNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				customerName = null;
				customer = (Customer) objArray[3];
				organization = (Organization) objArray[4];
				if (customer != null) {
					customerName = customer.getFullName();
				} else {
					customerName = organization.getName();
				}
				submittedDate = (Date) objArray[5];
				createdDate = (Date) objArray[6];
				claimedAmount = (Double) objArray[7];
				approvedAmount = (Double) objArray[8];
				cur = (String) objArray[9];
				result.add(new MCL001(id, claimReferenceNo, policyNo, customerName, null, submittedDate, createdDate, claimedAmount, approvedAmount, cur));
			}

			// 2. Join with MotorPolicy History
			buffer = new StringBuffer();
			buffer.append("SELECT d.id, d.claim.claimReferenceNo, d.claim.policyHistory.policyNo, d.claim.policyHistory.customer, "
					+ " d.claim.policyHistory.organization, d.claim.submittedDate, w.createdDate, d.expenseAmount, d.approvedValue, d.claim.policyHistory.currency.currencyCode "
					+ " FROM MedicalExpense d, WorkFlow w WHERE d.id = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
					+ " AND w.responsiblePerson.id = :responsiblePersonId");
			query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			objList = query.getResultList();
			em.flush();
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				claimReferenceNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				customerName = null;
				customer = (Customer) objArray[3];
				organization = (Organization) objArray[4];
				if (customer != null) {
					customerName = customer.getFullName();
				} else {
					customerName = organization.getName();
				}
				submittedDate = (Date) objArray[5];
				createdDate = (Date) objArray[6];
				expenseAmount = (Double) objArray[7];
				approvedAmount = (Double) objArray[8];
				cur = (String) objArray[9];
				result.add(new MCL001(id, claimReferenceNo, policyNo, customerName, null, submittedDate, createdDate, expenseAmount, approvedAmount, cur));
			}
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find MedicalExpense Claim by WorkflowCriteria", pe);
		}
		RegNoSorter<MCL001> regNoSorter = new RegNoSorter<MCL001>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LCLD001> find_LCLD001(WorkflowCriteria wfc) throws DAOException {
		List<LCLD001> result = null;
		try {
			String claim = null;
			if (WorkflowReferenceType.LIFE_CLAIM.equals(wfc.getReferenceType())) {
				claim = "LifeClaim";
			} else {
				claim = "LifeDisabilityClaim";
			}
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT lc.id, lc.claimRequestId, lc.submittedDate, w.createdDate, lc.lifePolicy " + " FROM " + claim + " lc, WorkFlow w"
					+ " WHERE lc.claimRequestId = w.referenceNo AND w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
					+ " AND w.responsiblePerson.id = :responsiblePersonId");

			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			List<Object> objList = query.getResultList();
			em.flush();
			result = new ArrayList<LCLD001>();
			String id = null;
			String claimRequestId = null;
			Date submittedDate = null;
			Date pendingSince = null;
			LifePolicy lifePolicy = null;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				claimRequestId = (String) objArray[1];
				submittedDate = (Date) objArray[2];
				pendingSince = (Date) objArray[3];
				lifePolicy = (LifePolicy) objArray[4];
				result.add(new LCLD001(id, claimRequestId, lifePolicy.getCustomerName(), submittedDate, pendingSince, lifePolicy.getTotalSumInsured()));
			}
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find DamagedOther for LCLD001 by WorkflowCriteria", pe);
		}
		RegNoSorter<LCLD001> regNoSorter = new RegNoSorter<LCLD001>(result);
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LCB001> find_LCB001(WorkflowCriteria wfc) throws DAOException {
		List<LCB001> result = null;
		try {
			String beneficiary = null;
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT lc.id, lc.refundNo , lc.claimAmount, w.createdDate,");
			if (WorkflowReferenceType.LIFE_CLAIM.equals(wfc.getReferenceType())) {
				beneficiary = "CONCAT(lc.policyInsuredPersonBeneficiaries.initialId , lc.policyInsuredPersonBeneficiaries.name.firstName ,lc.policyInsuredPersonBeneficiaries.name.middleName , lc.policyInsuredPersonBeneficiaries.name.lastName), lc.policyInsuredPersonBeneficiaries.policyInsuredPerson.sumInsured From LifeClaimInsuredPersonBeneficiary lc";
			} else {
				beneficiary = "CONCAT(lc.policyInsuredPerson.initialId , lc.policyInsuredPerson.name.firstName ,lc.policyInsuredPerson.name.middleName , lc.policyInsuredPerson.name.lastName) ,lc.policyInsuredPerson.sumInsured From LifeClaimInsuredPerson lc";
			}
			buffer.append(beneficiary + ", WorkFlow w " + " WHERE lc.refundNo = w.referenceNo AND  w.workflowTask = :workflowTask AND w.referenceType = :referenceType"
					+ " AND w.responsiblePerson.id = :responsiblePersonId");

			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			List<Object> objList = query.getResultList();
			em.flush();
			result = new ArrayList<LCB001>();
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				String id = (String) objArray[0];
				String refundNo = (String) objArray[1];
				double claimAmount = (Double) objArray[2];
				Date pendingSince = (Date) objArray[3];
				String beneficiaryName = (String) objArray[4].toString();
				double sumInsured = (Double) objArray[5];

				result.add(new LCB001(id, refundNo, beneficiaryName, pendingSince, claimAmount, sumInsured));
			}
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find DamagedOther for LCB001 by WorkflowCriteria", pe);
		}
		RegNoSorter<LCB001> regNoSorter = new RegNoSorter<LCB001>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<CIN001> find_CIN001(WorkflowReferenceType referenceType, CoinsuranceType coinsuranceType, WorkflowTask workFlowTask, String userId) throws DAOException {
		List<CIN001> result = new ArrayList<CIN001>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT c.id, c.invoiceNo, c.policyNo, co.name, c.invoiceDate, c.sumInsuranced, ");
			buffer.append(" c.commissionPercent, c.premium, c.coinsuranceNo, c.receivedSumInsured ");
			buffer.append(" FROM Coinsurance c LEFT JOIN c.coinsuranceCompany co, WorkFlow w ");
			buffer.append(" WHERE c.id = w.referenceNo AND c.coinsuranceType = :coinsuranceType AND w.workflowTask = :workflowTask ");
			buffer.append(" AND w.referenceType = :referenceType AND w.responsiblePerson.id = :responsiblePersonId ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("coinsuranceType", coinsuranceType);
			query.setParameter("workflowTask", workFlowTask);
			query.setParameter("referenceType", referenceType);
			query.setParameter("responsiblePersonId", userId);
			List<Object> objList = query.getResultList();
			String id;
			String invoiceNo;
			String policyNo;
			String companyName;
			Date invoiceDate;
			double sumInsuranced;
			double commissionPercent;
			double premium;
			double netPremium;
			String coinsuranceNo;
			double receivedSumInsured;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				invoiceNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				companyName = (String) objArray[3];
				invoiceDate = (Date) objArray[4];
				sumInsuranced = (Double) objArray[5];
				commissionPercent = (Double) objArray[6];
				premium = (Double) objArray[7];
				netPremium = premium - Utils.getPercentOf(commissionPercent, premium);
				coinsuranceNo = (String) objArray[8];
				receivedSumInsured = (Double) objArray[9];
				result.add(new CIN001(id, invoiceNo, policyNo, companyName, invoiceDate, sumInsuranced, premium, netPremium, coinsuranceNo, receivedSumInsured));
			}
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find CIN001 by WorkflowCriteria", pe);
		}
		RegNoSorter<CIN001> regNoSorter = new RegNoSorter<CIN001>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<CIN002> find_CIN002(WorkflowReferenceType referenceType, CoinsuranceType coinsuranceType, WorkflowTask workFlowTask, String userId) throws DAOException {
		List<CIN002> result = new ArrayList<CIN002>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT c.id, c.invoiceNo, c.policyNo, co.name, c.invoiceDate, c.sumInsuranced, ");
			buffer.append(" c.commissionPercent, c.premium, c.coinsuranceNo, c.receivedSumInsured ");
			buffer.append(" FROM Coinsurance c LEFT JOIN c.coinsuranceCompany co, WorkFlow w ");
			buffer.append(" WHERE c.id = w.referenceNo AND c.coinsuranceType = :coinsuranceType AND w.workflowTask = :workflowTask ");
			buffer.append(" AND w.referenceType = :referenceType AND w.responsiblePerson.id = :responsiblePersonId ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("coinsuranceType", coinsuranceType);
			query.setParameter("workflowTask", workFlowTask);
			query.setParameter("referenceType", referenceType);
			query.setParameter("responsiblePersonId", userId);
			List<Object> objList = query.getResultList();
			String id;
			String invoiceNo;
			String policyNo;
			String companyName;
			Date invoiceDate;
			double sumInsuranced;
			double commissionPercent;
			double premium;
			double netPremium;
			String coinsuranceNo;
			double receivedSumInsured;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				invoiceNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				companyName = (String) objArray[3];
				invoiceDate = (Date) objArray[4];
				sumInsuranced = (Double) objArray[5];
				commissionPercent = (Double) objArray[6];
				premium = (Double) objArray[7];
				netPremium = premium - Utils.getPercentOf(commissionPercent, premium);
				coinsuranceNo = (String) objArray[8];
				receivedSumInsured = (Double) objArray[9];
				result.add(new CIN002(id, invoiceNo, policyNo, companyName, invoiceDate, sumInsuranced, premium, netPremium, coinsuranceNo, receivedSumInsured));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find CIN001 by WorkflowCriteria", pe);
		}
		RegNoSorter<CIN002> regNoSorter = new RegNoSorter<CIN002>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<SnakeBitePolicyDTO> findSnakeBitePolicyForDashboard(WorkflowCriteria wfc) throws DAOException {
		List<Object> objList = new ArrayList<Object>();
		List<SnakeBitePolicyDTO> result = new ArrayList<SnakeBitePolicyDTO>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT s.referenceNo, s.bookNo, sum(s.premium), sum(s.sumInsured),");
			buffer.append(" b.name, a.name, r.name, sm.name, p.name,s.salePoint.name");
			buffer.append(" FROM SnakeBitePolicy s LEFT OUTER JOIN s.branch b LEFT OUTER JOIN s.agent a LEFT OUTER JOIN ");
			buffer.append(" s.referral r LEFT OUTER JOIN s.saleMan sm LEFT OUTER JOIN s.product p, WorkFlow w ");
			buffer.append(" WHERE s.complete = False AND s.referenceNo = w.referenceNo AND w.workflowTask = :workflowTask ");
			buffer.append(" AND w.referenceType = :referenceType AND w.responsiblePerson.id = :responsiblePersonId ");
			buffer.append(" GROUP BY s.referenceNo, s.bookNo ,b.name, a.name, r.name, sm.name, p.name, s.salePoint.name ");
			Query q = em.createQuery(buffer.toString());
			q.setParameter("workflowTask", wfc.getWorkflowTask());
			q.setParameter("referenceType", wfc.getReferenceType());
			q.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			objList = q.getResultList();
			em.flush();
			String referenceNo;
			String bookNo;
			double premium;
			double sumInsured;
			String branch;
			String agent;
			String salePoint;
			Name name1;
			Name name2;
			Name name3;
			String product;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				referenceNo = (String) objArray[0];
				bookNo = (String) objArray[1];
				premium = (Double) objArray[2];
				sumInsured = (Double) objArray[3];
				branch = (String) objArray[4];
				agent = null;
				name1 = (Name) objArray[5];
				name2 = (Name) objArray[6];
				name3 = (Name) objArray[7];
				product = (String) objArray[8];
				salePoint = (String) objArray[9];
				if (name1 != null) {
					agent = name1.getFullName();
				} else if (name2 != null) {
					agent = name2.getFullName();
				} else {
					agent = name3.getFullName();
				}
				SnakeBitePolicyDTO dto = new SnakeBitePolicyDTO(referenceNo, bookNo, premium, sumInsured, branch, agent, product, salePoint);
				result.add(dto);
			}

		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find SnakeBitePolicyDTO by WorkflowCriteria", pe);
		}
		RegNoSorter<SnakeBitePolicyDTO> regNoSorter = new RegNoSorter<SnakeBitePolicyDTO>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<AgentCommissionDTO> findAgentCommissionForDashboard(WorkflowCriteria wfc) throws DAOException {
		List<Object> objList = new ArrayList<Object>();
		List<AgentCommissionDTO> result = new ArrayList<AgentCommissionDTO>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(
					" SELECT ac.agent, sum(ac.commission), sum(ac.withHoldingTax), max(ac.commissionStartDate), min(ac.commissionStartDate), ac.invoiceNo, ac.paymentChannel ");
			buffer.append(" FROM AgentCommission ac, WorkFlow w ");
			buffer.append(" WHERE ac.id = w.referenceNo AND ac.isPaid = False AND ac.invoiceDate IS NOT NULL AND w.referenceType = :referenceType");
			buffer.append(" AND w.responsiblePerson.id = :responsiblePersonId AND w.workflowTask = :workflowTask");
			buffer.append(" GROUP BY ac.invoiceNo, ac.agent, ac.paymentChannel ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			objList = query.getResultList();
			em.flush();
			Agent agent;
			double sumCommission;
			double sumWithHoldingTax;
			Date startDate;
			Date endDate;
			String invoiceNo;
			PaymentChannel paymentChannel;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				agent = (Agent) objArray[0];
				sumCommission = (Double) objArray[1];
				sumWithHoldingTax = objArray[2] != null ? (Double) objArray[2] : 0;
				startDate = (Date) objArray[3];
				endDate = (Date) objArray[4];
				invoiceNo = (String) objArray[5];
				paymentChannel = (PaymentChannel) objArray[6];
				result.add(new AgentCommissionDTO(agent, sumCommission, sumWithHoldingTax, startDate, endDate, invoiceNo, paymentChannel));
			}

		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find AgentCommissionDTO by WorkflowCriteria", pe);
		}

		RegNoSorter<AgentCommissionDTO> regNoSorter = new RegNoSorter<AgentCommissionDTO>(result);
		return regNoSorter.getSortedList();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<StaffCommissionDTO> findStaffCommissionForDashboard(WorkflowCriteria wfc) throws DAOException {
		List<Object> objList = new ArrayList<Object>();
		List<StaffCommissionDTO> result = new ArrayList<StaffCommissionDTO>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(
					" SELECT ac.staff, sum(ac.commission), sum(ac.withHoldingTax), max(ac.commissionStartDate), min(ac.commissionStartDate), ac.invoiceNo, ac.paymentChannel ");
			buffer.append(" FROM StaffAgentCommission ac, WorkFlow w ");
			buffer.append(" WHERE ac.id = w.referenceNo AND ac.isPaid = False AND ac.invoiceDate IS NOT NULL AND w.referenceType = :referenceType");
			buffer.append(" AND w.responsiblePerson.id = :responsiblePersonId AND w.workflowTask = :workflowTask");
			buffer.append(" GROUP BY ac.invoiceNo, ac.staff, ac.paymentChannel ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			objList = query.getResultList();
			em.flush();
			Staff staff;
			double sumCommission;
			double sumWithHoldingTax;
			Date startDate;
			Date endDate;
			String invoiceNo;
			PaymentChannel paymentChannel;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				staff = (Staff) objArray[0];
				sumCommission = (Double) objArray[1];
				sumWithHoldingTax = objArray[2] != null ? (Double) objArray[2] : 0;
				startDate = (Date) objArray[3];
				endDate = (Date) objArray[4];
				invoiceNo = (String) objArray[5];
				paymentChannel = (PaymentChannel) objArray[6];
				result.add(new StaffCommissionDTO(staff, sumCommission, sumWithHoldingTax, startDate, endDate, invoiceNo, paymentChannel));
			}

		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find AgentCommissionDTO by WorkflowCriteria", pe);
		}

		RegNoSorter<StaffCommissionDTO> regNoSorter = new RegNoSorter<StaffCommissionDTO>(result);
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public long findAgentCommissionCount(WorkflowCriteria wfc) throws DAOException {
		long count = 0;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT ac.invoiceNo ");
			buffer.append(" FROM AgentCommission ac, WorkFlow w ");
			buffer.append("	WHERE ac.id = w.referenceNo AND ac.isPaid = False AND ac.invoiceDate IS NOT NULL ");
			buffer.append(" AND w.referenceType = :referenceType AND w.responsiblePerson.id = :responsiblePersonId AND w.workflowTask = :workflowTask ");
			buffer.append(" GROUP BY ac.invoiceNo, ac.agent, ac.paymentChannel ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			count = query.getResultList().size();
			em.flush();

		} catch (NoResultException pe) {
			return 0;
		} catch (PersistenceException pe) {
			throw translate("Failed to find AgentCommissionCount ", pe);
		}
		return count;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public long findStaffCommissionCount(WorkflowCriteria wfc) throws DAOException {
		long count = 0;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT ac.invoiceNo ");
			buffer.append(" FROM StaffAgentCommission ac, WorkFlow w ");
			buffer.append("	WHERE ac.id = w.referenceNo AND ac.isPaid = False AND ac.invoiceDate IS NOT NULL ");
			buffer.append(" AND w.referenceType = :referenceType AND w.responsiblePerson.id = :responsiblePersonId AND w.workflowTask = :workflowTask ");
			buffer.append(" GROUP BY ac.invoiceNo, ac.staff, ac.paymentChannel ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			count = query.getResultList().size();
			em.flush();

		} catch (NoResultException pe) {
			return 0;
		} catch (PersistenceException pe) {
			throw translate("Failed to find AgentCommissionCount ", pe);
		}
		return count;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<TRA001> findTravelProposalForDashboard(WorkflowCriteria wfc) throws DAOException {
		List<Object> objList = new ArrayList<Object>();
		List<TRA001> result = new ArrayList<TRA001>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT t.id, t.proposalNo, t.submittedDate, w.createdDate, SUM(e.netPremium) ");
			buffer.append(" FROM TravelProposal t JOIN t.expressList e,  WorkFlow w ");
			buffer.append(" WHERE t.id = w.referenceNo AND w.workflowTask = :workflowTask ");
			buffer.append(" AND w.referenceType = :referenceType AND w.responsiblePerson.id = :responsiblePersonId");
			buffer.append(" GROUP BY t.id, t.proposalNo, t.submittedDate, w.createdDate ");
			Query q = em.createQuery(buffer.toString());
			q.setParameter("workflowTask", wfc.getWorkflowTask());
			q.setParameter("referenceType", wfc.getReferenceType());
			q.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			objList = q.getResultList();
			em.flush();
			String id;
			String proposalNo;
			Date submittedDate;
			Date createdDate;
			double netPremium = 0.0;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				proposalNo = (String) objArray[1];
				submittedDate = (Date) objArray[2];
				createdDate = (Date) objArray[3];
				netPremium = (Double) objArray[4];
				TRA001 dto = new TRA001(id, proposalNo, submittedDate, createdDate, netPremium);
				result.add(dto);
			}

		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find TravelProposals by WorkflowCriteria", pe);
		}
		RegNoSorter<TRA001> regNoSorter = new RegNoSorter<TRA001>(result);
		return regNoSorter.getSortedList();
	}

	public static void main(String[] arg) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPA");
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		Query query = em.createQuery("SELECT t.id, t.proposalNo, t.submittedDate, w.createdDate" + " FROM TravelProposal t,  WorkFlow w"
				+ " WHERE t.id = w.referenceNo AND w.workflowTask = :workflowTask" + " AND w.referenceType = :referenceType"
				+ " AND w.responsiblePerson.id = :responsiblePersonId");

		query.setParameter("workflowTask", WorkflowTask.CONFIRMATION);
		query.setParameter("referenceType", WorkflowReferenceType.TRAVEL_PROPOSAL);
		query.setParameter("responsiblePersonId", "INUSR0010001000000001129032013");

		long count = query.getResultList().size();
		System.out.println("Count : " + count);

		em.getTransaction().commit();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MEDCLM002> find_MEDCLM002(WorkflowCriteria wfc) throws DAOException {
		List<MEDCLM002> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(
					" SELECT mc.id, mc.claimRequestId, c.initialId, c.name,  mc.submittedDate, w.createdDate, SUM(ip.unit) as UNIT , SUM (ip.basicPlusUnit) as BASICPLUSUNIT ");
			buffer.append(" FROM MedicalClaimProposal mc LEFT JOIN mc.policyInsuredPerson ip ");
			buffer.append(" LEFT OUTER JOIN ip.customer c , WorkFlow w ");
			buffer.append(" WHERE mc.id = w.referenceNo AND w.workflowTask = :workflowTask ");
			buffer.append(" AND w.referenceType = :referenceType AND w.responsiblePerson.id = :responsiblePersonId");
			buffer.append(" GROUP BY mc.id, mc.claimRequestId,c.initialId, c.name,  mc.submittedDate, w.createdDate ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			List<Object> objList = query.getResultList();
			result = new ArrayList<MEDCLM002>();
			String id;
			String claimRequestId;
			String customerName;
			String initialId = "";
			Name name;
			Date submittedDate;
			Date createdDate;
			int unit = 0;
			int basicPlusUnit = 0;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				claimRequestId = (String) objArray[1];
				customerName = null;
				initialId = (String) objArray[2];
				if (objArray[2] == null) {
					initialId = "";
				}

				name = (Name) objArray[3];
				submittedDate = (Date) objArray[4];
				createdDate = (Date) objArray[5];
				unit = ((Long) objArray[6]).intValue();
				basicPlusUnit = objArray[7] == null ? 0 : ((Long) objArray[7]).intValue();
				customerName = initialId + name.getFullName();

				result.add(new MEDCLM002(id, claimRequestId, customerName, submittedDate, createdDate, unit + basicPlusUnit));
			}
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find MEDCLM002 by WorkflowCriteria", pe);
		}

		RegNoSorter<MEDCLM002> regNoSorter = new RegNoSorter<MEDCLM002>(result);
		return regNoSorter.getSortedList();

	}

	/* For Life Surrender */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LSP001> find_LSP001(WorkflowCriteria wfc) throws DAOException {
		List<LSP001> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT ls.id, ls.proposalNo, ls.policyNo, c.initialId, c.name , ls.submittedDate, w.createdDate, ls.surrenderAmount");
			buffer.append(" FROM LifeSurrenderProposal ls JOIN ls.lifePolicy l LEFT OUTER JOIN l.customer c JOIN WorkFlow w ON ls.id = w.referenceNo ");
			buffer.append(" WHERE w.workflowTask = :workflowTask AND w.referenceType = :referenceType ");
			buffer.append(" AND w.responsiblePerson.id = :responsiblePersonId ");
			Query q = em.createQuery(buffer.toString());
			q.setParameter("workflowTask", wfc.getWorkflowTask());
			q.setParameter("referenceType", wfc.getReferenceType());
			q.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			List<Object> objList = q.getResultList();
			result = new ArrayList<LSP001>();
			String id;
			String proposalNo;
			String policyNo;
			String customerName;
			String initialId = "";
			Name name;
			Date submittedDate;
			Date pendingSince;
			Double surrenderAmount;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				proposalNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				initialId = (String) objArray[3];
				name = (Name) objArray[4];
				submittedDate = (Date) objArray[5];
				pendingSince = (Date) objArray[6];
				surrenderAmount = (Double) objArray[7];
				if (name != null)
					customerName = initialId + name.getFullName();
				else
					customerName = null;

				result.add(new LSP001(id, proposalNo, policyNo, customerName, submittedDate, pendingSince, surrenderAmount));

			}
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find LSP001 by WorkflowCriteria", pe);
		}

		RegNoSorter<LSP001> regNoSorter = new RegNoSorter<LSP001>(result);
		return regNoSorter.getSortedList();

	}

	/* Life PaidUp */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LPP001> find_LPP001(WorkflowCriteria wfc) throws DAOException {
		List<LPP001> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT lp.id, lp.proposalNo, lp.policyNo, c.initialId, c.name, lp.submittedDate, w.createdDate, lp.realPaidUpAmount");
			buffer.append(" FROM LifePaidUpProposal lp LEFT OUTER JOIN lp.lifePolicy l LEFT OUTER JOIN l.customer c, WorkFlow w ");
			buffer.append(" WHERE w.workflowTask=:workflowTask AND w.referenceType=:referenceType ");
			buffer.append(" AND w.responsiblePerson.id = :responsiblePersonId AND lp.id = w.referenceNo ");
			Query q = em.createQuery(buffer.toString());
			q.setParameter("workflowTask", wfc.getWorkflowTask());
			q.setParameter("referenceType", wfc.getReferenceType());
			q.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			List<Object> objList = q.getResultList();
			result = new ArrayList<LPP001>();
			String id;
			String proposalNo;
			String policyNo;
			String customerName;
			String initialId = "";
			Name name;
			Date submittedDate;
			Date pendingSince;
			Double paidUpAmount;
			for (Object obj : objList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				proposalNo = (String) objArray[1];
				policyNo = (String) objArray[2];
				customerName = null;
				initialId = (String) objArray[3];
				name = (Name) objArray[4];
				submittedDate = (Date) objArray[5];
				pendingSince = (Date) objArray[6];
				paidUpAmount = (Double) objArray[7];
				if (name != null) {
					customerName = initialId + name.getFullName();
				}
				result.add(new LPP001(id, proposalNo, policyNo, customerName, submittedDate, pendingSince, paidUpAmount));

			}
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LPP001 by WorkflowCriteria", pe);
		}
		RegNoSorter<LPP001> regNoSorter = new RegNoSorter<LPP001>(result);
		return regNoSorter.getSortedList();

	}

	/* Person Travel */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<TRA001> find_TRA001(WorkflowCriteria wfc) throws DAOException {
		List<TRA001> result = new ArrayList<>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT p.id,p.proposalNo,c.initialId,c.name,o.name,p.submittedDate,i.totalUnit,i.premium,w.createdDate");
			buffer.append(" FROM PersonTravelProposal p ");
			buffer.append(" LEFT OUTER JOIN p.customer c ");
			buffer.append(" LEFT OUTER JOIN p.organization o ");
			buffer.append(" JOIN p.personTravelInfo i ");
			buffer.append(",WorkFlow w ");
			buffer.append(" WHERE w.workflowTask=:workflowTask AND w.referenceType=:referenceType AND p.id = w.referenceNo ");
			if (null != wfc.getResponsiblePerson()) {
				buffer.append(" AND w.responsiblePerson.id = :responsiblePersonId ");
			}

			Query q = em.createQuery(buffer.toString());
			q.setParameter("workflowTask", wfc.getWorkflowTask());
			q.setParameter("referenceType", wfc.getReferenceType());
			if (null != wfc.getResponsiblePerson()) {
				q.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			}

			List<Object[]> objList = q.getResultList();
			for (Object obj[] : objList) {
				result.add(new TRA001((String) obj[0], (String) obj[1], (String) obj[2], (Name) obj[3], (String) obj[4], (Date) obj[5], (Integer) obj[6], (double) obj[7],
						(Date) obj[8]));
			}
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LPP001 by WorkflowCriteria", pe);
		}
		// RegNoSorter<TRA001> regNoSorter = new RegNoSorter<TRA001>(result);
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MEDICAL002> find_MEDICAL002(WorkflowCriteria wfc, List<String> productIdList) throws DAOException {
		List<MEDICAL002> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW org.ace.insurance.proxy.MEDICAL002");
			buffer.append("(m.id, m.proposalNo, c.name,o.name, m.submittedDate, w.createdDate)");
			buffer.append(" FROM MedicalProposal m ");
			buffer.append(" LEFT OUTER JOIN m.organization o ");
			buffer.append(" LEFT OUTER JOIN m.customer c ");
			buffer.append(" JOIN m.medicalProposalInsuredPersonList mp ");
			buffer.append(" ,WorkFlow w ");
			buffer.append(" WHERE m.id = w.referenceNo AND ");
			buffer.append(" mp.product.id IN :productIdList AND");
			if (wfc.getProposalType() != null) {
				buffer.append(" m.proposalType=:proposalType AND ");
			}
			if (wfc.getResponsiblePerson() != null) {
				buffer.append(" w.responsiblePerson.id = :responsiblePersonId AND ");
			}

			buffer.append(" w.workflowTask = :workflowTask AND w.referenceType = :referenceType ");
			buffer.append(" GROUP BY m.id, m.proposalNo, c.name,o.name,m.submittedDate, w.createdDate ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("productIdList", productIdList);
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			if (wfc.getProposalType() != null) {
				query.setParameter("proposalType", wfc.getProposalType());
			}
			if (wfc.getResponsiblePerson() != null) {
				query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			}

			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LIF001 by WorkflowCriteria", pe);
		}
		// RegNoSorter<LIF001> regNoSorter = new RegNoSorter<LIF001>(result);
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<GroupMicroHealth> findGroupMicroHealth(WorkflowCriteria wfc) throws DAOException {
		List<GroupMicroHealth> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT g from GroupMicroHealth g,WorkFlow w ");
			buffer.append(" WHERE g.id = w.referenceNo AND ");
			if (wfc.getResponsiblePerson() != null) {
				buffer.append(" w.responsiblePerson.id = :responsiblePersonId AND ");
			}
			buffer.append(" w.workflowTask = :workflowTask AND w.referenceType = :referenceType ");
			TypedQuery<GroupMicroHealth> query = em.createQuery(buffer.toString(), GroupMicroHealth.class);
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			if (wfc.getResponsiblePerson() != null) {
				query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			}
			result = query.getResultList();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LIF001 by WorkflowCriteria", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<GF001> find_GF001(WorkflowCriteria wfc) throws DAOException {
		List<GF001> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW org.ace.insurance.proxy.GF001");
			buffer.append("(g.id, g.proposalNo, g.organization.name, g.submittedDate, g.noOfInsuredPerson, g.totalSI, g.premium, w.createdDate)");
			buffer.append(" FROM GroupFarmerProposal g  ");
			buffer.append(" , WorkFlow  w ");
			buffer.append(" WHERE g.id = w.referenceNo AND ");

			if (wfc.getResponsiblePerson() != null) {
				buffer.append(" w.responsiblePerson.id = :responsiblePersonId AND ");
			}
			buffer.append(" w.workflowTask = :workflowTask AND w.referenceType = :referenceType ");
			buffer.append(" GROUP BY g.id, g.proposalNo,g.organization,g.submittedDate,g.noOfInsuredPerson,g.totalSI,g.premium,w.createdDate ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			if (wfc.getResponsiblePerson() != null) {
				query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			}

			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find GF001 by WorkflowCriteria", pe);
		}
		// RegNoSorter<LIF001> regNoSorter = new RegNoSorter<LIF001>(result);
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LCP001> find_LCP001(WorkflowCriteria wfc) throws DAOException {
		List<LCP001> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + LCP001.class.getName());
			buffer.append("( lcp.id, lcp.claimProposalNo, cp.initialId, cp.name, lcp.submittedDate, w.createdDate, cp.sumInsured )");
			buffer.append("FROM LifeClaimProposal lcp ");
			buffer.append("LEFT JOIN lcp.claimPerson cp ");
			buffer.append(",WorkFlow w ");
			buffer.append("WHERE lcp.id = w.referenceNo AND w.workflowTask = :workflowTask ");
			buffer.append("AND w.referenceType = :referenceType AND w.responsiblePerson.id = :responsiblePersonId ");
			buffer.append("GROUP BY lcp.id, lcp.claimProposalNo, cp.initialId, cp.name, lcp.submittedDate, w.createdDate, cp.sumInsured ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workflowTask", wfc.getWorkflowTask());
			query.setParameter("referenceType", wfc.getReferenceType());
			query.setParameter("responsiblePersonId", wfc.getResponsiblePerson().getId());
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LCP001 by WorkflowCriteria", pe);
		}
		return result;
	}
}
