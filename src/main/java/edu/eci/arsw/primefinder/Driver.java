/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.primefinder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public class Driver extends Thread{
    
    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 3000;
    private final static int TMILISECONDS = 5000;
    private Timer Reloj ;
    
    private final Object lock=new Object();
    public static boolean time;
    private final int NDATA = MAXVALUE / NTHREADS;

    private List<PrimeFinderThread> pft;
    
    private Driver() {
        super();
        this.pft = new  ArrayList<PrimeFinderThread>();

        int i;
        for(i = 0;i < NTHREADS  ; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i*NDATA, (i+1)*NDATA,lock);
            pft.add(elem);
        }
    }
    
    public static Driver newControl() {
        return new Driver();
    }

    @Override
    public void run() {
        time=true;
        Reloj = new Timer(TMILISECONDS, action);
        Reloj.start();
        for (int i = 0; i < NTHREADS; i++) {
            pft.get(i).start();
        }
        
    }

    public static boolean getTimer() {
        return time;
    }
    ActionListener action = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            time = false;
            Reloj.stop();
            List<Integer> respuesta = new LinkedList<Integer>();
            
            for (int i = 0; i < pft.size() ; i++) {
                try {
                    pft.get(i).join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for(int i = 0; i < pft.size() ; i++){
                respuesta.addAll(pft.get(i).getPrimes());
            }
            
            Collections.sort(respuesta);
            for(int n:respuesta){
                System.out.println(n);
            }
            System.out.println("Oprima la tecla Enter:");
            InputStreamReader instr = new InputStreamReader(System.in);
            BufferedReader bur = new BufferedReader(instr);
            String tecla;
            try {
                tecla = bur.readLine();
                if (tecla.equals("")) {
                    synchronized (lock) {
                        lock.notifyAll();
                        time = true;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
            }

            Reloj.start();
        }
    };

}