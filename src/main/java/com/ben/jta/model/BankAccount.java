package com.ben.jta.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class BankAccount {

	private BigInteger accountNumber;
	private BigDecimal accountBalance; // How much cash you have
	private String firstName;
	private String lastName;
	private String ssn;
	private List<BankTransaction> bankTranscations;
	
}
