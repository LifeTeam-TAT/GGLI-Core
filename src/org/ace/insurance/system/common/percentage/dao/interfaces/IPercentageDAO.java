package org.ace.insurance.system.common.percentage.dao.interfaces;

import java.util.List;

import org.ace.insurance.system.common.percentage.Percentage;

public interface IPercentageDAO {

	public void insert(Percentage percentage);

	public void delete(Percentage percentage);

	public void update(Percentage percentage);

	public List<Percentage> findAllPercentage();

	Percentage findPercentageWithRelationShip(String typeId, String productId);
}
