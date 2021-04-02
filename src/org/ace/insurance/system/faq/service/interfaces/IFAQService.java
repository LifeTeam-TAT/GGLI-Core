package org.ace.insurance.system.faq.service.interfaces;

import java.util.List;

import org.ace.insurance.system.faq.FAQ;
import org.ace.insurance.system.productinformation.Language;
import org.ace.ws.model.system.FAQ001;

public interface IFAQService {
	public void addNewFAQ(FAQ faq);

	public void updateFAQ(FAQ faq);

	public void deleteFAQ(FAQ faq);

	public FAQ findFAQById(String id);

	public List<FAQ> findAllFAQ();

	public List<FAQ> findFAQByCodeNo(String codeNo);

	public List<FAQ> findFAQByLanguage(Language language);

	public void addNewFAQGroup(List<FAQ> faqList);

	public List<FAQ001> findAll_FAQ001();

	public String generateFAQCode();

}
