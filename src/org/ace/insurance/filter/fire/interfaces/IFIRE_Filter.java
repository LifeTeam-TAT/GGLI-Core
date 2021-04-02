package org.ace.insurance.filter.fire.interfaces;

import java.util.List;

import org.ace.insurance.filter.cirteria.CRIA002;
import org.ace.insurance.filter.fire.FPLC001;

public interface IFIRE_Filter {
	public List<FPLC001> find(CRIA002 criteria);
}
