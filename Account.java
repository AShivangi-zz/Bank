/*
 * Account stores an id number, the current balance for the account, and the number of transactions that have 
 * occurred on the account. Multiple worker threads may be access an account simultaneously, but they cannot corrupt 
 * its data. The toString method overridden to handle printing of account information.
 */

/**
 *
 * @author Shivangi
 */


public class Account {//are getters needed?
    private int id_number;
    private int currentBalance;
    private int transactions = 0;
    
    public Account(int id, int balance){
        this.id_number = id;
        this.currentBalance = balance;
    }
    
    public synchronized void withdraw(int amount) {
        currentBalance = currentBalance-amount;
        transactions++;
    }
    public synchronized void deposit(int amount) {
        currentBalance = currentBalance+amount;
        transactions++;
    }
   
    @Override
    public String toString() {
        return "acct:"+id_number+" bal:"+currentBalance+" trans:"+transactions;
	}
    
    @Override
    public int hashCode() {
        return id_number;
    }	
}
