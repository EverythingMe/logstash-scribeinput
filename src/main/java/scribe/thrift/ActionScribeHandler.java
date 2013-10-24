/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribe.thrift;

import com.facebook.fb303.fb_status;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.thrift.TException;

/**
 * A class to extend in ruby. We will simply implement "action"
 * Of course we can always implement Scribe.Iface directly
 * but I find it easier to use as much native java as possible
 * @author smackware
 */
public class ActionScribeHandler implements Scribe.Iface {
    /*private BlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<Runnable>(4);
    private final ExecutorService executorService = new ThreadPoolExecutor(1, 10, 30, TimeUnit.SECONDS, linkedBlockingQueue, new ThreadPoolExecutor.AbortPolicy());
    private Processor processor= null;*/
    
    
    public void action(List<LogEntry> messages) {
        throw new UnsupportedOperationException();
    }
            
    public class Processor implements Runnable {
        protected List<LogEntry> messages;
        private ActionScribeHandler handler;
        private boolean run = true;
        public Processor(List<LogEntry> messages, ActionScribeHandler handler) {
            this.messages = messages;       
            this.handler = handler;
        }
        public void run() {                  
            this.handler.action(this.messages);
        }        
    }
    
    public boolean canQueue() {
        throw new UnsupportedOperationException();
    }
    
    public ResultCode Log(List<LogEntry> messages) throws TException {        
        if (messages.isEmpty()) {
            return ResultCode.OK;
        }
        long before = System.currentTimeMillis();
        System.out.println(before + " Processing: " + messages.size());
        this.action(messages);
        long delta = System.currentTimeMillis() - before;
        System.out.println(before + " Done processing: " + messages.size() + ": " + delta +"ms");
        return ResultCode.OK;
        /*
        try {        
            
            Processor processor = new Processor(messages, this);
            try {
                this.executorService.execute(processor);            
                System.out.println("Processing: " + messages.size());
            } catch (RejectedExecutionException e) {
                System.out.println("Too many threads");
                return ResultCode.TRY_LATER;
            }
            return ResultCode.OK;               
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResultCode.TRY_LATER;
        }*/
    }

    public String getName() throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getVersion() throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public fb_status getStatus() throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getStatusDetails() throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Map<String, Long> getCounters() throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public long getCounter(String string) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setOption(String string, String string1) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getOption(String string) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Map<String, String> getOptions() throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getCpuProfile(int i) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public long aliveSince() throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void reinitialize() throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void shutdown() throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
