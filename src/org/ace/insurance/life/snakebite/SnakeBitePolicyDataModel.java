package org.ace.insurance.life.snakebite;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class SnakeBitePolicyDataModel extends ListDataModel<SnakeBitePolicy> implements SelectableDataModel<SnakeBitePolicy> {

	public SnakeBitePolicyDataModel(){
		
	}
	
	public SnakeBitePolicyDataModel(List<SnakeBitePolicy> snakeBiteList){
		super(snakeBiteList);
	}
	
	@Override
	public SnakeBitePolicy getRowData(String rowKey) {
		List<SnakeBitePolicy> snakeBiteList = (List<SnakeBitePolicy>)getWrappedData();
		for (SnakeBitePolicy snakebite : snakeBiteList) {
			if (snakebite.getId().equals(rowKey)) {
				return snakebite;
			}
		}
		return null;
	}

	@Override
	public Object getRowKey(SnakeBitePolicy snakebite) {
		return snakebite.getId();
	}
}
