package org.ace.insurance.web.datamodel;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.ace.insurance.role.ResourceItem;
import org.primefaces.model.SelectableDataModel;

public class ResourceItemDataModel extends ListDataModel<ResourceItem> implements SelectableDataModel<ResourceItem> {

	public ResourceItemDataModel() {
	}

	public ResourceItemDataModel(List<ResourceItem> data) {
		super(data);
	}

	@Override
	public ResourceItem getRowData(String rowKey) {

		List<ResourceItem> resourceItems = (List<ResourceItem>) getWrappedData();

		for (ResourceItem resourceItem : resourceItems) {
			if (resourceItem.getId().equals(rowKey))
				return resourceItem;
		}

		return null;
	}

	@Override
	public Object getRowKey(ResourceItem resourceItem) {
		return resourceItem.getId();
	}

}
