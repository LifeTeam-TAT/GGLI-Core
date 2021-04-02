package org.ace.insurance.aspects.versionref.customer;

import org.ace.insurance.aspects.versionref.VersionReferenceUpdateAspect;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.industry.Industry;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.religion.Religion;
import org.ace.insurance.system.common.township.Township;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
//@Service("CustomerServiceAspect")
public class CustomerServiceAspect extends VersionReferenceUpdateAspect {
	
	@Around("execution(public * org.ace.insurance.system.common.branch.service.interfaces.IBranchService.*" +
						"(org.ace.insurance.system.common.branch.Branch)) " +
			"&& " +
			"args(branch)")	
	public void branchServiceAspect(ProceedingJoinPoint joinPoint, Branch branch) throws Throwable {		
		processVerIncrementAround(joinPoint, branch);
		/*
		String entityName = branch.getClass().getSimpleName();	
		String targetMetName = joinPoint.getSignature().toShortString();
		UpdateOperationType op = checkUpdateType(joinPoint.getSignature().getName(), entityName);
		
		logger.info( "Executing.." + targetMetName );
		try {
			joinPoint.proceed();
			increaseVersion(branch.getId(), entityName, op);
		} catch ( Throwable t ) {
			logger.error( t.getMessage() + ": " + targetMetName);
			throw t;
		}
		logger.info("Successfully executed " + targetMetName);
		*/	
	}
		
	@Around("execution(public * org.ace.insurance.system.common.bankBranch.service.interfaces.IBankBranchService.*" +
						"(org.ace.insurance.system.common.bankBranch.BankBranch)) " +
			"&& " +
			"args(bankBranch)")
	public void bankBranchServiceAspect(ProceedingJoinPoint joinPoint, BankBranch bankBranch) throws Throwable {
		processVerIncrementAround(joinPoint, bankBranch);
	}
	
	@Around("execution(public * org.ace.insurance.system.common.qualification.service.interfaces.IQualificationService.*" +
						"(org.ace.insurance.system.common.qualification.Qualification)) " +
			"&& " +
			"args(qualification)")
	public void qualificationServiceAspect(ProceedingJoinPoint joinPoint, Qualification qualification) throws Throwable {		
		processVerIncrementAround(joinPoint, qualification);
	}
	
	@Around("execution(public * org.ace.insurance.system.common.religion.service.interfaces.IReligionService.*" +
						"(org.ace.insurance.system.common.religion.Religion)) " +
			"&& " +
			"args(religion)")
	public void religionServiceAspect(ProceedingJoinPoint joinPoint, Religion religion) throws Throwable {
		processVerIncrementAround(joinPoint, religion);
	}
	
	@Around("execution(public * org.ace.insurance.system.common.occupation.service.interfaces.IOccupationService.*" +
						"(org.ace.insurance.system.common.occupation.Occupation)) " +
			"&& " +
			"args(occupation)")
	public void occupationServiceAspect(ProceedingJoinPoint joinPoint, Occupation occupation) throws Throwable {
		processVerIncrementAround(joinPoint, occupation);
	}
	
	@Around("execution(public * org.ace.insurance.system.common.industry.service.interfaces.IIndustryService.*" +
						"(org.ace.insurance.system.common.industry.Industry)) " +
			"&& " +
			"args(industry)")
	public void industryServiceAspect(ProceedingJoinPoint joinPoint, Industry industry) throws Throwable {
		processVerIncrementAround(joinPoint, industry);
	}
	
	@Around("execution(public * org.ace.insurance.system.common.country.service.interfaces.ICountryService.*" +
						"(org.ace.insurance.system.common.country.Country)) " +
			"&& " +
			"args(country)")
	public void countryServiceAspect(ProceedingJoinPoint joinPoint, Country country) throws Throwable {
		processVerIncrementAround(joinPoint, country);
	}
	
	@Around("execution(public * org.ace.insurance.system.common.township.service.interfaces.ITownshipService.*" +
						"(org.ace.insurance.system.common.township.Township)) " +
			"&& " +
			"args(township)")
	public void townshipServiceAspect(ProceedingJoinPoint joinPoint, Township township) throws Throwable {
		processVerIncrementAround(joinPoint, township);
	}
	
	@Around("execution(public * org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService.*" +
						"(org.ace.insurance.system.common.relationship.RelationShip)) " +
			"&& " +
			"args(relationship)")
	public void relationshipServiceAspect(ProceedingJoinPoint joinPoint, RelationShip relationship) throws Throwable {
		processVerIncrementAround(joinPoint, relationship);
	}
}
