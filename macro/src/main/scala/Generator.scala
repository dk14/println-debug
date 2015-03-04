/**
 * Created by user on 9/24/14.
 */

import scala.collection.mutable
import scala.reflect.macros.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.util.parsing.combinator.RegexParsers

import java.util.logging._

class trace extends StaticAnnotation { //(logger: Logger = Logger.getLogger(getClass.getName()))
  def macroTransform(annottees: Any*) = macro tracerMacro.impl
}

object MacroLogger {
  import scala.collection.mutable
  val thL = new ThreadLocal[mutable.Map[String, Int]]{ override def initialValue = mutable.Map[String, Int]() withDefaultValue(0)}
  def map = thL.get()
  def key(implicit obj: (Any, String)) = obj.toString

  def prefix(implicit obj: (Any, String)) = List.fill(map(key))(" ").mkString
  def log (s: String)(implicit obj: (Any, String))  = println(prefix + s) //Logger.getLogger(obj.getClass.getName).log(Level.FINE, prefix(obj) + s)
  def in(s: String)(implicit obj: (Any, String)) = { map.update(key, map(key) + 1); log(s) }
  def out(s: String)(implicit obj: (Any, String)) = { log(s); map.update(key, map(key) - 1)}
}

object tracerMacro {

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import scala.io.Source
    val grammarRaw = Source.fromURL(this.getClass.getResource("/option.template")).getLines().mkString("\n")

    import c.universe._
    val inputs = annottees.map(_.tree).toList


    val output = inputs.head match {
      case q"class $name extends $parent { ..$body }" =>
        q"""
            class $name extends $parent {
              ..${
                   body.map {
                       case x@q"def $name (..$params): $typ = $body" =>
                         println("name = " + name + " " + params)
                       q"""def $name (..$params): $typ = {
                             val nm = ${name.toString}
                             MacroLogger.in(nm + "(" + List(..${params map { case x@q"$param" => q"""  ${param.name.toString} + " = " + ${param.name}""" }}).mkString(", ") + ")")(this -> nm)
                             val res = try {
                                val res = $body
                                MacroLogger.out(nm + " = " + res.toString)(this -> nm)
                                res
                             } catch {
                               case t: Throwable => MacroLogger.out(nm + " = " + t)(this -> nm); throw(t)
                             }
                             res
                           }"""

                   }
                 }
            }
          """
      case x => sys.error(x.toString)
    }

    println(output)
    c.Expr[Any](output)
  }
}

object Experiment extends App {

}
