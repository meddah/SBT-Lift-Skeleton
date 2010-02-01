import sbt._

class MyWebProject(info: ProjectInfo) extends DefaultWebProject(info)
{
  val lift = "net.liftweb" % "lift-core" % "1.1-M8" % "compile->default"
  val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default"
  val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided->default"
  val derby = "org.apache.derby" % "derby" % "10.5.3.0_1" % "runtime->default"
  val specs = "org.scala-tools.testing" % "specs" % "1.6.1" % "test->default"

  // required because Ivy doesn't pull repositories from poms
  val smackRepo = "m2-repository-smack" at "http://maven.reucon.com/public"
}
