package org.ace.java.component.persistence;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.product.Product;
import org.ace.insurance.user.User;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.persistence.exception.DAOException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

/**
 * @author Zaw Than Oo
 */
public class BasicDAO {

	@PersistenceContext
	protected EntityManager em;

	@Resource(name = "UserProcessService")
	protected IUserProcessService userProcessService;

	@Resource(name = "KEYFACTOR_ID_CONFIG")
	private Properties keyFactorConfig;

	@Resource(name = "SQL_ERROR_CODE")
	private Properties properties;

	private final String FIRE_BUILDING = "FIRE_BUILDING";
	private final String FIRE_MACHINERY = "FIRE_MACHINERY";
	private final String FIRE_FURNITURE = "FIRE_FURNITURE";
	private final String FIRE_STOCK = "FIRE_STOCK";
	private final String FIRE_DECLARATION_POLICY = "FIRE_DECLARATION_POLICY";
	private final String PUBLIC_LIFE = "PUBLIC_LIFE";
	private final String GROUP_LIFE = "GROUP_LIFE";

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public DAOException translate(String message, SQLException sqlex) {
		String errorCode = properties.getProperty(sqlex.getErrorCode() + "");
		if (errorCode == null || errorCode.equals("")) {
			return new DAOException(ErrorCode.NO_SQL_ERROR_CODE_CONFIG, "There is no SQL ERROR CODE(" + sqlex.getErrorCode() + ") in configuration", sqlex);
		} else {
			return new DAOException(errorCode, message, sqlex);
		}
	}

	public RuntimeException translate(String message, RuntimeException e) {
		e.printStackTrace();
		DAOException dae = null;
		Throwable throwable = e;
		while (throwable.getCause() != null && !(throwable instanceof SQLException)) {
			throwable = throwable.getCause();
		}
		if (throwable instanceof SQLException) {
			dae = translate(message, (SQLException) throwable);
		}
		if (dae != null) {
			return dae;
		} else {
			return new DAOException(ErrorCode.DAO_RUNTIME_ERROR, message, e);
		}
	}

	protected void insertProcessLog(String tableName, String primaryKey) throws PersistenceException {
		insertProcessLogging(tableName, primaryKey, null);
	}

	protected void insertProcessLog(String tableName, String primaryKey, User user) throws PersistenceException {
		insertProcessLogging(tableName, primaryKey, user);
	}

	protected void updateProcessLog(String tableName, String primaryKey) throws PersistenceException {
		String queryString = "UPDATE " + tableName + " SET UPDATEDUSERID = ?, UPDATEDDATE = ? WHERE ID = ?";
		Query query = em.createNativeQuery(queryString);
		query.setParameter(1, userProcessService.getLoginUser().getId());
		query.setParameter(2, new Date());
		query.setParameter(3, primaryKey);
		query.executeUpdate();
	}

	protected boolean isFireProduct(String productId) {
		if (productId.equals(keyFactorConfig.getProperty(FIRE_BUILDING)) || productId.equals(keyFactorConfig.getProperty(FIRE_DECLARATION_POLICY))
				|| productId.equals(keyFactorConfig.getProperty(FIRE_FURNITURE)) || productId.equals(keyFactorConfig.getProperty(FIRE_MACHINERY))
				|| productId.equals(keyFactorConfig.getProperty(FIRE_STOCK))) {
			return true;
		}
		return false;
	}

	protected boolean isPublicLife(Product product) {
		if (product.getId().equals(keyFactorConfig.getProperty(PUBLIC_LIFE))) {
			return true;
		}
		return false;
	}

	protected boolean isGroupLife(Product product) {
		if (product.getId().equals(keyFactorConfig.getProperty(GROUP_LIFE))) {
			return true;
		}
		return false;
	}

	private void insertProcessLogging(String tableName, String primaryKey, User user) throws PersistenceException {
		String queryString = "UPDATE " + tableName + " SET CREATEDUSERID = ?, CREATEDDATE = ? WHERE ID = ?";
		Query query = em.createNativeQuery(queryString);
		if (user == null)
			query.setParameter(1, userProcessService.getLoginUser().getId());
		else
			query.setParameter(1, user.getId());
		query.setParameter(2, new Date());
		query.setParameter(3, primaryKey);
		query.executeUpdate();
	}

	protected boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		}
		if (value.toString().isEmpty()) {
			return true;
		}
		return false;
	}

	protected static void jasperListPDFExport(List jasperPrintList, String dirPath, String fileName) {
		JRExporter exporter = new JRPdfExporter();
		exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
		try {
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (JRException je) {
			je.printStackTrace();
		}
	}
}
