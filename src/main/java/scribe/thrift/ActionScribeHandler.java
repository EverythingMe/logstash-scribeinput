/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribe.thrift;

import com.facebook.fb303.fb_status;
import org.apache.thrift.TException;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class to extend in ruby. We will simply implement "action"
 * Of course we can always implement Scribe.Iface directly
 * but I find it easier to use as much native java as possible
 * @author smackware
 */
public class ActionScribeHandler implements Scribe.Iface {
    int inFlightMsgs;
    Logger logger = Logger.getLogger(this.getClass().toString());
    int MAX_IN_FLIGHT_MSGS;
    fb_status status;

    public ActionScribeHandler(int MAX_IN_FLIGHT_MSGS) {
        this.MAX_IN_FLIGHT_MSGS = MAX_IN_FLIGHT_MSGS;
        this.inFlightMsgs = 0;
        this.status = fb_status.ALIVE;
    }

    public ActionScribeHandler() {
        this(10000);
    }

    public void action(List<LogEntry> messages) {
        throw new UnsupportedOperationException();
    }
    
    public ResultCode Log(List<LogEntry> messages) throws TException {        
        if (messages.isEmpty()) {
            return ResultCode.OK;
        }

        // Throttling. we don't care about race conditions, it's quite all right to miss our target
        if (this.getStatus().equals(fb_status.STOPPING)) {
            logger.warning("Rejecting messages, server is stopping");
            return ResultCode.TRY_LATER;
        }

        if (inFlightMsgs > MAX_IN_FLIGHT_MSGS) {
            logger.log(Level.WARNING, "Throttling, too many messages in flight", inFlightMsgs);
            return ResultCode.TRY_LATER;
        }
        long before = System.currentTimeMillis();
        logger.info(before + " Processing: " + messages.size());
        this.inFlightMsgs += messages.size();
        try {
            this.action(messages);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Caught exception in messages handler", e);
            return ResultCode.TRY_LATER;
        } finally {
            this.inFlightMsgs -= messages.size();
        }
        long delta = System.currentTimeMillis() - before;
        logger.info(before + " Done processing: " + messages.size() + ": " + delta + "ms");
        return ResultCode.OK;
    }

    public String getName() throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getVersion() throws TException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public fb_status getStatus() throws TException {
        return status;
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

    @Override
    public void shutdown() throws TException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void drain() {
        logger.warning("Stopping handler");
        status = fb_status.STOPPING;
        for (int i=0; i < 60; i++) {
            if (this.inFlightMsgs == 0) {
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        logger.warning("Timed out waiting for all events to be sent");
    }
    
}
