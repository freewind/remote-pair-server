remote-pair-server
==================

The standalone server for the remote pair plugin

Required Java Version
=====================

Use jdk1.8 now.

Install
===========

1. `git clone https://github.com/freewind/remote-pair-server.git`
2. `cd remote-pair-server`
3. `./sbt publishLocal` or `./sbt -DversionCheck=false publishLocal` to publish it to local cache
4. `./sbt assembly` or `./sbt -DversionCheck=false assembly` will generate a jar, if throw exception, see "Issues"
5. `java -jar the_server.jar`


Issues
===========

1. add `127.0.0.1 localhost $the_local_name` to `/etc/hosts`, the `$the_local_name` is the name which thrown in previous exception.
