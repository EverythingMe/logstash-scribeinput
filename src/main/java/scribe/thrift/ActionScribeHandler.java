/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribe.thrift;

import com.facebook.fb303.fb_status;
import java.util.List;
import java.util.Map;
import org.apache.thrift.TException;

/**
 * A class to extend in ruby. We will simply implement "action"
 * Of course we can always implement Scribe.Iface directly
 * but I find it easier to use as much native java as possible
 * @author smackware
 */
public class ActionScribeHandler implements Scribe.Iface {

    public void action(LogEntry logEntry) {
        throw new UnsupportedOperationException();
    }
    
    public class ProcessThread implements Runnable {
        private List<LogEntry> messages;
        private ActionScribeHandler handler;
        public ProcessThread(List<LogEntry> messages, ActionScribeHandler handler) {
            this.messages = messages;       
            this.handler = handler;
        }
        public void run() {
            //System.out.println("Processing: " + messages.size() + " messages");
            for (LogEntry message : messages) {
                this.handler.action(message);
            }            
        }
        
    }
    
    public ResultCode Log(List<LogEntry> messages) throws TException {        
        //System.out.println("Got: " + messages.size() + " messages");
        ProcessThread processThread = new ProcessThread(messages, this);
        Thread thread = new Thread(processThread);
        thread.start();
        return ResultCode.OK;
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
