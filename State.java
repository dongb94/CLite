import java.util.*;
import java.util.Map.Entry;

public class State extends HashMap<Variable, Value> { 
    // Defines the set of variables and their associated values 
    // that are active during interpretation
    
    public State( ) { }
    
    public State(Variable key, Value val) {
        put(key, val);
    }
    
    public State onion(Variable key, Value val) {
        put(key, val);
        return this;
    }
    
    public State onion (State t) {
        for (Variable key : t.keySet( ))
            put(key, t.get(key));
        return this;
    }
    
    public void display() {
		Set<Entry<Variable, Value>> s = this.entrySet();
		Iterator<Entry<Variable, Value>> it = s.iterator();
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
