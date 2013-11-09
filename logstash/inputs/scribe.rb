require 'java'
require "logstash/namespace"
require "logstash/event"
require "logstash/inputs/base"
include Java
java_import 'scribe.thrift.ResultCode'
java_import 'scribe.thrift.Scribe'
java_import 'scribe.thrift.ActionScribeHandler'
java_import 'scribe.thrift.LogEntry'
java_import 'scribe.thrift.StdoutScribeHandler'
java_import 'scribe_server.Util'

class LogStash::Inputs::Scribe < LogStash::Inputs::Base
  class ScribeHandler < ActionScribeHandler
    def setQueue(output_queue)
      @output_queue = output_queue
    end

    def action(messages)
        messages.each do |message|
            event = LogStash::Event.new({"message" => message.getMessage(), "category" => message.getCategory()})
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

  class Interrupted < StandardError; end

  config_name "scribe"
  milestone 1

  config :host, :validate => :string, :default => "0.0.0.0"
  config :port, :validate => :number, :required => true

  def initialize(*args)
    super(*args)
  end

  public
  def register
    @logger.info("Starting scribe input listener", :address => "#{@host}:#{@port}")
  end

  public
  def run(output_queue)
    handler = ScribeHandler.new()
    handler.setQueue(output_queue)
    @logger.info("Start of run for thrift plugin")
    @tServer = Util.getServer(@host, @port, handler)
    @tServer.serve()
  end

  public
  def teardown
    @interrupted = true
    @tServer.stop()
  end # def teardown
end 
