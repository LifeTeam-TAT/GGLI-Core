package org.ace.insurance.web.manage.travel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.travel.expressTravel.ExpressDetail;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;

@ViewScoped
@ManagedBean(name = "ExpressDetailActionBean")
public class ExpressDetailActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	private User user;
	private Map<String, ExpressDetail> expressDetailMap;
	private TravelExpress express;
	private ExpressDetail expressDetail;
	private List<TravelProposal> travelProposalList;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposalList = new ArrayList<TravelProposal>();
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		travelProposalList = travelProposalService.findAllTravelProposal();
		expressDetailMap = new HashMap<String, ExpressDetail>();
		createNewExpress();
		createNewExpressDetail();
	}

	// public void prepareAddExpressDetail(TravelExpress express) {
	// this.express = express;
	// if (this.express.getgetExpressDetailList() != null &&
	// !express.getExpressDetailList().isEmpty()) {
	// for (ExpressDetail ed : this.express.getExpressDetailList()) {
	// expressDetailMap.put(ed.getTempId(), ed);
	// }
	// }
	// }

	public void createNewExpress() {
		express = new TravelExpress();
	}

	public void createNewExpressDetail() {
		expressDetail = new ExpressDetail();
	}

	public TravelExpress getExpress() {
		return express;
	}

	public void setExpress(TravelExpress express) {
		this.express = express;
	}

	public Map<String, ExpressDetail> getExpressDetailMap() {
		return expressDetailMap;
	}

	public void setExpressDetailMap(Map<String, ExpressDetail> expressDetailMap) {
		this.expressDetailMap = expressDetailMap;
	}

	public ExpressDetail getExpressDetail() {
		return expressDetail;
	}

	public void setExpressDetail(ExpressDetail expressDetail) {
		this.expressDetail = expressDetail;
	}

	public List<ExpressDetail> getExpressDetailList() {
		return new ArrayList<ExpressDetail>(expressDetailMap.values());
	}

	public void removeExpressDetail(ExpressDetail expressDetail) {
		expressDetailMap.remove(expressDetail.getTempId());
	}

	public List<TravelProposal> getTravelProposalList() {
		return travelProposalList;
	}

}
