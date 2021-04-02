package org.ace.insurance.web.datamodel;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.ace.insurance.coinsurance.Coinsurance;
import org.primefaces.model.SelectableDataModel;

public class CoinsuranceDataModel extends ListDataModel<Coinsurance> implements SelectableDataModel<Coinsurance> {

	public CoinsuranceDataModel() {
	}

	public CoinsuranceDataModel(List<Coinsurance> data) {
		super(data);
	}

	@Override
	public Coinsurance getRowData(String rowKey) {

		List<Coinsurance> coinsurances = (List<Coinsurance>) getWrappedData();

		for (Coinsurance coinsurance : coinsurances) {
			if (coinsurance.getId().equals(rowKey))
				return coinsurance;
		}

		return null;
	}

	@Override
	public Object getRowKey(Coinsurance coinsurance) {
		return coinsurance.getId();
	}
}
