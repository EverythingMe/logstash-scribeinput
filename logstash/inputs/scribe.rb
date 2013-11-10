require 'java'
require "logstash/namespace"
require "logstash/event"
require "logstash/inputs/base"
java_import 'scribe.thrift.ResultCode'
java_import 'scribe.thrift.Scribe'
java_import 'scribe.thrift.ActionScribeHandler'
java_import 'scribe.thrift.LogEntry'
java_import 'scribe.thrift.StdoutScribeHandler'
java_import 'org.apache.thrift.protocol.TBinaryProtocol'
java_import 'org.apache.thrift.server.TServer'
java_import 'org.apache.thrift.server.TThreadPoolServer'
java_import 'org.apache.thrift.transport.TFramedTransport'
java_import 'org.apache.thrift.transport.TServerSocket'
java_import 'org.apache.thrift.transport.TServerTransport'
java_import 'org.apache.thrift.transport.TTransportException'

class LogStash::Inputs::Scribe < LogStash::Inputs::Base
  class ScribeHandler < ActionScribeHandler
    # due to jruby bug, classes that subclass java classes cannot have constructor with different number of arguments
    # therefor we use a setter instead of simply assigning in a constructor
    attr_accessor :output_queue

    def action(messages)
        messages.each do |message|
            event = LogStash::Event.new({"message" => message.getMessage(), "category" => message.getCategory()})
            # this will block if output_queue is full. output_queue size is 20
            @output_queue << event
        end
    end

    #def action(message)
    #    event = LogStash::Event.new({"message" => message.getMessage(), "category" => message.getCategory()})
    #    @output_queue << event
    #end

    # Example for implementing "Log" directly
    # For some reason - the client does not accept the ResultCode.OK I return here
    # which is why I'm implementing "ActionScribeHandler" instead of implementing Log
    # myself. If anyone knows why this is the case - I'll be happy to know.
    # ---------------
    #java_signature 'ResultCode Log(List<LogEntry> messages)'
    #def Log(messages)
    #  messages.each do |message|
    #    event = LogStash::Event.new({"message" => message.getMessage(), "category" => message.getCategory()})
    #    @output_queue << event
    #  end
    #  return ResultCode.OK
    #end
  end

  config_name "scribe"
  milestone 1

  config :host, :validate => :string, :default => "0.0.0.0"
  config :port, :validate => :number, :required => true
  config :max_threads, :validate => :number, :default => 8
  config :min_threads, :validate => :number, :default => 1

  def initialize(*args)
    super(*args)
  end

  private
  def getServer(host, port, handler)
    bindAddr = java.net.InetSocketAddress.new(host, port)
    serverArgs = TThreadPoolServer::Args.new(TServerSocket.new(bindAddr))
    serverArgs.processor(Scribe::Processor.new(handler))
    serverArgs.minWorkerThreads([@min_threads, @max_threads].min)
    serverArgs.maxWorkerThreads(@max_threads)
    serverArgs.transportFactory(TFramedTransport::Factory.new())
    serverArgs.protocolFactory(TBinaryProtocol::Factory.new(false, false))
    return TThreadPoolServer.new(serverArgs)
  end

  public
  def register
    @logger.info("Starting scribe input listener", :address => "#{@host}:#{@port}")
  end

  def run(output_queue)
    @logger.info("Running scribe server")
    # due to jruby bug, classes that subclass java classes cannot have constructor with different number of arguments
    handler = ScribeHandler.new
    handler.output_queue = output_queue
    @tServer = getServer(@host, @port, handler)
    @tServer.serve()
  end

  def teardown
    @interrupted = true
    @tServer.stop()
  end # def teardown
end 
