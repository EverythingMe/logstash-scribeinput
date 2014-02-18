/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribe_server;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import scribe.thrift.Scribe;

import java.net.InetSocketAddress;

/**
 *
 * @author smackware
 */
public class Util {
    public static TServer getServer(String host, int port, Scribe.Iface handler) throws TTransportException {
        System.out.println("Setting up...");
        InetSocketAddress bindAddr = new InetSocketAddress(host, port);
        scribe.thrift.Scribe.Processor processor = new scribe.thrift.Scribe.Processor(handler);
        TServerTransport transport = new TServerSocket(bindAddr);
        TThreadPoolServer.Args serverArgs = new TThreadPoolServer.Args(transport);
        serverArgs.processor(processor);
        //serverArgs.minWorkerThreads(8);
        serverArgs.maxWorkerThreads(32);
        serverArgs.transportFactory(new TFramedTransport.Factory());
        serverArgs.protocolFactory(new TBinaryProtocol.Factory(false, false));
        return new TThreadPoolServer(serverArgs);
    }
}
