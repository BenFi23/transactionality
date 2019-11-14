package com.ben.controller;

import java.io.InputStream;
import java.net.URI;

import javax.inject.Inject;
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
import com.ben.service.AccountHandler;

/**
 *
 * @author airhacks.com
 */
@Path("/bank")
public class BankController {

	private AccountHandler accountHandler;
	
	@Inject
	public BankController(AccountHandler accountHandler) {
		this.accountHandler = accountHandler;
	}

    @GET
    public String ping() {
        return " Jakarta EE with MicroProfile 2+!";
    }
    
    @POST
    @Consumes("application/xml")
    public Response bankTransferRequest() {
    	
    	
    	String transactionID = "12345";
    	return Response.created(URI.create("/"+transactionID)).build();
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
