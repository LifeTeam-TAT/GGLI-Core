package org.ace.insurance.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ace.java.component.SystemException;

public class CommonSettingConfig {

	private static String TOTAL_HOSPITALIZATION_DAYS = "TOTAL_HOSPITALIZATION_DAYS";
	private static String TOTAL_OPERATION_AMOUNT = "TOTAL_OPERATION_AMOUNT";
	private static String TOTAL_MEDICATION_AMOUNT = "TOTAL_MEDICATION_AMOUNT";
	private static String MAXIMUN_MEDICAL_AGE = "MAXIMUN_MEDICAL_AGE";
	private static String MAXIMUN_CRITICAL_ILLNESS_AGE = "MAXIMUN_CRITICAL_ILLNESS_AGE";
	private static String MINIMUN_CRITICAL_ILLNESS_AGE = "MINIMUN_CRITICAL_ILLNESS_AGE";
	private static String MINIMUN_MEDICAL_AGE = "MINIMUN_MEDICAL_AGE";
	private static String MEDI_INSU_TERM = "MEDI_INSU_TERM";

	private static String MINIMUM_GUARDIAN_AGE = "MINIMUM_GUARDIAN_AGE";

	private static String DEATH_CLAIM_BASED = "DEATH_CLAIM_BASED";
	private static String DEATH_CLAIM_BASICPLUS = "DEATH_CLAIM_BASICPLUS";
	private static String MICRO_DEATH_CLAIM_BASED = "MICRO_DEATH_CLAIM_BASED";
	private static String DEATH_CLAIM_NOT_ACCIDENT = "DEATH_CLAIM_NOT_ACCIDENT";

	private static String HOSP_CALIM_BASED = "HOSP_CALIM_BASED";
	private static String HOSP_CALIM_BASICPLUS = "HOSP_CALIM_BASICPLUS";
	private static String HOSP_CALIM_ADDON2 = "HOSP_CALIM_ADDON2";
	private static String CRITICAL_HOSP_CALIM_BASED = "CRITICAL_HOSP_CALIM_BASED";
	private static String MICRO_HOSP_CALIM_BASED = "MICRO_HOSP_CALIM_BASED";

	private static String OPERATION_CLAIM_ONE = "OPERATION_CLAIM_ONE";
	private static String OPERATION_CLAIM_TWO = "OPERATION_CLAIM_TWO";

	private static String ABORTION_CLAIM_ONE = "ABORTION_CLAIM_ONE";

	private static String DISABILITY_CLAIM_BASICPLUS = "DISABILITY_CLAIM_BASICPLUS";
	private static String DISABILITY_CLAIM_BASED = "DISABILITY_CLAIM_BASED";

	private static Properties commonSettingConfig;

	static {
		try {
			commonSettingConfig = new Properties();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream in = classLoader.getResourceAsStream("common_setting_config.properties");
			commonSettingConfig.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load common_setting_config.properties");
		}
	}

	public static int getTotalHospitalizationDays() {
		return Integer.parseInt(commonSettingConfig.getProperty(TOTAL_HOSPITALIZATION_DAYS));
	}

	public static int getTotalOperationAmount() {
		return Integer.parseInt(commonSettingConfig.getProperty(TOTAL_OPERATION_AMOUNT));
	}

	public static int getTotalMedicationAmount() {
		return Integer.parseInt(commonSettingConfig.getProperty(TOTAL_MEDICATION_AMOUNT));
	}

	public static int getMaximunMedicalAge() {
		return Integer.parseInt(commonSettingConfig.getProperty(MAXIMUN_MEDICAL_AGE));
	}

	public static int getMinimumCriticalIllnessAge() {
		return Integer.parseInt(commonSettingConfig.getProperty(MINIMUN_CRITICAL_ILLNESS_AGE));
	}

	public static int getMaximunCriticalIllnessAge() {
		return Integer.parseInt(commonSettingConfig.getProperty(MAXIMUN_CRITICAL_ILLNESS_AGE));
	}

	public static int getMinimunMedicalAge() {
		return Integer.parseInt(commonSettingConfig.getProperty(MINIMUN_MEDICAL_AGE));
	}

	public static int getMinimumGuardianAge() {
		return Integer.parseInt(commonSettingConfig.getProperty(MINIMUM_GUARDIAN_AGE));
	}

	public static int getMedicalInsuTerm() {
		return Integer.parseInt(commonSettingConfig.getProperty(MEDI_INSU_TERM));
	}

	public static String getMaxUnit() {
		return commonSettingConfig.getProperty("MAX_UNIT");
	}

	public static double getDeathClaimAmountForBased() {
		return Double.parseDouble(commonSettingConfig.getProperty(DEATH_CLAIM_BASED));
	}

	public static int getDeathClaimAmountForBasicPlus() {
		return Integer.parseInt(commonSettingConfig.getProperty(DEATH_CLAIM_BASICPLUS));
	}

	public static double getMicroDeathClaimAmountForBased() {
		return Double.parseDouble(commonSettingConfig.getProperty(MICRO_DEATH_CLAIM_BASED));
	}

	public static int getDeathClaimAmountForNotAccident() {
		return Integer.parseInt(commonSettingConfig.getProperty(DEATH_CLAIM_NOT_ACCIDENT));
	}

	public static double getHospitalizedClaimAmountForBased() {
		return Double.parseDouble(commonSettingConfig.getProperty(HOSP_CALIM_BASED));
	}

	public static double getCriticalHospitalizedClaimAmountForBased() {
		return Double.parseDouble(commonSettingConfig.getProperty(CRITICAL_HOSP_CALIM_BASED));
	}

	public static double getMicroHospitalizedClaimAmountForBased() {
		return Double.parseDouble(commonSettingConfig.getProperty(MICRO_HOSP_CALIM_BASED));
	}

	public static double getHospitalizedClaimAmountForBasicPlus() {
		return Double.parseDouble(commonSettingConfig.getProperty(HOSP_CALIM_BASICPLUS));
	}

	public static double getOperationClaimForAddOnOne() {
		return Double.parseDouble(commonSettingConfig.getProperty(OPERATION_CLAIM_ONE));
	}

	public static double getOperationClaimForAddOnTwo() {
		return Double.parseDouble(commonSettingConfig.getProperty(OPERATION_CLAIM_TWO));
	}

	public static double getAbortionClaimForAddOnOne() {
		return Double.parseDouble(commonSettingConfig.getProperty(ABORTION_CLAIM_ONE));
	}

	public static double getHospitalizedClaimAmountForAddOnTwo() {
		return Double.parseDouble(commonSettingConfig.getProperty(HOSP_CALIM_ADDON2));
	}

	public static double getDisabilityAmountForBased() {
		return Double.parseDouble(commonSettingConfig.getProperty(DISABILITY_CLAIM_BASED));
	}

	public static double getDisabilityAmountForBasicPlus() {
		return Double.parseDouble(commonSettingConfig.getProperty(DISABILITY_CLAIM_BASICPLUS));
	}

}
