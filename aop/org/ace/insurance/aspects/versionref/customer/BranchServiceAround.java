package org.ace.insurance.aspects.versionref.customer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.ace.insurance.filter.bankCustomer.BANKCUSTOMER001;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.ws.common.model.VersionRefDomainName;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * Not used this spring AOP class in this project
 * If this class needs to be applied, needs more configuration in spring-beans config file
 */
//@Service(value = "BranchServiceAround")
public class BranchServiceAround implements MethodInterceptor {
	@Autowired
	private IBankService bankService;

	@Override
	public Object invoke(MethodInvocation inv) throws Throwable {
		System.out.println("Around method..............begin");
		System.out.println("Method name : " + inv.getMethod().getName());
		System.out.println("Method arguments : " + Arrays.toString(inv.getArguments()));

		List<BANKCUSTOMER001> banks = bankService.findAllBank();
		System.out.println("The size of bank list..." + banks.size());

		Object ret = inv.proceed();

		System.out.println("Around method..............end");
		System.out.println("Around method..............ret.getClass() : " + ret.getClass().getName());
		if (ret instanceof Vector) {
			Vector<Branch> retList = (Vector<Branch>) ret;
			for (Branch branch : retList) {
				System.out.println(branch.getName());
			}
		}
		return ret;
	}

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		System.out.println(VersionRefDomainName.Customer.class.getSimpleName());
		PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(VersionRefDomainName.Customer.class);
		for (PropertyDescriptor prop : props) {
			System.out.print(prop.getName() + "\t");
			System.out.print(prop.getPropertyType().getName() + "\t");
			System.out.println(prop.getPropertyType().isPrimitive());
		}
		Class clazz = VersionRefDomainName.Customer.class;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			System.out.println(field.get(field.getName()));
			System.out.println(field.getName());
		}
		Class clazz2 = VersionRefDomainName.Customer.class;
		Field[] fields2 = clazz2.getFields();
		for (Field field : fields2) {
			System.out.println(field.get(field.getName()));
			System.out.println(field.getName());
		}
	}
}
