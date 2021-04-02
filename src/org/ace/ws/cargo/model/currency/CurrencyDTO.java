package org.ace.ws.cargo.model.currency;

import java.io.Serializable;

public class CurrencyDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String prefix;
	private String currencyCode;
	private String description;
	private String symbol;
	private String inwordDesp1;
	private String inwordDesp2;
	private String myanInwordDesp1;
	private String myanInwordDesp2;
	private Boolean isHomeCur;
	private float m1;
	private float m2;
	private float m3;
	private float m4;
	private float m5;
	private float m6;
	private float m7;
	private float m8;
	private float m9;
	private float m10;
	private float m11;
	private float m12;
	private float m13;
	private int version;

	public CurrencyDTO() {

	}

	public CurrencyDTO(String id, String prefix, String currencyCode, String description, String symbol, String inwordDesp1, String inwordDesp2, String myanInwordDesp1,
			String myanInwordDesp2, Boolean isHomeCur, float m1, float m2, float m3, float m4, float m5, float m6, float m7, float m8, float m9, float m10, float m11, float m12,
			float m13, int version) {
		super();
		this.id = id;
		this.prefix = prefix;
		this.currencyCode = currencyCode;
		this.description = description;
		this.symbol = symbol;
		this.inwordDesp1 = inwordDesp1;
		this.inwordDesp2 = inwordDesp2;
		this.myanInwordDesp1 = myanInwordDesp1;
		this.myanInwordDesp2 = myanInwordDesp2;
		this.isHomeCur = isHomeCur;
		this.m1 = m1;
		this.m2 = m2;
		this.m3 = m3;
		this.m4 = m4;
		this.m5 = m5;
		this.m6 = m6;
		this.m7 = m7;
		this.m8 = m8;
		this.m9 = m9;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getInwordDesp1() {
		return inwordDesp1;
	}

	public void setInwordDesp1(String inwordDesp1) {
		this.inwordDesp1 = inwordDesp1;
	}

	public String getInwordDesp2() {
		return inwordDesp2;
	}

	public void setInwordDesp2(String inwordDesp2) {
		this.inwordDesp2 = inwordDesp2;
	}

	public String getMyanInwordDesp1() {
		return myanInwordDesp1;
	}

	public void setMyanInwordDesp1(String myanInwordDesp1) {
		this.myanInwordDesp1 = myanInwordDesp1;
	}

	public String getMyanInwordDesp2() {
		return myanInwordDesp2;
	}

	public void setMyanInwordDesp2(String myanInwordDesp2) {
		this.myanInwordDesp2 = myanInwordDesp2;
	}

	public Boolean getIsHomeCur() {
		return isHomeCur;
	}

	public void setIsHomeCur(Boolean isHomeCur) {
		this.isHomeCur = isHomeCur;
	}

	public float getM1() {
		return m1;
	}

	public void setM1(float m1) {
		this.m1 = m1;
	}

	public float getM2() {
		return m2;
	}

	public void setM2(float m2) {
		this.m2 = m2;
	}

	public float getM3() {
		return m3;
	}

	public void setM3(float m3) {
		this.m3 = m3;
	}

	public float getM4() {
		return m4;
	}

	public void setM4(float m4) {
		this.m4 = m4;
	}

	public float getM5() {
		return m5;
	}

	public void setM5(float m5) {
		this.m5 = m5;
	}

	public float getM6() {
		return m6;
	}

	public void setM6(float m6) {
		this.m6 = m6;
	}

	public float getM7() {
		return m7;
	}

	public void setM7(float m7) {
		this.m7 = m7;
	}

	public float getM8() {
		return m8;
	}

	public void setM8(float m8) {
		this.m8 = m8;
	}

	public float getM9() {
		return m9;
	}

	public void setM9(float m9) {
		this.m9 = m9;
	}

	public float getM10() {
		return m10;
	}

	public void setM10(float m10) {
		this.m10 = m10;
	}

	public float getM11() {
		return m11;
	}

	public void setM11(float m11) {
		this.m11 = m11;
	}

	public float getM12() {
		return m12;
	}

	public void setM12(float m12) {
		this.m12 = m12;
	}

	public float getM13() {
		return m13;
	}

	public void setM13(float m13) {
		this.m13 = m13;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
