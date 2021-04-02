package org.ace.insurance.system.common.saleman.service.interfaces;

import java.util.List;

import org.ace.insurance.common.SaleManCriteria;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.saleman.history.SaleManHistory;

public interface ISaleManService {
	public void addNewSaleMan(SaleMan saleMan);

	public void addNewSaleManHistory(SaleManHistory saleManhistory);

	public void updateSaleMan(SaleMan saleMan);

	public void deleteSaleMan(SaleMan saleMan);

	public SaleMan findSaleManById(String id);

	public List<SaleMan> findAllSaleMan();

	public List<SaleMan> findSaleManByCriteria(SaleManCriteria saleManCriteria);

	public boolean checkExistingSaleMan(SaleMan saleMan);
}
