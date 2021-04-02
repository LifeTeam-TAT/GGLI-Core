package org.ace.insurance.system.common.account;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ace.insurance.common.TableName;

@Entity
@Table(name = TableName.CCOA)
public class CurrencyChartOfAccount implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	private String id;

	private String coaid;

	@Column(name = "DEPARTMENTID")
	private String dCODE;

	@Column(name = "CURRENCYID")
	private String cUR;

	private String branchID;

	private BigDecimal bF;

	private BigDecimal oBAL;

	private BigDecimal hOBAL;

	private BigDecimal cBAL;

	private BigDecimal m1;

	private BigDecimal m2;

	private BigDecimal m3;

	private BigDecimal m4;

	private BigDecimal m5;

	private BigDecimal m6;

	private BigDecimal m7;

	private BigDecimal m8;

	private BigDecimal m9;

	private BigDecimal m10;

	private BigDecimal m11;

	private BigDecimal m12;

	private BigDecimal m13;

	private BigDecimal mSRC1;

	private BigDecimal mSRC2;

	private BigDecimal mSRC3;

	private BigDecimal mSRC4;

	private BigDecimal mSRC5;

	private BigDecimal mSRC6;

	private BigDecimal mSRC7;

	private BigDecimal mSRC8;

	private BigDecimal mSRC9;

	private BigDecimal mSRC10;

	private BigDecimal mSRC11;

	private BigDecimal mSRC12;

	private BigDecimal mSRC13;

	private BigDecimal bF1;

	private BigDecimal bF2;

	private BigDecimal bF3;

	private BigDecimal bF4;

	private BigDecimal bF5;

	private BigDecimal bF6;

	private BigDecimal bF7;

	private BigDecimal bF8;

	private BigDecimal bF9;

	private BigDecimal bF10;

	private BigDecimal bF11;

	private BigDecimal bF12;

	private BigDecimal bF13;

	private BigDecimal bFSRC1;

	private BigDecimal bFSRC2;

	private BigDecimal bFSRC3;

	private BigDecimal bFSRC4;

	private BigDecimal bFSRC5;

	private BigDecimal bFSRC6;

	private BigDecimal bFSRC7;

	private BigDecimal bFSRC8;

	private BigDecimal bFSRC9;

	private BigDecimal bFSRC10;

	private BigDecimal bFSRC11;

	private BigDecimal bFSRC12;

	private BigDecimal bFSRC13;

	private BigDecimal mREV1;

	private BigDecimal mREV2;

	private BigDecimal mREV3;

	private BigDecimal mREV4;

	private BigDecimal mREV5;

	private BigDecimal mREV6;

	private BigDecimal mREV7;

	private BigDecimal mREV8;

	private BigDecimal mREV9;

	private BigDecimal mREV10;

	private BigDecimal mREV11;

	private BigDecimal mREV12;

	private BigDecimal mREV13;

	private BigDecimal lYMSRC1;

	private BigDecimal lYMSRC2;

	private BigDecimal lYMSRC3;

	private BigDecimal lYMSRC4;

	private BigDecimal lYMSRC5;

	private BigDecimal lYMSRC6;

	private BigDecimal lYMSRC7;

	private BigDecimal lYMSRC8;

	private BigDecimal lYMSRC9;

	private BigDecimal lYMSRC10;

	private BigDecimal lYMSRC11;

	private BigDecimal lYMSRC12;

	private BigDecimal lYMSRC13;

	private BigDecimal sCCRBAL;

	private BigDecimal aCCRUED;

	private String bUDGET;

	private String aCNAME;

	public String getdCODE() {
		return dCODE;
	}

	public void setdCODE(String dCODE) {
		this.dCODE = dCODE;
	}

	public String getcUR() {
		return cUR;
	}

	public void setcUR(String cUR) {
		this.cUR = cUR;
	}

	public String getBranchID() {
		return branchID;
	}

	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}

	public BigDecimal getbF() {
		return bF;
	}

	public void setbF(BigDecimal bF) {
		this.bF = bF;
	}

	public BigDecimal getoBAL() {
		return oBAL;
	}

	public void setoBAL(BigDecimal oBAL) {
		this.oBAL = oBAL;
	}

	public BigDecimal gethOBAL() {
		return hOBAL;
	}

	public void sethOBAL(BigDecimal hOBAL) {
		this.hOBAL = hOBAL;
	}

	public BigDecimal getcBAL() {
		return cBAL;
	}

	public void setcBAL(BigDecimal cBAL) {
		this.cBAL = cBAL;
	}

	public BigDecimal getM1() {
		return m1;
	}

	public void setM1(BigDecimal m1) {
		this.m1 = m1;
	}

	public BigDecimal getM2() {
		return m2;
	}

	public void setM2(BigDecimal m2) {
		this.m2 = m2;
	}

	public BigDecimal getM3() {
		return m3;
	}

	public void setM3(BigDecimal m3) {
		this.m3 = m3;
	}

	public BigDecimal getM4() {
		return m4;
	}

	public void setM4(BigDecimal m4) {
		this.m4 = m4;
	}

	public BigDecimal getM5() {
		return m5;
	}

	public void setM5(BigDecimal m5) {
		this.m5 = m5;
	}

	public BigDecimal getM6() {
		return m6;
	}

	public void setM6(BigDecimal m6) {
		this.m6 = m6;
	}

	public BigDecimal getM7() {
		return m7;
	}

	public void setM7(BigDecimal m7) {
		this.m7 = m7;
	}

	public BigDecimal getM8() {
		return m8;
	}

	public void setM8(BigDecimal m8) {
		this.m8 = m8;
	}

	public BigDecimal getM9() {
		return m9;
	}

	public void setM9(BigDecimal m9) {
		this.m9 = m9;
	}

	public BigDecimal getM10() {
		return m10;
	}

	public void setM10(BigDecimal m10) {
		this.m10 = m10;
	}

	public BigDecimal getM11() {
		return m11;
	}

	public void setM11(BigDecimal m11) {
		this.m11 = m11;
	}

	public BigDecimal getM12() {
		return m12;
	}

	public void setM12(BigDecimal m12) {
		this.m12 = m12;
	}

	public BigDecimal getM13() {
		return m13;
	}

	public void setM13(BigDecimal m13) {
		this.m13 = m13;
	}

	public BigDecimal getmSRC1() {
		return mSRC1;
	}

	public void setmSRC1(BigDecimal mSRC1) {
		this.mSRC1 = mSRC1;
	}

	public BigDecimal getmSRC2() {
		return mSRC2;
	}

	public void setmSRC2(BigDecimal mSRC2) {
		this.mSRC2 = mSRC2;
	}

	public BigDecimal getmSRC3() {
		return mSRC3;
	}

	public void setmSRC3(BigDecimal mSRC3) {
		this.mSRC3 = mSRC3;
	}

	public BigDecimal getmSRC4() {
		return mSRC4;
	}

	public void setmSRC4(BigDecimal mSRC4) {
		this.mSRC4 = mSRC4;
	}

	public BigDecimal getmSRC5() {
		return mSRC5;
	}

	public void setmSRC5(BigDecimal mSRC5) {
		this.mSRC5 = mSRC5;
	}

	public BigDecimal getmSRC6() {
		return mSRC6;
	}

	public void setmSRC6(BigDecimal mSRC6) {
		this.mSRC6 = mSRC6;
	}

	public BigDecimal getmSRC7() {
		return mSRC7;
	}

	public void setmSRC7(BigDecimal mSRC7) {
		this.mSRC7 = mSRC7;
	}

	public BigDecimal getmSRC8() {
		return mSRC8;
	}

	public void setmSRC8(BigDecimal mSRC8) {
		this.mSRC8 = mSRC8;
	}

	public BigDecimal getmSRC9() {
		return mSRC9;
	}

	public void setmSRC9(BigDecimal mSRC9) {
		this.mSRC9 = mSRC9;
	}

	public BigDecimal getmSRC10() {
		return mSRC10;
	}

	public void setmSRC10(BigDecimal mSRC10) {
		this.mSRC10 = mSRC10;
	}

	public BigDecimal getmSRC11() {
		return mSRC11;
	}

	public void setmSRC11(BigDecimal mSRC11) {
		this.mSRC11 = mSRC11;
	}

	public BigDecimal getmSRC12() {
		return mSRC12;
	}

	public void setmSRC12(BigDecimal mSRC12) {
		this.mSRC12 = mSRC12;
	}

	public BigDecimal getmSRC13() {
		return mSRC13;
	}

	public void setmSRC13(BigDecimal mSRC13) {
		this.mSRC13 = mSRC13;
	}

	public BigDecimal getbF1() {
		return bF1;
	}

	public void setbF1(BigDecimal bF1) {
		this.bF1 = bF1;
	}

	public BigDecimal getbF2() {
		return bF2;
	}

	public void setbF2(BigDecimal bF2) {
		this.bF2 = bF2;
	}

	public BigDecimal getbF3() {
		return bF3;
	}

	public void setbF3(BigDecimal bF3) {
		this.bF3 = bF3;
	}

	public BigDecimal getbF4() {
		return bF4;
	}

	public void setbF4(BigDecimal bF4) {
		this.bF4 = bF4;
	}

	public BigDecimal getbF5() {
		return bF5;
	}

	public void setbF5(BigDecimal bF5) {
		this.bF5 = bF5;
	}

	public BigDecimal getbF6() {
		return bF6;
	}

	public void setbF6(BigDecimal bF6) {
		this.bF6 = bF6;
	}

	public BigDecimal getbF7() {
		return bF7;
	}

	public void setbF7(BigDecimal bF7) {
		this.bF7 = bF7;
	}

	public BigDecimal getbF8() {
		return bF8;
	}

	public void setbF8(BigDecimal bF8) {
		this.bF8 = bF8;
	}

	public BigDecimal getbF9() {
		return bF9;
	}

	public void setbF9(BigDecimal bF9) {
		this.bF9 = bF9;
	}

	public BigDecimal getbF10() {
		return bF10;
	}

	public void setbF10(BigDecimal bF10) {
		this.bF10 = bF10;
	}

	public BigDecimal getbF11() {
		return bF11;
	}

	public void setbF11(BigDecimal bF11) {
		this.bF11 = bF11;
	}

	public BigDecimal getbF12() {
		return bF12;
	}

	public void setbF12(BigDecimal bF12) {
		this.bF12 = bF12;
	}

	public BigDecimal getbF13() {
		return bF13;
	}

	public void setbF13(BigDecimal bF13) {
		this.bF13 = bF13;
	}

	public BigDecimal getbFSRC1() {
		return bFSRC1;
	}

	public void setbFSRC1(BigDecimal bFSRC1) {
		this.bFSRC1 = bFSRC1;
	}

	public BigDecimal getbFSRC2() {
		return bFSRC2;
	}

	public void setbFSRC2(BigDecimal bFSRC2) {
		this.bFSRC2 = bFSRC2;
	}

	public BigDecimal getbFSRC3() {
		return bFSRC3;
	}

	public void setbFSRC3(BigDecimal bFSRC3) {
		this.bFSRC3 = bFSRC3;
	}

	public BigDecimal getbFSRC4() {
		return bFSRC4;
	}

	public void setbFSRC4(BigDecimal bFSRC4) {
		this.bFSRC4 = bFSRC4;
	}

	public BigDecimal getbFSRC5() {
		return bFSRC5;
	}

	public void setbFSRC5(BigDecimal bFSRC5) {
		this.bFSRC5 = bFSRC5;
	}

	public BigDecimal getbFSRC6() {
		return bFSRC6;
	}

	public void setbFSRC6(BigDecimal bFSRC6) {
		this.bFSRC6 = bFSRC6;
	}

	public BigDecimal getbFSRC7() {
		return bFSRC7;
	}

	public void setbFSRC7(BigDecimal bFSRC7) {
		this.bFSRC7 = bFSRC7;
	}

	public BigDecimal getbFSRC8() {
		return bFSRC8;
	}

	public void setbFSRC8(BigDecimal bFSRC8) {
		this.bFSRC8 = bFSRC8;
	}

	public BigDecimal getbFSRC9() {
		return bFSRC9;
	}

	public void setbFSRC9(BigDecimal bFSRC9) {
		this.bFSRC9 = bFSRC9;
	}

	public BigDecimal getbFSRC10() {
		return bFSRC10;
	}

	public void setbFSRC10(BigDecimal bFSRC10) {
		this.bFSRC10 = bFSRC10;
	}

	public BigDecimal getbFSRC11() {
		return bFSRC11;
	}

	public void setbFSRC11(BigDecimal bFSRC11) {
		this.bFSRC11 = bFSRC11;
	}

	public BigDecimal getbFSRC12() {
		return bFSRC12;
	}

	public void setbFSRC12(BigDecimal bFSRC12) {
		this.bFSRC12 = bFSRC12;
	}

	public BigDecimal getbFSRC13() {
		return bFSRC13;
	}

	public void setbFSRC13(BigDecimal bFSRC13) {
		this.bFSRC13 = bFSRC13;
	}

	public BigDecimal getmREV1() {
		return mREV1;
	}

	public void setmREV1(BigDecimal mREV1) {
		this.mREV1 = mREV1;
	}

	public BigDecimal getmREV2() {
		return mREV2;
	}

	public void setmREV2(BigDecimal mREV2) {
		this.mREV2 = mREV2;
	}

	public BigDecimal getmREV3() {
		return mREV3;
	}

	public void setmREV3(BigDecimal mREV3) {
		this.mREV3 = mREV3;
	}

	public BigDecimal getmREV4() {
		return mREV4;
	}

	public void setmREV4(BigDecimal mREV4) {
		this.mREV4 = mREV4;
	}

	public BigDecimal getmREV5() {
		return mREV5;
	}

	public void setmREV5(BigDecimal mREV5) {
		this.mREV5 = mREV5;
	}

	public BigDecimal getmREV6() {
		return mREV6;
	}

	public void setmREV6(BigDecimal mREV6) {
		this.mREV6 = mREV6;
	}

	public BigDecimal getmREV7() {
		return mREV7;
	}

	public void setmREV7(BigDecimal mREV7) {
		this.mREV7 = mREV7;
	}

	public BigDecimal getmREV8() {
		return mREV8;
	}

	public void setmREV8(BigDecimal mREV8) {
		this.mREV8 = mREV8;
	}

	public BigDecimal getmREV9() {
		return mREV9;
	}

	public void setmREV9(BigDecimal mREV9) {
		this.mREV9 = mREV9;
	}

	public BigDecimal getmREV10() {
		return mREV10;
	}

	public void setmREV10(BigDecimal mREV10) {
		this.mREV10 = mREV10;
	}

	public BigDecimal getmREV11() {
		return mREV11;
	}

	public void setmREV11(BigDecimal mREV11) {
		this.mREV11 = mREV11;
	}

	public BigDecimal getmREV12() {
		return mREV12;
	}

	public void setmREV12(BigDecimal mREV12) {
		this.mREV12 = mREV12;
	}

	public BigDecimal getmREV13() {
		return mREV13;
	}

	public void setmREV13(BigDecimal mREV13) {
		this.mREV13 = mREV13;
	}

	public BigDecimal getlYMSRC1() {
		return lYMSRC1;
	}

	public void setlYMSRC1(BigDecimal lYMSRC1) {
		this.lYMSRC1 = lYMSRC1;
	}

	public BigDecimal getlYMSRC2() {
		return lYMSRC2;
	}

	public void setlYMSRC2(BigDecimal lYMSRC2) {
		this.lYMSRC2 = lYMSRC2;
	}

	public BigDecimal getlYMSRC3() {
		return lYMSRC3;
	}

	public void setlYMSRC3(BigDecimal lYMSRC3) {
		this.lYMSRC3 = lYMSRC3;
	}

	public BigDecimal getlYMSRC4() {
		return lYMSRC4;
	}

	public void setlYMSRC4(BigDecimal lYMSRC4) {
		this.lYMSRC4 = lYMSRC4;
	}

	public BigDecimal getlYMSRC5() {
		return lYMSRC5;
	}

	public void setlYMSRC5(BigDecimal lYMSRC5) {
		this.lYMSRC5 = lYMSRC5;
	}

	public BigDecimal getlYMSRC6() {
		return lYMSRC6;
	}

	public void setlYMSRC6(BigDecimal lYMSRC6) {
		this.lYMSRC6 = lYMSRC6;
	}

	public BigDecimal getlYMSRC7() {
		return lYMSRC7;
	}

	public void setlYMSRC7(BigDecimal lYMSRC7) {
		this.lYMSRC7 = lYMSRC7;
	}

	public BigDecimal getlYMSRC8() {
		return lYMSRC8;
	}

	public void setlYMSRC8(BigDecimal lYMSRC8) {
		this.lYMSRC8 = lYMSRC8;
	}

	public BigDecimal getlYMSRC9() {
		return lYMSRC9;
	}

	public void setlYMSRC9(BigDecimal lYMSRC9) {
		this.lYMSRC9 = lYMSRC9;
	}

	public BigDecimal getlYMSRC10() {
		return lYMSRC10;
	}

	public void setlYMSRC10(BigDecimal lYMSRC10) {
		this.lYMSRC10 = lYMSRC10;
	}

	public BigDecimal getlYMSRC11() {
		return lYMSRC11;
	}

	public void setlYMSRC11(BigDecimal lYMSRC11) {
		this.lYMSRC11 = lYMSRC11;
	}

	public BigDecimal getlYMSRC12() {
		return lYMSRC12;
	}

	public void setlYMSRC12(BigDecimal lYMSRC12) {
		this.lYMSRC12 = lYMSRC12;
	}

	public BigDecimal getlYMSRC13() {
		return lYMSRC13;
	}

	public void setlYMSRC13(BigDecimal lYMSRC13) {
		this.lYMSRC13 = lYMSRC13;
	}

	public BigDecimal getsCCRBAL() {
		return sCCRBAL;
	}

	public void setsCCRBAL(BigDecimal sCCRBAL) {
		this.sCCRBAL = sCCRBAL;
	}

	public BigDecimal getaCCRUED() {
		return aCCRUED;
	}

	public void setaCCRUED(BigDecimal aCCRUED) {
		this.aCCRUED = aCCRUED;
	}

	public String getbUDGET() {
		return bUDGET;
	}

	public void setbUDGET(String bUDGET) {
		this.bUDGET = bUDGET;
	}

	public String getaCNAME() {
		return aCNAME;
	}

	public void setaCNAME(String aCNAME) {
		this.aCNAME = aCNAME;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCoaid() {
		return coaid;
	}

	public void setCoaid(String coaid) {
		this.coaid = coaid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aCCRUED == null) ? 0 : aCCRUED.hashCode());
		result = prime * result + ((aCNAME == null) ? 0 : aCNAME.hashCode());
		result = prime * result + ((bF == null) ? 0 : bF.hashCode());
		result = prime * result + ((bF1 == null) ? 0 : bF1.hashCode());
		result = prime * result + ((bF10 == null) ? 0 : bF10.hashCode());
		result = prime * result + ((bF11 == null) ? 0 : bF11.hashCode());
		result = prime * result + ((bF12 == null) ? 0 : bF12.hashCode());
		result = prime * result + ((bF13 == null) ? 0 : bF13.hashCode());
		result = prime * result + ((bF2 == null) ? 0 : bF2.hashCode());
		result = prime * result + ((bF3 == null) ? 0 : bF3.hashCode());
		result = prime * result + ((bF4 == null) ? 0 : bF4.hashCode());
		result = prime * result + ((bF5 == null) ? 0 : bF5.hashCode());
		result = prime * result + ((bF6 == null) ? 0 : bF6.hashCode());
		result = prime * result + ((bF7 == null) ? 0 : bF7.hashCode());
		result = prime * result + ((bF8 == null) ? 0 : bF8.hashCode());
		result = prime * result + ((bF9 == null) ? 0 : bF9.hashCode());
		result = prime * result + ((bFSRC1 == null) ? 0 : bFSRC1.hashCode());
		result = prime * result + ((bFSRC10 == null) ? 0 : bFSRC10.hashCode());
		result = prime * result + ((bFSRC11 == null) ? 0 : bFSRC11.hashCode());
		result = prime * result + ((bFSRC12 == null) ? 0 : bFSRC12.hashCode());
		result = prime * result + ((bFSRC13 == null) ? 0 : bFSRC13.hashCode());
		result = prime * result + ((bFSRC2 == null) ? 0 : bFSRC2.hashCode());
		result = prime * result + ((bFSRC3 == null) ? 0 : bFSRC3.hashCode());
		result = prime * result + ((bFSRC4 == null) ? 0 : bFSRC4.hashCode());
		result = prime * result + ((bFSRC5 == null) ? 0 : bFSRC5.hashCode());
		result = prime * result + ((bFSRC6 == null) ? 0 : bFSRC6.hashCode());
		result = prime * result + ((bFSRC7 == null) ? 0 : bFSRC7.hashCode());
		result = prime * result + ((bFSRC8 == null) ? 0 : bFSRC8.hashCode());
		result = prime * result + ((bFSRC9 == null) ? 0 : bFSRC9.hashCode());
		result = prime * result + ((bUDGET == null) ? 0 : bUDGET.hashCode());
		result = prime * result + ((branchID == null) ? 0 : branchID.hashCode());
		result = prime * result + ((cBAL == null) ? 0 : cBAL.hashCode());
		result = prime * result + ((cUR == null) ? 0 : cUR.hashCode());
		result = prime * result + ((dCODE == null) ? 0 : dCODE.hashCode());
		result = prime * result + ((hOBAL == null) ? 0 : hOBAL.hashCode());
		result = prime * result + ((lYMSRC1 == null) ? 0 : lYMSRC1.hashCode());
		result = prime * result + ((lYMSRC10 == null) ? 0 : lYMSRC10.hashCode());
		result = prime * result + ((lYMSRC11 == null) ? 0 : lYMSRC11.hashCode());
		result = prime * result + ((lYMSRC12 == null) ? 0 : lYMSRC12.hashCode());
		result = prime * result + ((lYMSRC13 == null) ? 0 : lYMSRC13.hashCode());
		result = prime * result + ((lYMSRC2 == null) ? 0 : lYMSRC2.hashCode());
		result = prime * result + ((lYMSRC3 == null) ? 0 : lYMSRC3.hashCode());
		result = prime * result + ((lYMSRC4 == null) ? 0 : lYMSRC4.hashCode());
		result = prime * result + ((lYMSRC5 == null) ? 0 : lYMSRC5.hashCode());
		result = prime * result + ((lYMSRC6 == null) ? 0 : lYMSRC6.hashCode());
		result = prime * result + ((lYMSRC7 == null) ? 0 : lYMSRC7.hashCode());
		result = prime * result + ((lYMSRC8 == null) ? 0 : lYMSRC8.hashCode());
		result = prime * result + ((lYMSRC9 == null) ? 0 : lYMSRC9.hashCode());
		result = prime * result + ((m1 == null) ? 0 : m1.hashCode());
		result = prime * result + ((m10 == null) ? 0 : m10.hashCode());
		result = prime * result + ((m11 == null) ? 0 : m11.hashCode());
		result = prime * result + ((m12 == null) ? 0 : m12.hashCode());
		result = prime * result + ((m13 == null) ? 0 : m13.hashCode());
		result = prime * result + ((m2 == null) ? 0 : m2.hashCode());
		result = prime * result + ((m3 == null) ? 0 : m3.hashCode());
		result = prime * result + ((m4 == null) ? 0 : m4.hashCode());
		result = prime * result + ((m5 == null) ? 0 : m5.hashCode());
		result = prime * result + ((m6 == null) ? 0 : m6.hashCode());
		result = prime * result + ((m7 == null) ? 0 : m7.hashCode());
		result = prime * result + ((m8 == null) ? 0 : m8.hashCode());
		result = prime * result + ((m9 == null) ? 0 : m9.hashCode());
		result = prime * result + ((mREV1 == null) ? 0 : mREV1.hashCode());
		result = prime * result + ((mREV10 == null) ? 0 : mREV10.hashCode());
		result = prime * result + ((mREV11 == null) ? 0 : mREV11.hashCode());
		result = prime * result + ((mREV12 == null) ? 0 : mREV12.hashCode());
		result = prime * result + ((mREV13 == null) ? 0 : mREV13.hashCode());
		result = prime * result + ((mREV2 == null) ? 0 : mREV2.hashCode());
		result = prime * result + ((mREV3 == null) ? 0 : mREV3.hashCode());
		result = prime * result + ((mREV4 == null) ? 0 : mREV4.hashCode());
		result = prime * result + ((mREV5 == null) ? 0 : mREV5.hashCode());
		result = prime * result + ((mREV6 == null) ? 0 : mREV6.hashCode());
		result = prime * result + ((mREV7 == null) ? 0 : mREV7.hashCode());
		result = prime * result + ((mREV8 == null) ? 0 : mREV8.hashCode());
		result = prime * result + ((mREV9 == null) ? 0 : mREV9.hashCode());
		result = prime * result + ((mSRC1 == null) ? 0 : mSRC1.hashCode());
		result = prime * result + ((mSRC10 == null) ? 0 : mSRC10.hashCode());
		result = prime * result + ((mSRC11 == null) ? 0 : mSRC11.hashCode());
		result = prime * result + ((mSRC12 == null) ? 0 : mSRC12.hashCode());
		result = prime * result + ((mSRC13 == null) ? 0 : mSRC13.hashCode());
		result = prime * result + ((mSRC2 == null) ? 0 : mSRC2.hashCode());
		result = prime * result + ((mSRC3 == null) ? 0 : mSRC3.hashCode());
		result = prime * result + ((mSRC4 == null) ? 0 : mSRC4.hashCode());
		result = prime * result + ((mSRC5 == null) ? 0 : mSRC5.hashCode());
		result = prime * result + ((mSRC6 == null) ? 0 : mSRC6.hashCode());
		result = prime * result + ((mSRC7 == null) ? 0 : mSRC7.hashCode());
		result = prime * result + ((mSRC8 == null) ? 0 : mSRC8.hashCode());
		result = prime * result + ((mSRC9 == null) ? 0 : mSRC9.hashCode());
		result = prime * result + ((oBAL == null) ? 0 : oBAL.hashCode());
		result = prime * result + ((sCCRBAL == null) ? 0 : sCCRBAL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CurrencyChartOfAccount other = (CurrencyChartOfAccount) obj;
		if (aCCRUED == null) {
			if (other.aCCRUED != null)
				return false;
		} else if (!aCCRUED.equals(other.aCCRUED))
			return false;
		if (aCNAME == null) {
			if (other.aCNAME != null)
				return false;
		} else if (!aCNAME.equals(other.aCNAME))
			return false;
		if (bF == null) {
			if (other.bF != null)
				return false;
		} else if (!bF.equals(other.bF))
			return false;
		if (bF1 == null) {
			if (other.bF1 != null)
				return false;
		} else if (!bF1.equals(other.bF1))
			return false;
		if (bF10 == null) {
			if (other.bF10 != null)
				return false;
		} else if (!bF10.equals(other.bF10))
			return false;
		if (bF11 == null) {
			if (other.bF11 != null)
				return false;
		} else if (!bF11.equals(other.bF11))
			return false;
		if (bF12 == null) {
			if (other.bF12 != null)
				return false;
		} else if (!bF12.equals(other.bF12))
			return false;
		if (bF13 == null) {
			if (other.bF13 != null)
				return false;
		} else if (!bF13.equals(other.bF13))
			return false;
		if (bF2 == null) {
			if (other.bF2 != null)
				return false;
		} else if (!bF2.equals(other.bF2))
			return false;
		if (bF3 == null) {
			if (other.bF3 != null)
				return false;
		} else if (!bF3.equals(other.bF3))
			return false;
		if (bF4 == null) {
			if (other.bF4 != null)
				return false;
		} else if (!bF4.equals(other.bF4))
			return false;
		if (bF5 == null) {
			if (other.bF5 != null)
				return false;
		} else if (!bF5.equals(other.bF5))
			return false;
		if (bF6 == null) {
			if (other.bF6 != null)
				return false;
		} else if (!bF6.equals(other.bF6))
			return false;
		if (bF7 == null) {
			if (other.bF7 != null)
				return false;
		} else if (!bF7.equals(other.bF7))
			return false;
		if (bF8 == null) {
			if (other.bF8 != null)
				return false;
		} else if (!bF8.equals(other.bF8))
			return false;
		if (bF9 == null) {
			if (other.bF9 != null)
				return false;
		} else if (!bF9.equals(other.bF9))
			return false;
		if (bFSRC1 == null) {
			if (other.bFSRC1 != null)
				return false;
		} else if (!bFSRC1.equals(other.bFSRC1))
			return false;
		if (bFSRC10 == null) {
			if (other.bFSRC10 != null)
				return false;
		} else if (!bFSRC10.equals(other.bFSRC10))
			return false;
		if (bFSRC11 == null) {
			if (other.bFSRC11 != null)
				return false;
		} else if (!bFSRC11.equals(other.bFSRC11))
			return false;
		if (bFSRC12 == null) {
			if (other.bFSRC12 != null)
				return false;
		} else if (!bFSRC12.equals(other.bFSRC12))
			return false;
		if (bFSRC13 == null) {
			if (other.bFSRC13 != null)
				return false;
		} else if (!bFSRC13.equals(other.bFSRC13))
			return false;
		if (bFSRC2 == null) {
			if (other.bFSRC2 != null)
				return false;
		} else if (!bFSRC2.equals(other.bFSRC2))
			return false;
		if (bFSRC3 == null) {
			if (other.bFSRC3 != null)
				return false;
		} else if (!bFSRC3.equals(other.bFSRC3))
			return false;
		if (bFSRC4 == null) {
			if (other.bFSRC4 != null)
				return false;
		} else if (!bFSRC4.equals(other.bFSRC4))
			return false;
		if (bFSRC5 == null) {
			if (other.bFSRC5 != null)
				return false;
		} else if (!bFSRC5.equals(other.bFSRC5))
			return false;
		if (bFSRC6 == null) {
			if (other.bFSRC6 != null)
				return false;
		} else if (!bFSRC6.equals(other.bFSRC6))
			return false;
		if (bFSRC7 == null) {
			if (other.bFSRC7 != null)
				return false;
		} else if (!bFSRC7.equals(other.bFSRC7))
			return false;
		if (bFSRC8 == null) {
			if (other.bFSRC8 != null)
				return false;
		} else if (!bFSRC8.equals(other.bFSRC8))
			return false;
		if (bFSRC9 == null) {
			if (other.bFSRC9 != null)
				return false;
		} else if (!bFSRC9.equals(other.bFSRC9))
			return false;
		if (bUDGET == null) {
			if (other.bUDGET != null)
				return false;
		} else if (!bUDGET.equals(other.bUDGET))
			return false;
		if (branchID == null) {
			if (other.branchID != null)
				return false;
		} else if (!branchID.equals(other.branchID))
			return false;
		if (cBAL == null) {
			if (other.cBAL != null)
				return false;
		} else if (!cBAL.equals(other.cBAL))
			return false;
		if (cUR == null) {
			if (other.cUR != null)
				return false;
		} else if (!cUR.equals(other.cUR))
			return false;
		if (dCODE == null) {
			if (other.dCODE != null)
				return false;
		} else if (!dCODE.equals(other.dCODE))
			return false;
		if (hOBAL == null) {
			if (other.hOBAL != null)
				return false;
		} else if (!hOBAL.equals(other.hOBAL))
			return false;
		if (lYMSRC1 == null) {
			if (other.lYMSRC1 != null)
				return false;
		} else if (!lYMSRC1.equals(other.lYMSRC1))
			return false;
		if (lYMSRC10 == null) {
			if (other.lYMSRC10 != null)
				return false;
		} else if (!lYMSRC10.equals(other.lYMSRC10))
			return false;
		if (lYMSRC11 == null) {
			if (other.lYMSRC11 != null)
				return false;
		} else if (!lYMSRC11.equals(other.lYMSRC11))
			return false;
		if (lYMSRC12 == null) {
			if (other.lYMSRC12 != null)
				return false;
		} else if (!lYMSRC12.equals(other.lYMSRC12))
			return false;
		if (lYMSRC13 == null) {
			if (other.lYMSRC13 != null)
				return false;
		} else if (!lYMSRC13.equals(other.lYMSRC13))
			return false;
		if (lYMSRC2 == null) {
			if (other.lYMSRC2 != null)
				return false;
		} else if (!lYMSRC2.equals(other.lYMSRC2))
			return false;
		if (lYMSRC3 == null) {
			if (other.lYMSRC3 != null)
				return false;
		} else if (!lYMSRC3.equals(other.lYMSRC3))
			return false;
		if (lYMSRC4 == null) {
			if (other.lYMSRC4 != null)
				return false;
		} else if (!lYMSRC4.equals(other.lYMSRC4))
			return false;
		if (lYMSRC5 == null) {
			if (other.lYMSRC5 != null)
				return false;
		} else if (!lYMSRC5.equals(other.lYMSRC5))
			return false;
		if (lYMSRC6 == null) {
			if (other.lYMSRC6 != null)
				return false;
		} else if (!lYMSRC6.equals(other.lYMSRC6))
			return false;
		if (lYMSRC7 == null) {
			if (other.lYMSRC7 != null)
				return false;
		} else if (!lYMSRC7.equals(other.lYMSRC7))
			return false;
		if (lYMSRC8 == null) {
			if (other.lYMSRC8 != null)
				return false;
		} else if (!lYMSRC8.equals(other.lYMSRC8))
			return false;
		if (lYMSRC9 == null) {
			if (other.lYMSRC9 != null)
				return false;
		} else if (!lYMSRC9.equals(other.lYMSRC9))
			return false;
		if (m1 == null) {
			if (other.m1 != null)
				return false;
		} else if (!m1.equals(other.m1))
			return false;
		if (m10 == null) {
			if (other.m10 != null)
				return false;
		} else if (!m10.equals(other.m10))
			return false;
		if (m11 == null) {
			if (other.m11 != null)
				return false;
		} else if (!m11.equals(other.m11))
			return false;
		if (m12 == null) {
			if (other.m12 != null)
				return false;
		} else if (!m12.equals(other.m12))
			return false;
		if (m13 == null) {
			if (other.m13 != null)
				return false;
		} else if (!m13.equals(other.m13))
			return false;
		if (m2 == null) {
			if (other.m2 != null)
				return false;
		} else if (!m2.equals(other.m2))
			return false;
		if (m3 == null) {
			if (other.m3 != null)
				return false;
		} else if (!m3.equals(other.m3))
			return false;
		if (m4 == null) {
			if (other.m4 != null)
				return false;
		} else if (!m4.equals(other.m4))
			return false;
		if (m5 == null) {
			if (other.m5 != null)
				return false;
		} else if (!m5.equals(other.m5))
			return false;
		if (m6 == null) {
			if (other.m6 != null)
				return false;
		} else if (!m6.equals(other.m6))
			return false;
		if (m7 == null) {
			if (other.m7 != null)
				return false;
		} else if (!m7.equals(other.m7))
			return false;
		if (m8 == null) {
			if (other.m8 != null)
				return false;
		} else if (!m8.equals(other.m8))
			return false;
		if (m9 == null) {
			if (other.m9 != null)
				return false;
		} else if (!m9.equals(other.m9))
			return false;
		if (mREV1 == null) {
			if (other.mREV1 != null)
				return false;
		} else if (!mREV1.equals(other.mREV1))
			return false;
		if (mREV10 == null) {
			if (other.mREV10 != null)
				return false;
		} else if (!mREV10.equals(other.mREV10))
			return false;
		if (mREV11 == null) {
			if (other.mREV11 != null)
				return false;
		} else if (!mREV11.equals(other.mREV11))
			return false;
		if (mREV12 == null) {
			if (other.mREV12 != null)
				return false;
		} else if (!mREV12.equals(other.mREV12))
			return false;
		if (mREV13 == null) {
			if (other.mREV13 != null)
				return false;
		} else if (!mREV13.equals(other.mREV13))
			return false;
		if (mREV2 == null) {
			if (other.mREV2 != null)
				return false;
		} else if (!mREV2.equals(other.mREV2))
			return false;
		if (mREV3 == null) {
			if (other.mREV3 != null)
				return false;
		} else if (!mREV3.equals(other.mREV3))
			return false;
		if (mREV4 == null) {
			if (other.mREV4 != null)
				return false;
		} else if (!mREV4.equals(other.mREV4))
			return false;
		if (mREV5 == null) {
			if (other.mREV5 != null)
				return false;
		} else if (!mREV5.equals(other.mREV5))
			return false;
		if (mREV6 == null) {
			if (other.mREV6 != null)
				return false;
		} else if (!mREV6.equals(other.mREV6))
			return false;
		if (mREV7 == null) {
			if (other.mREV7 != null)
				return false;
		} else if (!mREV7.equals(other.mREV7))
			return false;
		if (mREV8 == null) {
			if (other.mREV8 != null)
				return false;
		} else if (!mREV8.equals(other.mREV8))
			return false;
		if (mREV9 == null) {
			if (other.mREV9 != null)
				return false;
		} else if (!mREV9.equals(other.mREV9))
			return false;
		if (mSRC1 == null) {
			if (other.mSRC1 != null)
				return false;
		} else if (!mSRC1.equals(other.mSRC1))
			return false;
		if (mSRC10 == null) {
			if (other.mSRC10 != null)
				return false;
		} else if (!mSRC10.equals(other.mSRC10))
			return false;
		if (mSRC11 == null) {
			if (other.mSRC11 != null)
				return false;
		} else if (!mSRC11.equals(other.mSRC11))
			return false;
		if (mSRC12 == null) {
			if (other.mSRC12 != null)
				return false;
		} else if (!mSRC12.equals(other.mSRC12))
			return false;
		if (mSRC13 == null) {
			if (other.mSRC13 != null)
				return false;
		} else if (!mSRC13.equals(other.mSRC13))
			return false;
		if (mSRC2 == null) {
			if (other.mSRC2 != null)
				return false;
		} else if (!mSRC2.equals(other.mSRC2))
			return false;
		if (mSRC3 == null) {
			if (other.mSRC3 != null)
				return false;
		} else if (!mSRC3.equals(other.mSRC3))
			return false;
		if (mSRC4 == null) {
			if (other.mSRC4 != null)
				return false;
		} else if (!mSRC4.equals(other.mSRC4))
			return false;
		if (mSRC5 == null) {
			if (other.mSRC5 != null)
				return false;
		} else if (!mSRC5.equals(other.mSRC5))
			return false;
		if (mSRC6 == null) {
			if (other.mSRC6 != null)
				return false;
		} else if (!mSRC6.equals(other.mSRC6))
			return false;
		if (mSRC7 == null) {
			if (other.mSRC7 != null)
				return false;
		} else if (!mSRC7.equals(other.mSRC7))
			return false;
		if (mSRC8 == null) {
			if (other.mSRC8 != null)
				return false;
		} else if (!mSRC8.equals(other.mSRC8))
			return false;
		if (mSRC9 == null) {
			if (other.mSRC9 != null)
				return false;
		} else if (!mSRC9.equals(other.mSRC9))
			return false;
		if (oBAL == null) {
			if (other.oBAL != null)
				return false;
		} else if (!oBAL.equals(other.oBAL))
			return false;
		if (sCCRBAL == null) {
			if (other.sCCRBAL != null)
				return false;
		} else if (!sCCRBAL.equals(other.sCCRBAL))
			return false;
		return true;
	}

}
