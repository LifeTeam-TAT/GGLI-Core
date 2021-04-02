package org.ace.insurance.system.common.ggiorganization.service.interfaces;

import java.util.List;


import org.ace.insurance.system.common.ggiorganization.GGIOrganization;
import org.ace.insurance.system.common.organization.ORG001;
import org.ace.insurance.system.common.organization.Organization;

public interface IGGIOrganizationService {

	public void insert(GGIOrganization ggliorganization);

	public void delete(GGIOrganization ggliorganization);

	public void update(GGIOrganization ggliorganization);

	public List<GGIOrganization> findAllGGIOrganization();
	
	public List<ORG001> findAll_ORG001();

	public GGIOrganization findOrganizationById(String id);
}
