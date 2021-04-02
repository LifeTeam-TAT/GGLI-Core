package org.ace.java.component.idgen.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.annotation.Resource;

import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.user.User;
import org.ace.java.component.idgen.IDGen;
import org.ace.java.component.idgen.exception.CustomIDGeneratorException;
import org.ace.java.component.idgen.persistence.interfaces.IDGenDAOInf;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.idgen.service.interfaces.IDConfigLoader;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;

@Service("CustomIDGenerator")
public class CustomIDGenerator implements ICustomIDGenerator {
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");

	@Resource(name = "ID_CONFIG")
	private Properties properties;

	@Resource(name = "IDGenDAO")
	private IDGenDAOInf idGenDAO;

	@Resource(name = "IDConfigLoader")
	private IDConfigLoader idConfigLoader;

	@Resource(name = "UserProcessService")
	private IUserProcessService userProcessService;

	@Override
	public String getNextId(String key, String productCode, boolean isIgnoreBranch) throws CustomIDGeneratorException {
		String id = null;
		try {
			String genName = (String) properties.getProperty(key);
			id = formatId(idGenDAO.getNextId(genName, isIgnoreBranch), isIgnoreBranch);
		} catch (DAOException e) {
			throw new CustomIDGeneratorException(e.getErrorCode(), "Failed to generate a ID", e);
		}
		return id;
	}

	@Override
	public String getNextId(String key, String productCode, Branch branch, boolean isIgnoreBranch) throws CustomIDGeneratorException {
		String id = null;
		try {
			String genName = (String) properties.getProperty(key);
			id = formatId(idGenDAO.getNextId(genName, branch, isIgnoreBranch), isIgnoreBranch);
		} catch (DAOException e) {
			throw new CustomIDGeneratorException(e.getErrorCode(), "Failed to generate a ID", e);
		}
		return id;
	}

	@Override
	/* This method is only for AutoRenewal Process */
	public String getNextIdForAutoRenewal(String key) throws CustomIDGeneratorException {
		String id = null;
		try {
			String genName = (String) properties.getProperty(key);
			id = formatIdForAutoRenewal(idGenDAO.getIDGenForAutoRenewal(genName), null);
		} catch (DAOException e) {
			throw new CustomIDGeneratorException(e.getErrorCode(), "Failed to generate a ID", e);
		}
		return id;
	}

	/* This method is only for AutoRenewal Process */
	private String formatIdForAutoRenewal(IDGen idGen, String productCode) {
		String id = idGen.getMaxValue() + "";
		String prefix = idGen.getPrefix();
		String suffix = idGen.getSuffix();
		int maxLength = idGen.getLength();
		String branchCode = null;
		if (idConfigLoader.isCentralizedSystem()) {

			branchCode = idConfigLoader.getBranchCode();
		}
		int idLength = id.length();
		for (; (maxLength - idLength) > 0; idLength++) {
			id = '0' + id;
		}
		suffix = suffix == null ? "" : "/" + suffix;
		productCode = productCode == null ? "" : productCode;
		// TODO need to validate isDateBased
		id = prefix + productCode + "/" + getDateString() + "/" + id + "/" + branchCode + suffix;
		return id;
	}

	// TODO FIX RMOVE productCode Parameter
	private String formatId(IDGen idGen, boolean isIgnoreBranch) {
		String id = idGen.getMaxValue() + "";
		String prefix = idGen.getPrefix();
		String suffix = idGen.getSuffix();
		int maxLength = idGen.getLength();
		String branchCode = null;
		boolean isDateBased = idGen.isDateBased();
		if (idConfigLoader.isCentralizedSystem()) {
			branchCode = userProcessService.getLoginUser().getBranch().getBranchCode();
		} else {
			branchCode = idConfigLoader.getBranchCode();
		}
		int idLength = id.length();
		for (; (maxLength - idLength) > 0; idLength++) {
			id = '0' + id;
		}
		if (suffix == null) {
			suffix = "";
		}

		if (isIgnoreBranch) {
			if (isDateBased) {

				id = prefix + "/" + getDateString() + "/" + id;
			} else {
				id = prefix + "/" + id;
			}
		} else {
			if (isDateBased) {
				id = prefix + "/" + getDateString() + "/" + id + "/" + branchCode + suffix;
			} else {
				id = prefix + "/" + id + "/" + branchCode + suffix;
			}
		}
		return id;
	}

	private String formatIdWithBranchCode(IDGen idGen, String productCode, Branch branch, boolean isIgnoreBranch) {
		String id = idGen.getMaxValue() + "";
		String prefix = idGen.getPrefix();
		String suffix = idGen.getSuffix();
		int maxLength = idGen.getLength();

		boolean isDateBased = idGen.isDateBased();
		// use passed branch instead of login branch
		// String branchCode = null;
		// if (idConfigLoader.isCentralizedSystem()) {
		// branchCode =
		// userProcessService.getLoginUser().getBranch().getBranchCode();
		// } else {
		// branchCode = idConfigLoader.getBranchCode();
		// }
		int idLength = id.length();
		for (; (maxLength - idLength) > 0; idLength++) {
			id = '0' + id;
		}
		if (suffix == null) {
			suffix = "";
		}
		if (productCode == null) {
			productCode = "";
		}
		if (isIgnoreBranch) {
			if (isDateBased) {
				id = prefix + productCode + "/" + getDateString() + "/" + id + "/" + suffix;
			} else {
				id = prefix + productCode + "/" + id + "/" + suffix;
			}
		} else {
			if (isDateBased) {
				id = prefix + productCode + "/" + getDateString() + "/" + id + "/" + branch.getBranchCode() + suffix;
			} else {
				id = prefix + productCode + "/" + id + "/" + branch.getBranchCode() + suffix;
			}
		}

		return id;
	}

	@Override
	/* This method is only for AutoRenewal Process */
	public String getPrefixForAutoRenewal(Class cla) {
		String prefix = idConfigLoader.getFormat(cla.getName());
		return prefix;
	}

	@Override
	public String getPrefix(Class cla, boolean isIgnoreBranch) {
		return getPrefixStr(cla, null, isIgnoreBranch);
	}

	@Override
	public String getPrefix(Class cla, User user, boolean isIgnoreBranch) {
		return getPrefixStr(cla, user, isIgnoreBranch);
	}

	private String getPrefixStr(Class cla, User user, boolean isIgnoreBranch) {

		String prefix = idConfigLoader.getFormat(cla.getName());
		if (!isIgnoreBranch) {
			String branchCode = null;
			if (idConfigLoader.isCentralizedSystem()) {
				branchCode = user == null ? userProcessService.getLoginUser().getBranch().getBranchCode() : user.getBranch().getBranchCode();
			} else {
				branchCode = idConfigLoader.getBranchCode();
			}
			return prefix + branchCode;
		} else {
			return prefix;
		}

	}

	private String getDateString() {
		return simpleDateFormat.format(new Date());
	}

	/*
	 * public static void main(String args[]) {
	 * 
	 * EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPA");
	 * EntityManager em = emf.createEntityManager();
	 * em.getTransaction().begin();
	 * 
	 * List<String> genNames = Arrays.asList("Endowment_Life_Policy_No",
	 * "Short_Term_Endowment_Life_Policy_No", "Personal_Accident_Policy_No",
	 * "Snake_Bite_Life_Policy_No", "Education_Policy_No",
	 * "Farmer_Life_Policy_No", "Group_Life_Policy_No", "Health_Policy_No",
	 * "Critical_Illness_Policy_No ", "Micro_Health_Policy_No",
	 * "Special_Travelling_Policy_No", "Travel_Under_100_Miles_Policy_No",
	 * "General_Travel_Policy_No", "Sportsman_Life_Policy_No");
	 * 
	 * int i = 0; for (String genName : genNames) { String prefix =
	 * String.valueOf((char) (65 + i)); IDGen idGen = new IDGen(String.valueOf(i
	 * + 15), genName.toUpperCase(), 0, prefix, null, null, 10, true, null);
	 * em.persist(idGen); i++; }
	 * 
	 * em.getTransaction().commit(); em.close(); emf.close();
	 * 
	 * }
	 */

	/*
	 * public static void main(String args[]) {
	 * 
	 * EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPA");
	 * EntityManager em = emf.createEntityManager();
	 * em.getTransaction().begin();
	 * 
	 * List<String> genNames = Arrays.asList("Endowment_Life_Proposal_NO",
	 * "Short_Term_Endowment_Life_Proposal_NO", "Personal_Accident_Proposal_NO",
	 * "Snake_Bite_Life_Proposal_NO", "Education_Proposal_NO",
	 * "Farmer_Life_Proposal_NO", "Group_Life_Proposal_NO",
	 * "Health_Proposal_NO", "Critical_Illness_Proposal_NO",
	 * "Micro_Health_Proposal_NO", "Special_Travelling_Proposal_NO",
	 * "Travel_Under_100_Miles_Proposal_NO", "General_Travel_Proposal_NO",
	 * "Sportsman_Life_Proposal_NO");
	 * 
	 * int i = 0; for (String genName : genNames) { String prefix =
	 * String.valueOf((char) (65 + i)); IDGen idGen = new
	 * IDGen(String.valueOf(i), genName.toUpperCase(), 0, prefix.concat("P"),
	 * null, null, 10, true, null); em.persist(idGen); i++; }
	 * 
	 * em.getTransaction().commit(); em.close(); emf.close();
	 * 
	 * }
	 */

	/* ALTER TABLE CUSTOM_ID_GEN ALTER COLUMN BRANCHID char(36) NULL */

	/*
	 * public static void main(String args[]) {
	 * 
	 * EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPA");
	 * EntityManager em = emf.createEntityManager();
	 * em.getTransaction().begin();
	 * 
	 * List<String> genNames = Arrays.asList("AGENT_SANCTION_ID_GEN",
	 * "AGENT_COMMISSION_INVOICE_ID_GEN", "CASH_RECEIPT_ID_GEN",
	 * "CHEQUE_RECEIPT_ID_GEN", "TRANSFER_RECEIPT_ID_GEN");
	 * 
	 * int i = 0; for (String genName : genNames) { String prefix =
	 * String.valueOf((char) (65 + i)); IDGen idGen = new IDGen(String.valueOf(i
	 * + 1), genName.toUpperCase(), 0, prefix.concat("P"), null, null, 10, true,
	 * null); em.persist(idGen); i++; }
	 * 
	 * em.getTransaction().commit(); em.close(); emf.close();
	 * 
	 * }
	 */

	// public static void main(String args[]) {
	//
	// EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPA");
	// EntityManager em = emf.createEntityManager();
	// em.getTransaction().begin();
	// String[] genNames = { "AGENT_SANCTION_ID_GEN",
	// "AGENT_COMMISSION_INVOICE_ID_GEN", "CASH_RECEIPT_ID_GEN",
	// "CHEQUE_RECEIPT_ID_GEN", "TRANSFER_RECEIPT_ID_GEN" };
	// int i = 0;
	// for (String genName : genNames) {
	// String prefix = String.valueOf((char) (65 + i));
	// IDGen idGen = new IDGen(String.valueOf(i + 1), genName.toUpperCase(), 0,
	// prefix.concat("P"), null, null, 10, true, null);
	// em.persist(idGen);
	// i++;
	// }
	//
	// em.getTransaction().commit();
	// em.close();
	// emf.close();
	//
	// }

	/*
	 * public static void main(String args[]) {
	 * 
	 * EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPA");
	 * EntityManager em = emf.createEntityManager();
	 * em.getTransaction().begin();
	 * 
	 * List<String> genNames = Arrays.asList("Endowment_Life_CLAIM_No",
	 * "Short_Term_Endowment_Life_CLAIM_No", "Personal_Accident_CLAIM_No",
	 * "Snake_Bite_Life_CLAIM_No", "Education_CLAIM_No", "Farmer_Life_CLAIM_No",
	 * "Group_Life_CLAIM_No", "Health_CLAIM_No", "Critical_Illness_CLAIM_No ",
	 * "Micro_Health_CLAIM_No", "Special_Travelling_CLAIM_No",
	 * "Travel_Under_100_Miles_CLAIM_No", "General_Travel_CLAIM_No",
	 * "Sportsman_Life_CLAIM_No");
	 * 
	 * int i = 0; for (String genName : genNames) { String prefix =
	 * String.valueOf((char) (65 + i)); IDGen idGen = new IDGen(String.valueOf(i
	 * + 29), genName.toUpperCase(), 0, prefix.concat("C"), null, null, 10,
	 * true, null); em.persist(idGen); i++; }
	 * 
	 * em.getTransaction().commit(); em.close(); emf.close();
	 * 
	 * }
	 */

	@Override
	public IDGen getIDGen(String key, boolean isIgnoreBranch) throws CustomIDGeneratorException {
		IDGen idGen = null;
		try {
			String genName = (String) properties.getProperty(key);
			idGen = idGenDAO.getIDGen(genName, isIgnoreBranch);
		} catch (DAOException e) {
			throw new CustomIDGeneratorException(e.getErrorCode(), "Failed to Find IDGen", e);
		}
		return idGen;
	}

	@Override
	public IDGen updateIDGen(IDGen idGen) throws CustomIDGeneratorException {
		try {
			idGen = idGenDAO.updateIDGen(idGen);
		} catch (DAOException e) {
			throw new CustomIDGeneratorException(e.getErrorCode(), "Failed to Update IDGen", e);
		}
		return idGen;
	}

	@Override
	public String getNextIdWithBranchCode(String key, String productCode, Branch branch, boolean isIgnoreBranch) throws CustomIDGeneratorException {
		String id = null;
		try {
			String genName = (String) properties.getProperty(key);
			id = formatIdWithBranchCode(idGenDAO.getNextId(genName, branch, isIgnoreBranch), productCode, branch, isIgnoreBranch);
		} catch (DAOException e) {
			throw new CustomIDGeneratorException(e.getErrorCode(), "Failed to generate a ID", e);
		}
		return id;
	}

}
