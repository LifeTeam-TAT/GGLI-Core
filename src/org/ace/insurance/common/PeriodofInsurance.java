package org.ace.insurance.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "PeriodofInsurance")
@XmlEnum
public enum PeriodofInsurance {
	
    Month(1), Week(1), Year(1);
    private final int value;

    private PeriodofInsurance(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
