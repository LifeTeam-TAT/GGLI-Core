package org.ace.insurance.system.common.relationshiptype.dao.interfaces;

import java.util.List;


import org.ace.insurance.system.common.relationshiptype.RelationShipType;

public interface IRelationShipTypeDAO {

	public void insert(RelationShipType relationshiptype);

	public void delete(RelationShipType relationshiptype);

	public void update(RelationShipType relationshiptype);

	public List<RelationShipType> findAllRelationShipType();

}
