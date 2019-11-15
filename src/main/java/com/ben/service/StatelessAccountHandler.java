package com.ben.service;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.transaction.TransactionSynchronizationRegistry;

import com.ben.jta.model.BankAccount;
import com.ben.jta.model.BankTransaction;
import com.ben.jta.utils.JtaStatus;

@Stateless
public class StatelessAccountHandler {
	
	@Resource(lookup = "java:comp/TransactionSynchronizationRegistry")
	TransactionSynchronizationRegistry transaction;
	
	public String getTransactionStatus() {
		return JtaStatus.getStatus(transaction.getTransactionStatus());
	}
	
	
	public BankTransaction getTransaction(int id) {
		
		return null;
	}
	
	public BankAccount getBankAccount(int id) {
		
		
		return null;
	}
	
	
}
