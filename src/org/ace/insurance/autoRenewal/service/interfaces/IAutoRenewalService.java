package org.ace.insurance.autoRenewal.service.interfaces;

import java.util.List;

import org.ace.insurance.autoRenewal.AutoRenewal;
import org.ace.insurance.autoRenewal.AutoRenewalCriteria;
import org.ace.insurance.autoRenewal.AutoRenewalStatus;

public interface IAutoRenewalService {
	public AutoRenewal addNewAutoRenewal(AutoRenewal autoRenewal);

	public void updateAutoRenewal(AutoRenewal autoRenewal);

	public void updateStatus(AutoRenewalStatus status, String id);

	public void deleteAutoRenewal(AutoRenewal autoRenewal);

	public AutoRenewal findAutoRenewalById(String id);

	public List<AutoRenewal> findAllAutoRenewal();

	public List<AutoRenewal> findAllActiveAutoRenewal();

	public List<AutoRenewal> findByAutoRenewalCriteria(AutoRenewalCriteria criteria);

	public void scheduler();

}
