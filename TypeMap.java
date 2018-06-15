import java.util.*;
import java.util.Map.Entry;

public class TypeMap extends HashMap<Variable, Type> { 

// TypeMap is implemented as a Java HashMap.  
// Plus a 'display' method to facilitate experimentation.

	void display() {
		Set<Entry<Variable, Type>> s =this.entrySet();
		Iterator<Entry<Variable, Type>> it = s.iterator();
		int size;
		if((size=s.size()) == 0) {
			System.out.println("There are no Declarations");
			return;
		}
		while(it.hasNext()) {
			Entry e = it.next();
			System.out.println("<"+e.getKey() + "," + e.getValue() + ">");
		}
	}
}
