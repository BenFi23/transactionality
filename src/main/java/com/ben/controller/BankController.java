package com.ben.controller;

import java.io.InputStream;
import java.net.URI;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.ben.jta.model.BankAccount;
import com.ben.jta.model.BankTransaction;
import com.ben.jta.utils.JtaStatus;
import com.ben.service.StatelessAccountHandler;

/**
 * REST Principles
 * - Uniform constrained interface - Set of well defined methods (HTTP methods, GET,POST,PUT,DELETE) etc
 * - Stateless communication - Stateless apps are easier to scale. Each request isn't affected by the previous. Follow HATEOAS
 * - Representation oriented - Clients may (possibly) use different formats to interact with your service.
 * - Addressable resources - You can use a URI/URL to uniquely identify every resource. Resource being a key abstraction.
 * 
 * 
 *  Offers end-points to make deposits and withdrawals on a given bank account.
 * @author Ben Hunt
 */
@Path("bank")
public class BankController {
	
	Logger LOGGER = Logger.getLogger(BankController.class.getName());
	
	@Resource(lookup = "java:comp/TransactionSynchronizationRegistry")
	TransactionSynchronizationRegistry transaction;

	private StatelessAccountHandler accountHandler;
	
	@Inject
	public BankController(StatelessAccountHandler accountHandler) {
		this.accountHandler = accountHandler;
	}

    @GET
    public String ping() {
        return "Ping Bank " + JtaStatus.getStatus(transaction.getTransactionStatus()) +
        		" Statless Bean JTA Status: " + accountHandler.getTransactionStatus();
    }
    
    @POST @Path("deposit")
    @Consumes("application/xml")
    public Response deposit() {
    	
    	String transactionID = "12345";
    	return Response.created(URI.create("/"+transactionID)).build();
    }
    
    
    @GET @Path("withdrawal")
    @Produces("application/xml")
    public StreamingOutput withdrawal(@PathParam("id") int accountID) {
    	
    	BankTransaction transcation = accountHandler.getTransaction(accountID);
		return null;
    	
    }
    
    @GET
    @Produces("application/xml")
    public StreamingOutput getBankTransaction(@PathParam("id") int id) {
    	
    	BankTransaction transcation = accountHandler.getTransaction(id);
		return null;
    	
    }
    
    // Updates profile
    @PUT
    @Path("{id}")
    public void updateBankAccount(@PathParam("{id}") int accountID, InputStream body) throws JAXBException {
    	
    	BankAccount currentBankAccount = accountHandler.getBankAccount(accountID);
    	if(currentBankAccount == null)
    		throw new WebApplicationException(Response.Status.NOT_FOUND);
    	
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance(BankAccount.class);
    	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    	BankAccount updatedBankAccount = (BankAccount) unmarshaller.unmarshal(body);
    	
    	
    	
    }

}
