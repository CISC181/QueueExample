package application.model;

import java.util.HashMap;
import java.util.Map;

public enum eBlockingMethod {
	ADD, OFFER, OFFER_TIME, PUT, REMOVE, POLL, POLL_TIME, TAKE;
	
	private static Map<String, eBlockingMethod> map = new HashMap<String, eBlockingMethod>();

	static {
		for (eBlockingMethod e : eBlockingMethod.values()) {
			map.put(e.toString(), e);
		}
	}	
    public static eBlockingMethod get(String method) {
        return map.get(method);
    }
}
