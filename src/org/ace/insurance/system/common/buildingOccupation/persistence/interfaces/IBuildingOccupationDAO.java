package org.ace.insurance.system.common.buildingOccupation.persistence.interfaces;

import java.util.List;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.system.common.buildingOccupation.BuildingOccupation;
import org.ace.insurance.system.common.buildingOccupation.BuildingOccupation001;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBuildingOccupationDAO {
	public void insert(BuildingOccupation buildingOccupation) throws DAOException;

	public void update(BuildingOccupation buildingOccupation) throws DAOException;

	public void delete(BuildingOccupation buildingOccupation) throws DAOException;

	public BuildingOccupation findById(String id) throws DAOException;

	public List<BuildingOccupation> findByInsuranceType(InsuranceType insuranceType) throws DAOException;

	public List<BuildingOccupation> findAll() throws DAOException;

	public List<BuildingOccupation001> findAllBuildingOccupation() throws DAOException;

	public List<BuildingOccupation> findByCriteria(String criteria) throws DAOException;

	public List<BuildingOccupation001> findByCriteria001(String criteria) throws DAOException;

}
