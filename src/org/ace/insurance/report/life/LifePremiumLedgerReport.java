package org.ace.insurance.report.life;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.ace.insurance.common.Utils;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.payment.Payment;

public class LifePremiumLedgerReport {
	private LifePolicy lifePolicy;
	private Date surveyDate;
	private Date nextPaymentDate;
	private List<PremiumLedgerInfo> premiumLedgerInfoList;

	public LifePremiumLedgerReport() {
	}

	public LifePremiumLedgerReport(LifePolicy lifePolicy, List<Payment> paymentList, Date surveyDate) {
		this.lifePolicy = lifePolicy;
		this.surveyDate = surveyDate;
		premiumLedgerInfoList = new ArrayList<PremiumLedgerInfo>();
		PremiumLedgerInfo ledgerInfo = null;
		Date dueDate = lifePolicy.getCommenmanceDate();
		int periodMonths = lifePolicy.getPaymentType().getMonth();
		nextPaymentDate = Utils.getNextPaymentDate(dueDate, lifePolicy.getLastPaymentTerm(), periodMonths, lifePolicy.getPolicyInsuredPersonList().get(0).getPaymentTimes());
		Payment payment;
		Payment prevPayment;
		int previousPaymentTimes;
		int paymentSize = paymentList.size();
		for (int i = 0; i < paymentSize; i++) {
			payment = paymentList.get(i);

			if (i == 0) {
				ledgerInfo = new PremiumLedgerInfo(null, payment.getPaymentDate(), payment.getFromTerm(), payment.getToTerm(), payment.getAmount(), payment.getReceiptNo());
			} else {
				prevPayment = paymentList.get(i - 1);
				previousPaymentTimes = prevPayment.getToTerm() - prevPayment.getFromTerm() + 1;
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dueDate);
				calendar.add(Calendar.MONTH, periodMonths * previousPaymentTimes);
				dueDate = calendar.getTime();
				ledgerInfo = new PremiumLedgerInfo(dueDate, payment.getPaymentDate(), payment.getFromTerm(), payment.getToTerm(), payment.getAmount(), payment.getReceiptNo());
			}
			premiumLedgerInfoList.add(ledgerInfo);
		}
		prevPayment = paymentList.get(paymentSize - 1);
		previousPaymentTimes = prevPayment.getToTerm() - prevPayment.getFromTerm() + 1;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dueDate);
		calendar.add(Calendar.MONTH, periodMonths * previousPaymentTimes);
		dueDate = calendar.getTime();
		ledgerInfo = new PremiumLedgerInfo(dueDate, null, 0, 0, 0.0, "");
		premiumLedgerInfoList.add(ledgerInfo);
		// sorting by receipt date
		Collections.sort(premiumLedgerInfoList, new Comparator<PremiumLedgerInfo>() {
			public int compare(PremiumLedgerInfo o1, PremiumLedgerInfo o2) {
				if (o1.getReceiptDate() == null || o2.getReceiptDate() == null)
					return 0;
				return o1.getReceiptDate().compareTo(o2.getReceiptDate());
			}
		});
	}

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public void setLifePolicy(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
	}

	public List<PremiumLedgerInfo> getPremiumLedgerInfoList() {
		return premiumLedgerInfoList;
	}

	public void setPremiumLedgerInfoList(List<PremiumLedgerInfo> premiumLedgerInfoList) {
		this.premiumLedgerInfoList = premiumLedgerInfoList;
	}

	public Date getSurveyDate() {
		return surveyDate;
	}

	public void setSurveyDate(Date surveyDate) {
		this.surveyDate = surveyDate;
	}

	public Date getNextPaymentDate() {
		return nextPaymentDate;
	}

	public class PremiumLedgerInfo {
		private Date dueDate = null;
		private Date receiptDate;
		private String receiptNo;
		private int fromTerm;
		private int toTerm;
		private double amount;

		public PremiumLedgerInfo() {
		}

		public PremiumLedgerInfo(Date dueDate, Date receiptDate, int fromTerm, int toTerm, double amount, String receiptNo) {
			super();
			this.dueDate = dueDate;
			this.receiptDate = receiptDate;
			this.fromTerm = fromTerm;
			this.toTerm = toTerm;
			this.amount = amount;
			this.receiptNo = receiptNo;
		}

		public Date getDueDate() {
			return dueDate;
		}

		public void setDueDate(Date dueDate) {
			this.dueDate = dueDate;
		}

		public Date getReceiptDate() {
			return receiptDate;
		}

		public void setReceiptDate(Date receiptDate) {
			this.receiptDate = receiptDate;
		}

		public String getReceiptNo() {
			return receiptNo;
		}

		public void setReceiptNo(String receiptNo) {
			this.receiptNo = receiptNo;
		}

		public int getFromTerm() {
			return fromTerm;
		}

		public void setFromTerm(int fromTerm) {
			this.fromTerm = fromTerm;
		}

		public int getToTerm() {
			return toTerm;
		}

		public void setToTerm(int toTerm) {
			this.toTerm = toTerm;
		}

		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}

	}

}
