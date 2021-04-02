package org.ace.insurance.accounting;

public enum CalculationType {
	// Calculate based of instructions in children
	CALCULATE,

	// Direct , no calculation
	DIRECT,

	// No more discount , now gross premium is already deducted
	// Discounted , substract discount from gross premium , P - D
	// DISCOUNTED,
	NONE;
}
