package org.ace.insurance.web.manage.travel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;

import org.ace.insurance.common.Utils;
import org.ace.insurance.system.common.occurrence.Occurrence;
import org.ace.insurance.system.common.occurrence.service.interfaces.IOccurrenceService;
import org.ace.insurance.travel.expressTravel.ExpressDetail;
import org.ace.insurance.travel.expressTravel.Tour;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewExpressDetailActionBean")
public class AddNewExpressDetailActionBean extends BaseBean implements Serializable {
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

	@ManagedProperty(value = "#{OccurrenceService}")
	private IOccurrenceService occurrenceService;

	public void setOccurrenceService(IOccurrenceService occurrenceService) {
		this.occurrenceService = occurrenceService;
	}

	private User user;
	private TravelExpress travelExpress;

	private Tour tour;
	private Map<String, Tour> tourMap;
	private Map<String, Tour> tourMapUpdate;
	private ExpressDetail expressDetail;
	private boolean createNew;
	private boolean saveExpress;
	private String occurrenceId;
	private int tempTotalPassenger = 0;
	private int tempTotalUnit = 0;
	private double tempTotalNetPremium = 0.0;
	private Map<String, ExpressDetail> expressDetailForTotalMap;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelExpress = (travelExpress == null) ? (TravelExpress) getParam("TravelExpress") : travelExpress;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		createNewTour();
		tourMap = new HashMap<String, Tour>();
		tourMapUpdate = new HashMap<String, Tour>();
		if (travelExpress.getTourList() != null) {
			for (Tour tour : travelExpress.getTourList()) {
				tourMap.put(tour.getOccurrence().getId(), tour);
				for (ExpressDetail e : tour.getExpressDetailList()) {
					tempTotalPassenger += e.getNoOfPassenger();
					tempTotalUnit += e.getNoOfUnit();
					tempTotalNetPremium += e.getNetPremium();
				}
			}
		}
	}

	@PreDestroy
	public void preDestroy() {
		removeParam("TravelExpress");
	}

	public void createNewTour() {
		createNew = true;
		tour = new Tour();
		tour.setTravelExpress(travelExpress);
	}

	private boolean validTour() {
		if (tourMap.containsKey(tour.getOccurrence().getId())) {
			return false;
		}
		return true;
	}

	public void addTour() {
		if (validTour()) {
			tourMap.put(tour.getOccurrence().getId(), tour);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, tour.getOccurrence().getDescription());
			createNewTour();
		} else {
			addWranningMessage(null, MessageId.ALREADY_INSERT, tour.getOccurrence().getDescription());
		}
	}

	public void deleteTour() {
		tourMap.remove(tour.getOccurrence().getId());
		tempTotalNetPremium = tempTotalNetPremium - tour.getNetPremium();
		tempTotalPassenger = tempTotalPassenger - tour.getNoOfPassenger();
		tempTotalUnit = tempTotalUnit - tour.getNoOfUnit();
		createNewTour();
	}

	private boolean validTourForEdit() {
		if (tourMapUpdate.containsKey(tour.getOccurrence().getId())) {
			return false;
		}
		return true;
	}

	public void editTour() {
		if (validTourForEdit()) {
			tourMap.remove(occurrenceId);
			tourMap.put(tour.getOccurrence().getId(), tour);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, tour.getOccurrence().getDescription());
			createNewTour();
		} else {
			addWranningMessage(null, MessageId.ALREADY_INSERT, tour.getOccurrence().getDescription());
		}
	}

	public void prepareEditTour(Tour tour) {
		this.tour = copyTour(tour);
		tourMapUpdate.putAll(tourMap);
		tourMapUpdate.remove(tour.getOccurrence().getId());
		occurrenceId = tour.getOccurrence().getId();
		createNew = false;
	}

	private Tour copyTour(Tour tour) {
		Tour t = new Tour();
		t.setId(tour.getId());
		t.setOccurrence(tour.getOccurrence());
		t.setPrefix(tour.getPrefix());
		t.setTravelExpress(tour.getTravelExpress());
		t.setExpressDetailList(tour.getExpressDetailList());
		t.setVersion(tour.getVersion());
		return t;
	}

	public List<Tour> getTourList() {
		return new ArrayList<Tour>(tourMap.values());
	}

	public void returnOccurrence(SelectEvent event) {
		Occurrence occurrence = (Occurrence) event.getObject();
		if (createNew) {
			createNewTour();
		}
		this.tour.setOccurrence(occurrence);
	}

	public void createNewExpressDetail() {
		createNew = true;
		expressDetail = new ExpressDetail();
	}

	public void prepareExpressDetail(Tour tour) {
		createNew = false;
		this.tour = tour;
		createNewExpressDetail();
	}

	private boolean validExpressDetail() {
		String formID = "expressListForm";
		boolean valid = true;
		if (isEmpty(expressDetail.getRegistrationNo())) {
			addErrorMessage(formID + ":regNo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (expressDetail.getNoOfPassenger() == 0) {
			addErrorMessage(formID + ":noOfPassenger", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (expressDetail.getNoOfUnit() == 0) {
			addErrorMessage(formID + ":noOfUnit", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(expressDetail.getDate())) {
			addErrorMessage(formID + ":date", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		return valid;
	}

	public void addExpressDetail() {
		if (validExpressDetail()) {
			double totalPremium = travelExpress.getTravelProposal().getProduct().getFixedValue() * expressDetail.getNoOfUnit();
			double commission = Utils.getPercentOf(travelExpress.getTravelProposal().getProduct().getFirstCommission(), totalPremium);
			double netPremium = totalPremium - commission;
			expressDetail.setPremium(totalPremium);
			expressDetail.setCommission(commission);
			expressDetail.setNetPremium(netPremium);
			tour.addExpressDetail(expressDetail);

			tempTotalNetPremium += expressDetail.getNetPremium();
			tempTotalUnit += expressDetail.getNoOfUnit();
			tempTotalPassenger += expressDetail.getNoOfPassenger();
			createNewExpressDetail();
		}
	}

	public void deleteExpressDetail() {
		tour.getExpressDetailList().remove(expressDetail);
		tempTotalNetPremium = tempTotalNetPremium - expressDetail.getNetPremium();
		tempTotalPassenger = tempTotalPassenger - expressDetail.getNoOfPassenger();
		tempTotalUnit = tempTotalUnit - expressDetail.getNoOfUnit();
	}

	public void prepareEditExpressDetail(ExpressDetail eDetail) {
		createNew = false;
		tempTotalNetPremium -= eDetail.getNetPremium();
		tempTotalUnit -= eDetail.getNoOfUnit();
		tempTotalPassenger -= eDetail.getNoOfPassenger();
		this.expressDetail = eDetail;
	}

	public void editExpressDetail() {
		if (validExpressDetail()) {
			double totalPremium = travelExpress.getTravelProposal().getProduct().getFixedValue() * expressDetail.getNoOfUnit();
			double commission = Utils.getPercentOf(travelExpress.getTravelProposal().getProduct().getFirstCommission(), totalPremium);
			double netPremium = totalPremium - commission;
			expressDetail.setPremium(totalPremium);
			expressDetail.setCommission(commission);
			expressDetail.setNetPremium(netPremium);
			tour.addExpressDetail(expressDetail);

			tempTotalNetPremium += expressDetail.getNetPremium();
			tempTotalUnit += expressDetail.getNoOfUnit();
			tempTotalPassenger += expressDetail.getNoOfPassenger();
			createNewExpressDetail();
		}
	}

	public void saveExpressDetailList() {
		for (ExpressDetail expressDetail : tour.getExpressDetailList()) {
			tour.addExpressDetail(expressDetail);
		}
		createNewTour();
	}

	public void updateTravelExpress() {
		if (travelExpress.getNoOfUnit() >= tempTotalUnit) {
			travelExpress.setTourList(getTourList());
			travelProposalService.updateExpress(travelExpress);
			saveExpress = true;
			addInfoMessage(null, MessageId.SAVE_SUCCESS, travelExpress.getExpress().getName());
		} else {
			addInfoMessage(null, MessageId.VALID_NOOFUNIT, travelExpress.getNoOfUnit(), tempTotalUnit);
		}
	}

	public void prepareEntries(Tour tour) {
		this.tour = tour;
	}

	public List<Entry<String, List<ExpressDetail>>> getEntries() {
		Map<String, List<ExpressDetail>> map = new HashMap<String, List<ExpressDetail>>();
		expressDetailForTotalMap = new HashMap<String, ExpressDetail>();
		Date fromDate = tour.getTravelExpress().getTravelProposal().getFromDate();
		Date toDate = tour.getTravelExpress().getTravelProposal().getToDate();
		int diffDate = Utils.daysBetween(fromDate, toDate, false, false);
		double totalNetPremium = 0;
		int totalPassenger = 0;
		int totalUnit = 0;

		for (int i = 0; i <= diffDate; i++) {
			List<ExpressDetail> edList = new ArrayList<ExpressDetail>();
			Date getDate = Utils.plusDays(fromDate, i);
			for (ExpressDetail expressDetail : tour.getExpressDetailList()) {
				if (expressDetail.getDate().compareTo(getDate) == 0) {
					edList.add(expressDetail);
				}
			}
			if (!edList.isEmpty() && edList != null) {
				String strDate = Utils.getDateFormatString(getDate);
				map.put(strDate, edList);

				// for total(netpremium,units,passenger) show expressDetail by
				// date
				ExpressDetail totalEDetail = new ExpressDetail();
				for (ExpressDetail expressDetail : edList) {
					totalNetPremium += expressDetail.getNetPremium();
					totalPassenger += expressDetail.getNoOfPassenger();
					totalUnit += expressDetail.getNoOfUnit();
				}
				totalEDetail.setNetPremium(totalNetPremium);
				totalNetPremium = 0;
				totalEDetail.setNoOfPassenger(totalPassenger);
				totalPassenger = 0;
				totalEDetail.setNoOfUnit(totalUnit);
				totalUnit = 0;
				expressDetailForTotalMap.put(strDate, totalEDetail);
			}
		}
		Map<String, List<ExpressDetail>> sortedMap = new TreeMap(map);
		return new ArrayList<Entry<String, List<ExpressDetail>>>(sortedMap.entrySet());
	}

	public void setTour(Tour tour) {
		this.tour = tour;
	}

	public Tour getTour() {
		return tour;
	}

	public TravelExpress getTravelExpress() {
		return travelExpress;
	}

	public void setTravelExpress(TravelExpress travelExpress) {
		this.travelExpress = travelExpress;
	}

	public Map<String, Tour> getTourMap() {
		return tourMap;
	}

	public void setTourMap(Map<String, Tour> tourMap) {
		this.tourMap = tourMap;
	}

	public ExpressDetail getExpressDetail() {
		return expressDetail;
	}

	public void setExpressDetail(ExpressDetail expressDetail) {
		this.expressDetail = expressDetail;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public int getTempTotalPassenger() {
		return tempTotalPassenger;
	}

	public void setTempTotalPassenger(int tempTotalPassenger) {
		this.tempTotalPassenger = tempTotalPassenger;
	}

	public int getTempTotalUnit() {
		return tempTotalUnit;
	}

	public void setTempTotalUnit(int tempTotalUnit) {
		this.tempTotalUnit = tempTotalUnit;
	}

	public double getTempTotalNetPremium() {
		return tempTotalNetPremium;
	}

	public void setTempTotalNetPremium(double tempTotalNetPremium) {
		this.tempTotalNetPremium = tempTotalNetPremium;
	}

	public Map<String, ExpressDetail> getExpressDetailForTotalMap() {
		return expressDetailForTotalMap;
	}

	public void setExpressDetailForTotalMap(Map<String, ExpressDetail> expressDetailForTotalMap) {
		this.expressDetailForTotalMap = expressDetailForTotalMap;
	}

	public boolean isSaveExpress() {
		return saveExpress;
	}

}
