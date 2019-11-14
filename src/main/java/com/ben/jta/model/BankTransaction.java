package com.ben.jta.model;

import java.math.BigInteger;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BankTransaction {

	private BigInteger transactionID;
	private List<String> transactionDetails;
}
