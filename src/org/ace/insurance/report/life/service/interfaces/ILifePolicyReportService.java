package org.ace.insurance.report.life.service.interfaces;

import java.util.List;

import org.ace.insurance.report.life.LifePolicyReport;
import org.ace.insurance.report.life.LifePolicyReportCriteria;
import org.ace.insurance.report.life.ShortTermEndowmentLifeReportCriteria;
import org.ace.insurance.report.personalAccident.PersonalAccidentPolicyReport;
import org.ace.insurance.report.shortEndowLife.NewShortTermEndowmentLifePolicyReport;
import org.ace.insurance.report.shortEndowLife.ShortEndowLifePolicyReport;

public interface ILifePolicyReportService {

	public List<LifePolicyReport> findLifePolicyReport(LifePolicyReportCriteria lifePolicyCriteria, List<String> productIdList);

	public void generateLifePolicyReport(List<LifePolicyReport> reports, String dirPath, String fileName, String branch);

	public void generatePersonalAccidentPolicyReport(List<PersonalAccidentPolicyReport> reports, String dirPath, String fileName);

	public List<PersonalAccidentPolicyReport> findPersonalAccidentPolicyReport(LifePolicyReportCriteria criteria);

	public List<ShortEndowLifePolicyReport> findShortEndowLifePolicyReport(LifePolicyReportCriteria lifePolicyCriteria);

	List<NewShortTermEndowmentLifePolicyReport> findNewShortTermEndowLifePolicyReport(ShortTermEndowmentLifeReportCriteria criteria);

}
