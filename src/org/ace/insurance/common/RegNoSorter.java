package org.ace.insurance.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Zaw Than Oo
 * @Use The <code>RegNoSorter</code> class is used to sort the entities which have custom registration number format.
 */ 
public class RegNoSorter<T extends ISorter> {
	private List<T> sortedList;
	public RegNoSorter(List<T> entitylist) {
		if(entitylist != null && !entitylist.isEmpty()) {
			sortedList = new ArrayList<T>();
			Collections.sort(entitylist, new Comparator<T>() {
				public int compare(T obj1, T obj2) {
					StringTokenizer st1 = new StringTokenizer(obj1.getRegistrationNo(), "/");
					st1.nextToken();st1.nextToken();
					String id1 = st1.nextToken();
					StringTokenizer st2 = new StringTokenizer(obj2.getRegistrationNo(), "/");
					st2.nextToken();st2.nextToken();
					String id2 = st2.nextToken();

					return id1.compareTo(id2);
				}
			});
			for(T t : entitylist) {
				sortedList.add(t);
			}
		}
	}
	
	public List<T> getSortedList() {
		return sortedList;
	}
	
	/*Example Usage*/
	public static void main(String[] args) {
		Person p1 = new Person("Zaw Than Oo", "MTR(USD)/1306/0000000003/HO");
		Person p2 = new Person("Pyae Phyo Aung", "MTR/1306/0000000010/HO");
		Person p3 = new Person("Ye Wint Aung", "MTR/1306/0000000055/HO");
		Person p4 = new Person("Myat Su Naing", "MTR/1306/0000000002/HO");
		List<Person> list = new ArrayList<Person>();
		list.add(p1);
		list.add(p2);
		list.add(p3);
		list.add(p4);
		RegNoSorter<Person> regNoSorter = new RegNoSorter<Person>(list);
		List<Person> sortedPersonList = regNoSorter.getSortedList();
		for(Person p : sortedPersonList) {
			System.out.println(p.getName() + " \t: " + p.getProposalNo());			
		}
	}
}