# Build
mvn clean package && docker build -t com.ben/transactions .

# RUN
docker rm -f transactions || true && docker run -d -p 8080:8080 -p 4848:4848 --name transactions com.ben/transactions


### Endpoints to Demo Transcationality

Make deposits and withdrawals with the following endpoints to test out transactionality.


- https://localhost:8181/transactions/bank/transaction [Ping]
- https://localhost:8181/transactions/bank/transaction/deposit [Deposit into account]
- https://localhost:8181/transactions/bank/transaction/withdrawal [Withdrawal from account]
- https://localhost:8181/transactions/bank/transaction/{id} [Get info about a particular transaction]


### Beans Available
```
// EJB
@Stateless
class EJBAccountHandler{...}

// CDI
@RequestScoped
class CDIAccountHandler{

    @Transactional
    public void deposit(BigDecimal amount){...}
}

// Plain Java Class
class PlainAccountHandler{...}
```

There is 3 types of beans, or java classes, that a Java EE developer would typically interact with. The application uses all 3 types of java beans to demonstrate the transactionality of each.
- **EJB's** : Enterprise Java Beans (EJB 3.2, JSR 345).
- **CDI Beans** : Contexts and Dependency Injection Beans (CDI 2.0, JSR 365).
- **Java Beans** : Plain ole Java Objects, not managed by any IoC containers.

# Why and How?

Learn Java EE Transcations by making bank deposits and transfers. Bank deposits and transfers are
operations that need to happen all at once or not all all, e.g. atomically as a part of a **transaction**. 

Imagine we request to make a withdrawal from our bank for $1,000 and the server experiences a runtime exception (i.e. a NullPointerException) in the middle of the process of making the withdrawal. The server sucessfully deducts $1,000 from your account but the logic in the ATM fails to give you your cash. Without someway of rolling back these actions, you are out $1,000. 

Transcationality / Atomicity is obviously needed, but how do we want to achieve it?

### Container Managed Transactions

#### How the container is able to manage your transactions
Typically, with enterprise applications you develop within an Inversion of Control, IoC, framework where the framework takes care of a lot of the plumbing that you would otherwise explicity have to control such as concurrency management, security, transcationality, etc. (ew).

For the framework to manage your transactions and help safeguard your application from data inconsistencies, it needs some way to be aware of where your business logic is executing. This is where Enterprise Java Beans, (EJB's), come in. Using one of the EJB annotations such as ```@Stateless```, ```@Stateful```, you are telling the container,
 > I want to focus on writing my business logic, while you provide me extra functionality wrapped around my class to address my side-concerns, e.g. the things that don't pertain directly to business logic.

 In our case, we would want the container to provide us transcationality over our business methods. Note, the EJB container and most containers are not doing byte-code manipulation on your managed beans. Rather, proxies of your classes are being made by subclassing your beans. Thus, If we had transactionally configured to be on within our framework and we called the following business method, ```bankHandler.withDrawal(1000)```, we would not be directly hitting that business method first. Rather, we would hit the proxy of the object the container created and the proxy might perform any cross-cutting concerns before our logic gets executed like beginning a new transaction. The same applies after the withdrawal method returns.

#### How transcationality can be configured
Questions you might ask 
- Do all business methods have separate transcations? 
- Can the calle join the transaction of the caller?
- What if I don't need a transaction on a business method? Nothing is being persisted.

You can configure the transactionality of business methods by utilizing the Transaction Attribute annotation. Configured either at the class or method level. For ex: ```@TransactionAttribute(RequiresNew)```

| Transcation Attribute | Foo | Bar |
|------------------------|-----|-----|
| **Required (Default)** | **None**| **T2**  |
| **Requred (Default)** | **T1** | **T1** |
| RequiresNew | None | T2 |
| RequiresNew | T1 | T2 |

See https://javaee.github.io/tutorial/transactions004.html for a full table of attributes.

By default, EJB's always operate within a transaction with the implicit **Required** attribute. This transaction is apart of the caller's transaction if one exists otherwise a new transaction is created. 

```
@Stateless
class Foo {

	@Inject // Using field injection for simplicity
	Bar bar;

	// Assuming the client of Foo is not in a transaction, Transaction T1 is started
	fooMethod (){
		// bar will execute within Foo's transaction, T1.
		// If Foo was not executing within a transaction, say it was a plain java class, then 
		// bar would create execute under a new transaction.
		bar.barMethod(); 
	}
}

@Session
class Bar {

	barMethod(){
		// some business logic
	}
}
```
#### Rolling it back

There way two ways for a transaction to be rolled back:
- System/Runtime Exceptions - Developer error, implicit rollback 
- setRollback(...) of EJBContext interface. - Developer explicitly rollsback transaction

Be aware of the following scenarios:

1. Application Exceptions, the exceptions you define, do not automatically initiate a rollback.
This means if you throw and catch an exception in a part of your business logic, you need to explicity call ```ejbContext.setRollback(true);``` if you want the transcation the application error occured in to rollback. The framework will assume you can proceed gracefully with your processing unless told otherwise.

2. Runtime exceptions that occur in a non-managed class invoked by a class participating in a transaction will not rollback the transcation.


Exceptions thrown from EJB:
```
javax.ejb.EJBTransactionRolledbackException
javax.ejb.TransactionRolledbackLocalException
javax.transaction.TransactionRolledbackException
```

Clients recieving one of these exceptions know that the transcation is marked for rollback. You can't commit at this point, you should not proceed with processing.


### Application Managed Transactions

Application Managed Transactions, also known as Bean Mangaged Transactions, must explicity
demarcate transaction boundaries. Developers would have to obtain a reference to ```javax.transaction.UserTransaction``` through a resource injection/lookup. 
```
@Resource UserTransaction userTransaction;
public void updateData() {
// Start a transaction.
userTransaction.begin();
// ...
// Perform transactional operations on data
// Commit the transaction.
userTransaction.commit();
}
```

There is not a huge need to use Bean Managed Transactions for the everyday developer. One benefits of using Bean Managed Transactions is the granularity you could achieve. Bean Managed Transactions can have multiple transactions per method while Container Managed Transactions can only have 1 transaction ber business method.

### Resources:
See the Gang of Four https://www.geeksforgeeks.org/proxy-design-pattern/ for more information on the proxy pattern.

JTA 1.2 Specfication: http://download.oracle.com/otn-pub/jcp/jta-1_2-mrel2-eval-spec/JTA1.2Specification.pdf

CDI 2.0 JSR-365 Specification: https://docs.jboss.org/cdi/spec/2.0/cdi-spec.html#introduction

Java EE 8 Tutorial on Transactions: https://javaee.github.io/tutorial/transactions.html#BNCIH

EJB 3.2 JSR-345: https://download.oracle.com/otndocs/jcp/ejb-3_2-fr-spec/index.html