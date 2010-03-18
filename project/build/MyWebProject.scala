import sbt._

class MyWebProject(info: ProjectInfo) extends DefaultWebProject(info)
{
  val liftVersion = "2.0-M3"
  val liftWebkit = "net.liftweb" % "lift-webkit" % liftVersion % "compile"
  val liftMapper = "net.liftweb" % "lift-mapper" % liftVersion % "compile"
  val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.22" % "test"
  val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  val derby = "org.apache.derby" % "derby" % "10.5.3.0_1" % "runtime"
  val specs = "org.scala-tools.testing" % "specs" % "1.6.1" % "test"

  // required because Ivy doesn't pull repositories from poms
  val smackRepo = "m2-repository-smack" at "http://maven.reucon.com/public"
}
