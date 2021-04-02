package org.ace.insurance.medical.productprocess;

public enum OptionType {
	DECLARATION("Declaration"), GP_A_OPTION_2_U_35(" Gp A, Option 2, U 35"), GP_A_OPTION_2_A_35(" Gp A, Option 2, A 35"), GP_B_OPTION_1_A_35(
			"Gp B, Option 1, A 35"), GP_B_OPTION_2_U_35(
					"Gp B, Option 2, U 35"), GP_B_OPTION_2_A_35("Gp B, Option 2, A 35"), GP_C_OPTION_2_U_35("Gp C, Option 2, U 35"), GP_D_OPTION_2_U_35("Gp D, Option 2, U 35");

	private String label;

	private OptionType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
