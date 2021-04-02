package org.ace.insurance.web.common.ntw.mym;

public class TestCase {
	public static void main(String[] args) {
		double value = 15000;
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		System.out.println(convertor.getName(value));
		// String dString = new DecimalFormat("#").format(value);
		// System.out.println(dString.length());
	}
}
