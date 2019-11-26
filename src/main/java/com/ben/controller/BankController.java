package com.ben.controller;

import java.io.InputStream;
import java.net.URI;
import java.util.Formatter;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.naming.NamingException;
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
import com.ben.jta.model.BeanType;
import com.ben.jta.utils.JtaStatus;
import com.ben.service.AccountHandler;
import com.ben.service.CDIAccountHandler;
import com.ben.service.EJBAccountHandler;
import com.ben.service.PlainAccountHandler;
import com.ben.service.factory.AccountHandlerFactory;

/**
 * REST Principles
 * - Uniform constrained interface - Set of well defined methods (HTTP methods, GET,POST,PUT,DELETE) etc
 * 	- Familiarity -> (Just need HTTP client), Interoperability -> (HTTP is ubiquitous), Scalability ->
 * - Stateless communication - Stateless apps are easier to scale. Each request isn't affected by the previous. Follow HATEOAS
 * 	- Only state maintained on the server is the resources. Session data if any is stored client side. EZ Scale by adding more machines.
 * - Representation oriented - Clients may (possibly) use different formats to interact with your service.
 * 	- How you represent your resources, BankAccount can be represented in a variety of formats, XML for Java, JSON for ajax, etc..
 * - Addressable resources - You can use a URI/URL to uniquely identify every resource. Resource being a key abstraction.
 * - HATEOS - Provide hyperlinks in HTTP responses to drive state transitions to other services or resources
 * 
 *  Offers end-points to make deposits and withdrawals on a given bank account.
 *  
 *  Operations:
 *  	GET - Bank Account information. (Imdepotent)
 *  	GET - Ping the server, e.g. get the status of server.
 *  	POST - Withdrawal {$1} amount of $
 *  	POST - Transfer {$1} amount of $ to Account {$2}.
 *  	PUT - Update account {$1} information.
 *  
 * @author Ben Hunt
 */
@Path("bank")
public class BankController {
	
	Logger LOGGER = Logger.getLogger(BankController.class.getName());
	
	@Resource(lookup = "java:comp/TransactionSynchronizationRegistry")
	TransactionSynchronizationRegistry transactionRegistry;

	private EJBAccountHandler ejbAccountHandler;
	private CDIAccountHandler cdiAccountHandler;
	private PlainAccountHandler plainAccountHandler;
	private AccountHandlerFactory hanlderFactory;
	
	@Inject
	public BankController(EJBAccountHandler accountHandler, CDIAccountHandler cdiAccountHandler, AccountHandlerFactory hanlderFactory) throws NamingException {
		this.ejbAccountHandler = accountHandler;
		this.cdiAccountHandler = cdiAccountHandler;
		this.hanlderFactory = hanlderFactory;
		plainAccountHandler = new PlainAccountHandler();
	}

    @GET
    public String ping() throws NamingException {
    	Formatter formatter  = new Formatter();
        String response =
        		"Ping Bank - Success - Default Transaction Status' of the following beans: \n" +
        		formatter.format("%-12s %20s", "Bank Controller", JtaStatus.getStatus(transactionRegistry.getTransactionStatus())) + "\n" +
        		"Statless Bean JTA Status: " + ejbAccountHandler.getTransactionStatus() + "\n" +
        		"CDI Bean JTA Status: " + cdiAccountHandler.getTransactionStatus() + "\n" +
        		"Plain Bean JTA Status: " + plainAccountHandler.getTransactionStatus();
        formatter.close();
        return response;
    }
    
    @POST @Path("deposit")
    @Consumes("application/xml")
    public Response deposit(InputStream inputStream) throws JAXBException {
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance(BankTransaction.class);
    	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller(); // xml -> java
    	BankTransaction bankTransaction = (BankTransaction) unmarshaller.unmarshal(inputStream);
    	
    	LOGGER.info(String.format("Depositing $%s. Transaction Status: %s",
    			bankTransaction.getAmount(), JtaStatus.getStatus(transactionRegistry.getTransactionStatus())));
    	
    	AccountHandler accountHandler;
    	
    	switch(bankTransaction.getBeanType()) {
    	
	    	case CDI: accountHandler = cdiAccountHandler;
	    		break;
	    	case EJB: accountHandler = ejbAccountHandler;
	    		break;
	    	default: accountHandler = plainAccountHandler;
    	
    	}

    	accountHandler.deposit(bankTransaction);
//    	AccountHandler accountHandler = hanlderFactory.createAccountHandler(BeanType.CDI);
    	
    	return Response.created(URI.create("/TestURI")).build();
    }
    
    @GET @Path("withdrawal")
    @Produces("application/xml")
    public StreamingOutput withdrawal(@PathParam("id") int accountID) {
    	
    	BankTransaction transcation = ejbAccountHandler.getTransaction(accountID);
		return null;
    }
    
    // Updates profile
    @PUT
    @Path("{id}")
    public void updateBankAccount(@PathParam("{id}") int accountID, InputStream body) throws JAXBException {
    	
    	BankAccount currentBankAccount = ejbAccountHandler.getBankAccount(accountID);
    	if(currentBankAccount == null)
    		throw new WebApplicationException(Response.Status.NOT_FOUND);
    	
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance(BankAccount.class);
    	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    	BankAccount updatedBankAccount = (BankAccount) unmarshaller.unmarshal(body);
    	
    	
    	
    }

}
