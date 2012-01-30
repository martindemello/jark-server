
# jark-server

clojure.tools.jark

Jark is a tool to run a persistent JVM daemon and provide a set of utilities to interact with it. 
It has 2 components - a client written in OCaml and a server written in Clojure/Java. The client is compiled to native code and is extremely tiny (~300K). 
The client uses the nREPL protocol to transfer clojure datastructures across the wire. 

## GOALS

* Interactive 
* Lightweight and can be used by Editors/IDEs.
* OS agnostic
* Server-side plugins
* Secure
* Embeddable Server
* Interoperable with existing clojure tools (lein, cljr etc)
* A nailgun replacement that is secure, faster and clojure-aware.

## INSTALLATION

### Client

Download the appropriate client binary for your platform from http://icylisper.in/jark/downloads.html 
Currently, there are 32-bit and 64-bit binaries for MacOSX, GNU/Linux and Windows. 

### Server

    jark [--standalone=<true|false> (default:true)] server install 

Currently, the standalone version is packaged with clojure-1.3. To install with clojure-1.2.x do:
           
    jark --standalone=false --clojure-version=1.2.x server install

## BASIC USAGE

    jark server start
    jark [-h HOST -p PORT] cp add <CLASSPATH>
    jark [-h HOST -p PORT] cp list
    jark [-h HOST -p PORT] vm stat
    jark [-h HOST -p PORT] ns find <PATTERN>
    jark [-h HOST -p PORT] ns load <FILE>
    jark [-h HOST -p PORT] repl
    and more ...
    jark <NAMESPACE> <FUNCTION> <ARGS>
    and more ...
    jark server stop

Default HOST is localhost and default port is 9000

# FEATURES 

* REPL commands `/vm stat`, `/server info`, '/debug on|off` ..type `/help` in jark repl
* Server-side plugin system. All plugins are written in Clojure
  
        jark plugin list
        jark plugin load <path-to-plugin.clj>

* Configurable (Edit $PREFIX/jark.conf)
* Remote JVM Performance monitoring (`jark vm stat`)
* Dynamically add classpath(s) (`jark cp add`)
* Remote Scripting (using the #! operator)
* Output JSON :
  All commands output JSON for parsing when passed a `--json` option
* Embeddable Server:
  Add [jark/jark-server "0.4-SNAPSHOT"] to project.clj and do a (clojure.tools.jark.server/start PORT) in your code. The jark-client can connect to it.
* Evaluate code from STDIN 
  
        echo CLOJURE-EXPRESSION | jark -s 
        jark -e CLOJURE-EXPRESSION        

* and more ..
    
## LICENSE

Copyright © 2012 Martin Demello and Isaac Praveen

Licensed under the EPL. (See the file epl.html.)
