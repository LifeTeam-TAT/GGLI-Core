package org.ace.insurance.claim.disabilityPart.persistence.interfaces;

import java.util.List;

import org.ace.insurance.claim.disabilityPart.DisabilityPart;
import org.ace.insurance.claim.disabilityPart.ProductDisability;
import org.ace.insurance.claim.disabilityPart.ProductDisabilityRate;
import org.ace.java.component.persistence.exception.DAOException;

public interface IProductDisabilityDAO {

	public void insert(ProductDisability productDisability) throws DAOException;

	public ProductDisability update(ProductDisability productDisability) throws DAOException;

	public void delete(ProductDisability productDisability) throws DAOException;

	public void insert(ProductDisabilityRate productDisabilityRate) throws DAOException;

	public void delete(ProductDisabilityRate productDisabilityRate) throws DAOException;

	public List<ProductDisability> findAll() throws DAOException;

	public List<DisabilityPart> findAllDisabilityPartByProduct(String productId) throws DAOException;

	public List<ProductDisabilityRate> findAllDisabilityRateByProduct(String productId) throws DAOException;

	public double findDisabilityRateById(String id) throws DAOException;
}
