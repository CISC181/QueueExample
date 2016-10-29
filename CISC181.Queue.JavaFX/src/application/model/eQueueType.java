package application.model;

import java.util.HashMap;
import java.util.Map;

public enum eQueueType {
	PriorityBlockingQueue, ArrayBlockingQueue;
	
	private static Map<String, eQueueType> map = new HashMap<String, eQueueType>();

	static {
		for (eQueueType e : eQueueType.values()) {
			map.put(e.toString(), e);
		}
	}	
    public static eQueueType get(String strQueueType) {
        return map.get(strQueueType);
    }
}
