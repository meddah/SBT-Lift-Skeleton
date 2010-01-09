package web

import org.specs._
import org.specs.matcher.Matcher
import net.liftweb.util.PCDataXmlParser
import net.liftweb.common.Full
import java.io.File
import scala.xml.XML

object AppTest extends Specification {
  "All project xml files are well-formed" in {
    "src/main/webapp" must haveWellFormedFiles()
  }
}

case class haveWellFormedFiles() extends Matcher[String]() {
  def apply(directory: => String) = {
    var failed: List[File] = Nil

    def handledXml(file: String) =
      file.endsWith(".xml")

    def handledXHtml(file: String) =
      file.endsWith(".html") || file.endsWith(".htm") || file.endsWith(".xhtml")

    def wellFormed(file: File) {
      if (file.isDirectory)
        for (f <- file.listFiles) wellFormed(f)

      if (file.isFile && handledXml(file.getName)) {
        try {
          XML.loadFile(file)
        } catch {
          case e: _root_.org.xml.sax.SAXParseException => failed = file :: failed
        }
      }
      if (file.isFile && handledXHtml(file.getName)) {
        PCDataXmlParser(new java.io.FileInputStream(file.getAbsolutePath)) match {
          case Full(_) => // file is ok
          case _ => failed = file :: failed
        }
      }
    }

    wellFormed(new File(directory))
    
    val numFails = failed.size
    if (numFails > 0) {
      val fileStr = if (numFails == 1) "file" else "files"
      val msg = " has malformed XML in " + numFails + " " + fileStr + ": " + failed.mkString(", ")
      (false, "", msg)
    } else {
      (true, " has well-formed files", "")
    }
  }
}
