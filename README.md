logstash-scribeinput
====================

A logstash input plugin which receives scribe log entries via thrift

# Building

To create a bundle-jar with all the dependencies

```
mvn clean compile assembly:single
```


# Logstash configuration

```
input {
        scribe {
                host => "localhost"
                port => 8000
        }
}
```

# Invoking logstash
This is just a rough example showing how to treat the CLASSPATH and plugin-path of logstash
you shouldn't use this exact command, but rather learn the important key parts from it

```
java -Xmx400M -server \
   -cp scribe_server.jar:logstash-1.2.1-flatjar.jar \
   logstash.runner agent \
   -p /where/did/i/put/this/downloaded/plugin?  \
   -f logstash.conf \
   -vv 
```
