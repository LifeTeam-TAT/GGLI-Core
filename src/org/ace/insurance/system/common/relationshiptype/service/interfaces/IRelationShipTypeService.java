package org.ace.insurance.system.common.relationshiptype.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.relationshiptype.RelationShipType;

public interface IRelationShipTypeService {

	public void insert(RelationShipType relationshiptype);

	public void delete(RelationShipType relationshiptype);

	public void update(RelationShipType relationshiptype);

	public List<RelationShipType> findAllRelationShipType();

}
