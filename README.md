
# jark-server

clojure.tools.jark

Jark is a tool to run a persistent JVM daemon and provide a set of utilities to interact with it.

# Core Features

* Interactive 
* Lightweight and can be used by Editors/IDEs.
* Cross-platform and OS agnostic
* Server-side plugins
* Secure
* Embeddable Server
* Easy-to-deploy and build
* Interoperable with existing clojure tools (lein, cljr etc)


# Installing the client 

Download the appropriate client binary for your platform from "downloads page":/jark/downloads.html
Untar and copy the jark-0.4-`platform` script to PATH/jark.
Currently, there are 32-bit and 64-bit binaries for MacOSX, GNU/Linux and Windows.

# Installing the Server

    jark server install [--standalone=<true|false> (default:true)

# Basic Usage

    jark server start

## Plugins

    jark [-h HOST -p PORT] cp add <CLASSPATH>
    jark [-h HOST -p PORT] cp list
    jark [-h HOST -p PORT] vm stat
    jark [-h HOST -p PORT] ns find <PATTERN>
    jark [-h HOST -p PORT] ns load <FILE>
    and more ...
    jark <NAMESPACE> <FUNCTION> <ARGS>
    and more ...

Default HOST is localhost and default port is 9000
    
## LICENSE

Copyright © 2012 Martin Demello and Isaac Praveen

Licensed under the EPL. (See the file epl.html.)
