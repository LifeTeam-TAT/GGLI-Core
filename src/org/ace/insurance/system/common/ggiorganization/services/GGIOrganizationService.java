package org.ace.insurance.system.common.ggiorganization.services;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.ggiorganization.GGIOrganization;
import org.ace.insurance.system.common.ggiorganization.dao.interfaces.IGGIOrganizationDAO;
import org.ace.insurance.system.common.ggiorganization.service.interfaces.IGGIOrganizationService;
import org.ace.insurance.system.common.organization.ORG001;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("GGIOrganizationService")
public class GGIOrganizationService implements IGGIOrganizationService {

	@Resource(name = "GGIOrganizationDAO")
	IGGIOrganizationDAO organizationDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(GGIOrganization ggiorganization) {
		try {
			organizationDAO.insert(ggiorganization);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a entity", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(GGIOrganization ggiorganization) {
		try {
			organizationDAO.delete(ggiorganization);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a entity", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(GGIOrganization ggiorganization) {
		try {
			organizationDAO.update(ggiorganization);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a entity", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GGIOrganization> findAllGGIOrganization() {
		List<GGIOrganization> result = null;
		try {
			result = organizationDAO.findAllGGIOrganization();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a entity", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<ORG001> findAll_ORG001() {
		List<ORG001> result = null;
		try {
			result = organizationDAO.findAll_ORG001();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all Organization (ORG001 )", e);
		}
		return result;
	}

	public GGIOrganization findOrganizationById(String id) {
		GGIOrganization result = null;
		try {
			result = organizationDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Organization (ID : " + id + ")", e);
		}
		return result;
	}

}
