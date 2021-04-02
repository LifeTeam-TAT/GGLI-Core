package org.ace.java.web.common;

import java.io.File;
import java.text.ParseException;
import java.util.List;

public class Test {

	/*
	 * ApprovalServiceException MotorPolicyServiceException
	 * PolicyVehicleInfoServiceException MotorProductServiceException
	 * MotorProposalServiceException MotorProposalWorkFlowServiceException
	 * ProposalHistoryServiceException ProposalVehicleInfoServiceException
	 * PaymentServiceException AgentCommissionDetaillReportServiceException
	 * MotorDailyIncomeReportServiceException MotorPolicyReportServiceException
	 * MotorProposalReportServiceException PremiumPaymentReportServiceException
	 * RoleServiceException AddOnServiceException AgentServiceException
	 * BankServiceException BranchServiceException CityServiceException
	 * CompanyServiceException CountryServiceException CustomerServiceException
	 * DenoServiceException KeyFactorServiceException OccupationServiceException
	 * PaymentTypeServiceException ProvinceServiceException
	 * RelationShipServiceException TownshipServiceException
	 * BuildingClassServiceException FloorServiceException
	 * MainCoverServiceException RoofServiceException WallServiceException
	 * ManufactureServiceException MotorTypeServiceException
	 * TypeOfBodyServiceException VehicleInfoServiceException
	 * UserServiceException
	 */
	public static List<String> printFnames(String sDir, List<String> list) {
		File[] faFiles = new File(sDir).listFiles();
		for (File file : faFiles) {
			if (file.getName().matches("^(.*?)")) {
				if (file.getAbsolutePath().contains("xhtml")) {
					String[] arr = file.getAbsolutePath().split("WebContent");
					list.add(arr[1]);
				}
			}
			if (file.isDirectory()) {
				printFnames(file.getAbsolutePath(), list);
			}
		}

		return list;
	}

	private String name = "Alex";

	public void sayHello() {
		System.out.println("Hello, " + name);
	}

	public void run(String args) {
	}

	public static void main(String[] args) throws ParseException {
		//

	}

	// Pattern whitespace = Pattern.compile("\\s\\s");
	// Matcher matcher = whitespace.matcher(s);
	// if (matcher.find()) {
	// System.out.println("-----------");
	// }
	// String str2 = " Hey there Joey!!! ";
	// System.out.println(str2);
	// System.out.println(str2.trim());
	// Map<String, List<String>> map = new LinkedHashMap<String,
	// List<String>>();
	// map.put("motor", new ArrayList<String>());
	// map.put("fire", new ArrayList<String>());
	// map.put("life", new ArrayList<String>());
	// map.put("system", new ArrayList<String>());
	// map.put("report", new ArrayList<String>());
	// map.put("reversal", new ArrayList<String>());
	// map.put("common", new ArrayList<String>());
	// map.put("agent", new ArrayList<String>());
	// map.put("coinsurance", new ArrayList<String>());
	// map.put("all", new ArrayList<String>());
	// OutputStream op;
	// try {
	// List<String> list =
	// printFnames("D:/dev/ggipWorkspace/ggip/WebContent", new
	// ArrayList<String>());
	// for (String url : list) {
	// if (url.contains("motor") || url.contains("mortor")) {
	// map.get("motor").add(url);
	// } else if (url.contains("fire")) {
	// map.get("fire").add(url);
	// } else if (url.contains("life")) {
	// map.get("life").add(url);
	// } else if (url.contains("system")) {
	// map.get("system").add(url);
	// } else if (url.contains("report")) {
	// map.get("report").add(url);
	// } else if (url.contains("reversal")) {
	// map.get("reversal").add(url);
	// } else if (url.contains("common")) {
	// map.get("common").add(url);
	// } else if (url.contains("agent")) {
	// map.get("agent").add(url);
	// } else if (url.contains("coinsurance")) {
	// map.get("coinsurance").add(url);
	// } else {
	// map.get("all").add(url);
	// }
	// }
	//
	// op = new FileOutputStream("D:/temp/URL.xlsx");
	// XSSFWorkbook wb = new XSSFWorkbook();
	// Sheet sheet = wb.createSheet("Motor");
	// int i = 0;
	// for (String key : map.keySet()) {
	// for (String result : map.get(key)) {
	// Row row = sheet.createRow(i);
	// Cell noCell = row.createCell(0);
	// noCell.setCellValue(key);
	// Cell noCell2 = row.createCell(4);
	// noCell2.setCellValue(result);
	// i++;
	// }
	// }
	// wb.setPrintArea(0, "$A$1:$T$" + (i + 1));
	// wb.write(op);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }

}
