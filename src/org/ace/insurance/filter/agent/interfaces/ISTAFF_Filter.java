package org.ace.insurance.filter.agent.interfaces;

import java.util.List;


import org.ace.insurance.filter.agent.STAFF001;
import org.ace.insurance.filter.cirteria.CRISTAFF001;

public interface ISTAFF_Filter {
	public List<STAFF001> find(CRISTAFF001 item, String value);
	public List<STAFF001> find(int maxResult);
}
