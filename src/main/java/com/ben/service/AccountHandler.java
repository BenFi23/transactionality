package com.ben.service;

import com.ben.jta.model.BankTransaction;

public interface AccountHandler {
	
	public String getTransactionStatus(); // Does it belong in the interface?
	public String deposit(BankTransaction bankTransaction);
	public String withdrawal(BankTransaction bankTransaction);

}
