package com.ben.jta.utils;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class JtaStatus {
	
	private final static Map<Integer, String> JTA_STATUS_MAP = ImmutableMap.<Integer, String>builder()
			.put(0,"STATUS_ACTIVE")
			.put(3,"STATUS_COMMITTED")
			.put(8,"STATUS_COMMITTING")
			.put(1,"STATUS_MARKED_ROLLBACK")
			.put(6,"STATUS_NO_TRANSACTION")
			.put(2,"STATUS_PREPARED")
			.put(7,"STATUS_PREPARING")
			.put(4,"STATUS_ROLLEDBACK")
			.put(9,"STATUS_ROLLING_BACK")
			.put(5,"STATUS_UNKNOWN")
			.build();
	
	public static String getStatus(Integer status) {
		return JTA_STATUS_MAP.get(status);
	}

			
			
		


}
