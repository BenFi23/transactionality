package com.ben.service;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionSynchronizationRegistry;

import com.ben.jta.model.BankTransaction;
import com.ben.jta.utils.JtaStatus;

@RequestScoped
public class CDIAccountHandler implements AccountHandler {
	
	@Resource(lookup = "java:comp/TransactionSynchronizationRegistry")
	TransactionSynchronizationRegistry transaction;
	
	@PersistenceContext(unitName="jtaDemoPU")
	EntityManager em;
	
	Logger LOGGER = Logger.getLogger(CDIAccountHandler.class.getName());

	public String getTransactionStatus() {
		return JtaStatus.getStatus(transaction.getTransactionStatus());
	}

	@Override
	public String deposit(BankTransaction bankTransaction) {
		
		LOGGER.info("Depositing :$ "+bankTransaction.getAmount()+ " Transaction Status: "+ getTransactionStatus());
		
		em.persist(bankTransaction);
		
		if(bankTransaction.isRuntimeException()) {
			int runtimeException = 1 / 0;
		}
		
		return "Sucessfully Persisted" + bankTransaction.toString();
	}

	@Override
	public String withdrawal(BankTransaction bankTransaction) {
		return null;
	}

}
