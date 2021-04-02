package org.ace.insurance.filter.life.interfaces;

import java.util.List;

import org.ace.insurance.filter.life.LFP001;
import org.ace.insurance.filter.life.LifeFilterCriteria;

public interface ILIFE_Filter {
	public List<LFP001> find(LifeFilterCriteria criteria);
}
