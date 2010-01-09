SBT Lift Skeleton
=================

Pre-configured for lift M8, jetty and org.specs.

Running
-------

Install Simple Build Tool first. Then run

 $ sbt update

 $ sbt jetty

And now you can access the application in http://localhost:8080

Tests
-----

There is currently only one test that checks all the xml
files in src/main/webapp directory for well-formedness.

 $ sbt test
