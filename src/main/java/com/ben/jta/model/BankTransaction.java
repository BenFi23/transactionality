package com.ben.jta.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@Entity
@XmlRootElement
public class BankTransaction {
 
	@Id
	private BigInteger transactionID;
	private BigDecimal amount;
	private BeanType beanType;
	private boolean runtimeException;
}
 