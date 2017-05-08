/*
 * Transaction is a simple class that stores information on each transaction. It is immutable. An immutable object’s 
 * values never change, therefore its values are not subject to corruption in a concurrent environment.
 */

/**
 *
 * @author Shivangi
 */
public class Transaction { //doe sit have to take in a file input?
    public int toAccount;
    public int fromAccount;
    public int amount;

    public Transaction(int from,int to, int amount){
    this.fromAccount = from;
    this.toAccount = to;
    this.amount = amount;
    }
    
    public int getToAccount(){
        return toAccount;
    }
    
    public int getFromAccount(){
        return fromAccount;
    }
    
        public int getAmount(){
        return amount;
    }
}
