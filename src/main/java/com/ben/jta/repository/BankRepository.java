package com.ben.jta.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 * Session Bean implementation class Repository
 */
@Singleton
@LocalBean
public class BankRepository {
	
	private Map<BigInteger, BigDecimal>
		accountBalance;
	
	@PostConstruct
	public void initialize() {
		accountBalance = new ConcurrentHashMap<>();
	}


	public void deposit() {
		
	}
	
	
	public void withdrawal() {
		
	}
}
