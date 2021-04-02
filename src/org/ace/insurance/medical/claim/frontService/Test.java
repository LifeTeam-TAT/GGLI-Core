package org.ace.insurance.medical.claim.frontService;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.payment.Payment;

public class Test {

	public static void main(String[] args) {
		try {

			EntityManager em = Persistence.createEntityManagerFactory("JPA").createEntityManager();
			em.getTransaction().begin();
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT p FROM Payment p JOIN MedicalPolicy l ON  l.id = p.referenceNo WHERE  p.referenceType = :referenceType");
			buffer.append(" AND l.policyNo = :policyNo");
			buffer.append(" AND p.toTerm = (");
			buffer.append(" SELECT MAX(p.toTerm) FROM Payment p JOIN MedicalPolicy l ON  l.id = p.referenceNo WHERE  p.referenceType = :referenceType");
			buffer.append(" AND l.policyNo = :policyNo");
			buffer.append(" )");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("referenceType", PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION);
			query.setParameter("policyNo", "H/1904/0000000004");
			Payment p = (Payment) query.getSingleResult();
			System.out.println(p);
			em.flush();
			em.getTransaction().commit();

			System.out.println("jpa test is successfully !!!!!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}