package org.ace.insurance.aspects.versionref.customer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BranchServiceAOPTest {
	private static IBranchService branchService;
	private static ITownshipService townshipService;
	private static Logger logger = Logger.getLogger(BranchServiceAOPTest.class);
	private static IUserProcessService userProcessService;
	private static IUserService userService;
	
	//@BeforeClass
	public static void init() {
        logger.info("BranchServiceAOPTest is started.........................................");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
        BeanFactory factory = context;
        branchService = (IBranchService)factory.getBean("BranchService");
        townshipService = (ITownshipService) factory.getBean("TownshipService");
                
        
        logger.info("BranchServiceAOPTest instance has been loaded.");           
    }
	
	public static void main(String[] args) {
        //org.junit.runner.JUnitCore.main(BranchServiceAOPTest.class.getName());
		
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher("0");
		System.out.println(m.matches());
		
    }
	
	//@Test
	public void testTownshipUpdate() {
		Township township = townshipService.findTownshipById("ISSYS004HO000000000813052013");
		township.setDescription("SANCHAUNG TOWNSHIP");
		townshipService.updateTownship(township);
		logger.debug("Updated Township");
	}
	
	//@Test
	public void testBranch() {
		System.out.println("*************************");
		List<Branch> bankList = branchService.findAllBranch();
		System.out.println("The size of branch list...." + bankList.size());
		Branch branch = branchService.findBranchById("BANCH0000000000000012903201");
		if (branch == null)
			System.out.println("Null branch");
		else
			System.out.println(branch.getId());
		System.out.println("*************************");
	}
	
	//@Test
	public void testSOP() {
		System.out.println(Branch.class.getSimpleName());
//		Branch branch = new Branch();
//		branch.setId("aaa");
//		testObjectSOP(branch);
//		Customer customer = new Customer();
//		testObjectSOP(customer);
//		testProperties(CustomerRequirements.class);
	}
	
	private void testProperties(Class bean) {
		PropertyDescriptor[]  ret = PropertyUtils.getPropertyDescriptors(bean);
		System.out.println("PropertyDescriptor[]");
		for (PropertyDescriptor prop : ret) {
			System.out.println("prop.getPropertyType()" + prop.getPropertyType().toString());
			System.out.println("prop.getName()" + prop.getName());
		}
	}
	
	private void testObjectSOP(Object object) {
		System.out.println(object.getClass().getSimpleName());
		try {
			System.out.println(PropertyUtils.getProperty(object, "id"));
		} catch (IllegalAccessException e) {			
			e.printStackTrace();
		} catch (InvocationTargetException e) {			
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) {
//		ApplicationContext appContext = new ClassPathXmlApplicationContext("spring-beans.xml");
// 
//		IBranchService branchService = (IBranchService) appContext.getBean("BranchService");
	
//		ICustomerService custService = (ICustomerService) appContext.getBean("CustomerService");
		
 
//		System.out.println("*************************");
//		custService.findCustomerById("ISSYS001HO000000226116072013");        
//		branchService.findAllBranch();
//		Branch branch = branchService.findBranchById("ISSYS008HO000000001202102013");
//		branch.setName("updated");
		//updateBranch("ISSYS008HO000000001202102013");
		
//		System.out.println("*************************");
//		System.out.println("*************************");
//	}
	
//	public static void updateBranch(String id) {
//		Branch branch = branchService.findBranchById("ISSYS008HO000000001202102013");
//		branch.setName("updated");
//		branchService.updateBranch(branch);
//	}
}