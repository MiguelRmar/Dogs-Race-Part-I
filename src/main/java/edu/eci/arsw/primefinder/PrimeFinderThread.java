package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrimeFinderThread extends Thread{

	
    int a,b;
    private Object lock;

    private List<Integer> primes;

    public PrimeFinderThread(int a, int b, Object lock) {
            super();
            this.a = a;
            this.b = b;
            this.primes = new LinkedList<Integer>();
            this.lock=lock;
    }

    public void run(){
        for (int i=a;i<=b;i++){						
            if (isPrime(i)){
                primes.add(i);
            }
            if(Driver.getTimer()){
                this.pause();
            }
        }	
    }
        
    private void pause() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(PrimeFinderThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
	
    boolean isPrime(int n) {
        if (n%2==0) return false;
        for(int i=3;i*i<=n;i+=2) {
            if(n%i==0)
                return false;
        }
        return true;
    }

    public List<Integer> getPrimes() {
            return primes;
    }	
}