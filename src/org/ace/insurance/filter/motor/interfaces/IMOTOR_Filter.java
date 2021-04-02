package org.ace.insurance.filter.motor.interfaces;

import java.util.List;

import org.ace.insurance.filter.cirteria.CRIA002;
import org.ace.insurance.filter.motor.MPLC001;

public interface IMOTOR_Filter {
	public List<MPLC001> find(CRIA002 criteria);
}
