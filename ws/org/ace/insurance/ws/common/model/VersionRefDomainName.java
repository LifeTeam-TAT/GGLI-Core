package org.ace.insurance.ws.common.model;

import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.industry.Industry;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.religion.Religion;
import org.ace.insurance.system.common.township.Township;

/**
 * To define base domain name(entity name) and its relative entity class names.
 * e.g; Customer and its ORM entity class names 
 * @author rnd_admin
 *
 */
public class VersionRefDomainName {
	/**
	 * Customer domain class name and its ORM entity class names
	 * @author rnd_admin
	 *
	 */
	public static class Customer {
		/**
		 * Customer entity class's relationship class names.
		 * Note that the following defined names must be identical to relative entity class name 
		 * e.g Branch (org.ace.insurance.system.common.branch.Branch)
		 */
		public static final String BRANCH = Branch.class.getSimpleName();
		public static final String BANKBRANCH = BankBranch.class.getSimpleName();
		public static final String QUALIFICATION = Qualification.class.getSimpleName();
		public static final String RELIGION = Religion.class.getSimpleName();
		public static final String OCCUPATION = Occupation.class.getSimpleName();
		public static final String INDUSTRY = Industry.class.getSimpleName();
		public static final String COUNTRY = Country.class.getSimpleName();
		public static final String TOWNSHIP = Township.class.getSimpleName();
		public static final String RELATIONSHIP = RelationShip.class.getSimpleName();				
	}	
}
