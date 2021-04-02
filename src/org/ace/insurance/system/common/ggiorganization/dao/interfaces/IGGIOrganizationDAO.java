package org.ace.insurance.system.common.ggiorganization.dao.interfaces;

import java.util.List;

import org.ace.insurance.system.common.ggiorganization.GGIOrganization;
import org.ace.insurance.system.common.organization.ORG001;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.java.component.persistence.exception.DAOException;

public interface IGGIOrganizationDAO{

	public void insert(GGIOrganization ggiorganization);

	public void delete(GGIOrganization ggiorganization);

	public void update(GGIOrganization ggiorganization);

	public List<GGIOrganization> findAllGGIOrganization();

	public List<ORG001> findAll_ORG001() throws DAOException;
	
	public GGIOrganization findById(String id) throws DAOException;
}
