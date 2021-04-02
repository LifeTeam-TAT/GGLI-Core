package org.ace.insurance.accounting;

public enum GLSign {
	POSITIVE, NEGATIVE, ALL;

	public GLSign getReverse() {
		return this == POSITIVE ? NEGATIVE : POSITIVE;
	}
}
