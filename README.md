remote-pair-server
==================

The standalone server for the remote pair plugin

Required Java Version
=====================

When you run `sbt` command, it will check if the java version is `jdk 1.6.x`, if not, it will throw an exception.

We have this requirement is because the generated jar may be used in IDEA plugin, `1.6.x` is safer.

But if you just want to employ it as a standalone server, or your IDEA is not working on `1.6.x`, you can use
`-DversionCheck=false` to disable the checking.

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
