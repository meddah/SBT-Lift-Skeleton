package bootstrap.liftweb

import _root_.net.liftweb._
import common.{Full, Empty, Box}
import http.{LiftRules, S}
import sitemap.{SiteMap, Menu, Loc}
import Loc._
import util.Props
import mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, ConnectionIdentifier}
import _root_.java.sql.{Connection, DriverManager}

class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?)
      DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)

    LiftRules.addToPackages("web")

    LiftRules.useXhtmlMimeType = false

    val entries = Menu(Loc("Home", List("index"), "Main page")) :: Nil
    LiftRules.setSiteMap(SiteMap(entries: _*))

    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    S.addAround(DB.buildLoanWrapper)
  }
}

/**
* Database connection calculation
*/
object DBVendor extends ConnectionManager {
  private var pool: List[Connection] = Nil
  private var poolSize = 0
  private val maxPoolSize = 4

  private def createOne: Box[Connection] = try {
    val driverName: String = Props.get("db.driver") openOr
    "org.apache.derby.jdbc.EmbeddedDriver"

    val dbUrl: String = Props.get("db.url") openOr
    "jdbc:derby:lift_example;create=true"

    Class.forName(driverName)

    val dm = (Props.get("db.user"), Props.get("db.password")) match {
      case (Full(user), Full(pwd)) =>
	      DriverManager.getConnection(dbUrl, user, pwd)
      case _ => DriverManager.getConnection(dbUrl)
    }
    Full(dm)
  } catch {
    case e: Exception => e.printStackTrace; Empty
  }

  def newConnection(name: ConnectionIdentifier): Box[Connection] =
    synchronized {
      pool match {
	      case Nil if poolSize < maxPoolSize =>
	        val ret = createOne
          poolSize = poolSize + 1
          ret.foreach(c => pool = c :: pool)
          ret
	      case Nil => wait(1000L); newConnection(name)
	      case x :: xs => try {
          x.setAutoCommit(false)
          Full(x)
        } catch {
          case e => try {
            pool = xs
            poolSize = poolSize - 1
            x.close
            newConnection(name)
          } catch {
            case e => newConnection(name)
          }
        }
      }
    }

  def releaseConnection(conn: Connection): Unit = synchronized {
    pool = conn :: pool
    notify
  }
}


