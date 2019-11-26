package com.ben.service;

import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionSynchronizationRegistry;

import com.ben.jta.model.BankTransaction;
import com.ben.jta.utils.JtaStatus;

public class PlainAccountHandler implements AccountHandler {
	
	TransactionSynchronizationRegistry transactionInfo;
	Logger LOGGER = Logger.getLogger(PlainAccountHandler.class.getName());
	
	public PlainAccountHandler() throws NamingException {
		Context c = new InitialContext();
		transactionInfo = TransactionSynchronizationRegistry.class.cast(c.lookup("java:comp/TransactionSynchronizationRegistry"));
	}

	
	public String getTransactionStatus()  {
		Context c;
		try {
			c = new InitialContext();
			transactionInfo = TransactionSynchronizationRegistry.class.cast(c.lookup("java:comp/TransactionSynchronizationRegistry"));
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JtaStatus.getStatus(transactionInfo.getTransactionStatus());
	}


	@Override
	public String deposit(BankTransaction bankTransaction) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String withdrawal(BankTransaction bankTransaction) {
		// TODO Auto-generated method stub
		return null;
	}
}
