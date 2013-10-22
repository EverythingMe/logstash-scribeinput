package scribe_server;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.thrift.transport.TTransportException;
import scribe.thrift.StdoutScribeHandler;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Setting up..." );
        StdoutScribeHandler handler = new StdoutScribeHandler();
        try {
            Util.serveWithHandler("localhost", 7777, handler);
        } catch (TTransportException ex) {
            System.out.println("ERROR!: " + ex.toString());
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
