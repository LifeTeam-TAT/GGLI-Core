package org.ace.insurance.claim.disabilityPart.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.claim.disabilityPart.DisabilityPart;
import org.ace.insurance.claim.disabilityPart.LifeClaimKeyFactorInfoMultiValue;
import org.ace.insurance.claim.disabilityPart.ProductDisability;
import org.ace.insurance.claim.disabilityPart.ProductDisabilityRate;
import org.ace.insurance.claim.disabilityPart.persistence.interfaces.IProductDisabilityDAO;
import org.ace.insurance.claim.disabilityPart.service.interfaces.IProductDisabilityService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.persistence.interfaces.IDataRepository;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "ProductDisabilityService")
public class ProductDisabilityService extends BaseService implements IProductDisabilityService {

	@Resource(name = "ProductDisabilityDAO")
	private IProductDisabilityDAO productDisabilityDAO;

	@Resource(name = "DataRepository")
	private IDataRepository<ProductDisability> productDisabilityRepo;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void addNewProductDisability(ProductDisability productDisability) {
		try {
			productDisabilityDAO.insert(productDisability);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to add a new Product Disability", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ProductDisability updateProductDisability(ProductDisability productDisability) {
		try {
			productDisabilityDAO.update(productDisability);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to update a Product Disability", e);
		}
		return productDisability;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteProductDisability(ProductDisability productDisability) {
		try {
			productDisabilityDAO.delete(productDisability);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to delete a Product Disability", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void addNewProductDisabilityRate(ProductDisabilityRate productDisabilityRate) {
		try {
			productDisabilityDAO.insert(productDisabilityRate);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to add a new Product Disability Rate", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteProductDisabilityRate(ProductDisabilityRate productDisabilityRate) {
		try {
			productDisabilityDAO.delete(productDisabilityRate);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to delete a Product Disability Rate", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<ProductDisability> findAllDisabilityPart() {
		List<ProductDisability> result = null;
		try {
			result = productDisabilityDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all of Product Disability)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<DisabilityPart> findAllDisabilityPartByProduct(String productId) {
		List<DisabilityPart> result = null;
		try {
			result = productDisabilityDAO.findAllDisabilityPartByProduct(productId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all of Disability Part)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<ProductDisabilityRate> findAllDisabilityRateByProduct(String productId) {
		List<ProductDisabilityRate> result = null;
		try {
			result = productDisabilityDAO.findAllDisabilityRateByProduct(productId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all of Disability Rate)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public double findAllDisabilityRateById(List<LifeClaimKeyFactorInfoMultiValue> claimMultiValueList) {
		double disabilityRate = 0.0;
		try {
			for (LifeClaimKeyFactorInfoMultiValue lckimValue : claimMultiValueList) {
				double rate = productDisabilityDAO.findDisabilityRateById(lckimValue.getValue());
				disabilityRate += rate;
			}
			if (disabilityRate > 100) {
				disabilityRate = 100;
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all of Disability Rate)", e);
		}
		return disabilityRate;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ProductDisability findProductDisbilityById(String id) {
		try {
			return productDisabilityRepo.findById(ProductDisability.class, id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find by id)", e);
		}

	}
}
