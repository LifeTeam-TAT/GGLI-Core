package org.ace.insurance.system.common.salepoint.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.salepoint.SalePoint;

public interface ISalePointService {

	void addNewSalePoint(SalePoint salePoint);

	void updateSalePoint(SalePoint salePoint);

	void deleteSalePoint(SalePoint salePoint);

	List<SalePoint> findAllSalePoint();

	List<SalePoint> findAllSalePointByStatus();

	SalePoint findSalePointById(String id);

	SalePoint findSalePointByCode(String branchCode);

	boolean isAlreadyExist(SalePoint salePoint);

	public List<SalePoint> findAllSalePointByBranch(String branchId);

}