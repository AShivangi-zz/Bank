/*
 * The Bank class maintains a list of accounts and the BlockingQueue used to communicate between the main thread 
 * and the worker threads. The Bank is also responsible for starting up the worker threads, reading transactions 
 * from the file, and printing out all the account values when everything is done. 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Shivangi
 */
public class Bank {
    
    private static final int ACCOUNTS = 20;
    private static final int BALANCE = 1000;
    private int numOfThreads;
    private CountDownLatch latch;
    private final Map<Integer, Account> accountsMap = new HashMap<>();
    private final BlockingQueue<Transaction> transactionsQueue = new LinkedBlockingQueue<>();
    private final Transaction lastTransaction = new Transaction(-1, 0, 0);

    
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 2) {
            System.err.println("Wrong input format. Format: java Bank filepath numberofThreads");
        }
        else {
            Bank bank = new Bank();
            bank.launch(args[0], args[1]);
        }
    }
    
    public void launch(String fileName ,String numberOfThreads) throws FileNotFoundException {
        int n = Integer.parseInt(numberOfThreads);
        numOfThreads = (n > 0) ? n : 1;
        latch = new CountDownLatch(numOfThreads);
        for (int i = 0; i < numOfThreads; i++) {
            new Worker().start();
        }
        for (int i = 0; i < ACCOUNTS; i++) {
            accountsMap.put(i, new Account(i, BALANCE));
        }
	makeTransactionQueueFromFile(fileName);
        try { latch.await();
        } catch (InterruptedException e) { }
	System.out.println(this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
	for (Map.Entry<Integer, Account> entry : accountsMap.entrySet()) {
            sb.append(entry.getValue().toString());
            sb.append('\n');
        }
        return sb.toString();
    }
    
    private void makeTransactionQueueFromFile(String fileName) throws FileNotFoundException {
        try {
            File f = new File(fileName);
            FileInputStream fstream = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;
            while ((line = br.readLine()) != null) {
                String[] transaction = line.split("\\W+");
		int fromAccount = Integer.parseInt(transaction[0]);
		int toAccount = Integer.parseInt(transaction[1]);
		int amount = Integer.parseInt(transaction[2]);
		transactionsQueue.put(new Transaction(fromAccount, toAccount, amount));
            }
            br.close();
            fstream.close();
            for (int i = 0; i < numOfThreads; i++) {
                try {
                    transactionsQueue.put(lastTransaction);
                } catch (InterruptedException e) { }
            }
        } 
        catch (FileNotFoundException e) {
            System.err.println("File not found." );
        } 
        catch (InterruptedException | NumberFormatException | IOException e) {} 
    }
    
    private class Worker extends Thread {
        
        @Override
        public void run() {
            int transactionCounter = 0;
            while(true) {
                try {
                    Transaction t = transactionsQueue.take();
                    if (t == lastTransaction) {
                        break;
                    }
                    accountsMap.get(t.getFromAccount()).withdraw(t.getAmount());
                    accountsMap.get(t.getToAccount()).deposit(t.getAmount());
                    transactionCounter++;
                } catch (InterruptedException e) { }
            }
            
            System.out.println("Worker done after " + transactionCounter + " transactions.");
            latch.countDown();
        }
    }
}
