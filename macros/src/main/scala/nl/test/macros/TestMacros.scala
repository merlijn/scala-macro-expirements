package nl.test.macros

import scala.language.experimental.macros
import reflect.macros.whitebox.Context

object TestMacros {

  def n(arg: Int): NatWrapper = macro n_impl

  def n_impl(c: Context)(arg: c.Expr[Int]): c.Expr[NatWrapper] = {
    import c.universe._

    val n = arg.tree match {
      case Literal(Constant(value: Int)) => value
      case _                             => c.abort(c.enclosingPosition, "This macro requires an int literal as it's argument")
    }

    val nat0 = Select(Select(Ident(TermName("shapeless")), TermName("Nat")), TypeName("_0"))
    val succ = Select(Ident(TermName("shapeless")), TypeName("Succ"))

    def succN(i: Int): Tree = i match {
      case n if n < 0 => c.abort(c.enclosingPosition, "n must be a natural number")
      case 0 => nat0
      case 1 => AppliedTypeTree(succ, List(nat0))
      case n => AppliedTypeTree(succ, List(succN(n-1)))
    }

    val w = succN(n)

    c.Expr(q"new nl.test.macros.Foo { override type W = $w }")
  }

  def switch(code: Unit): Unit = macro switch_impl

  def switch_impl(c: Context)(code: c.Expr[Unit]): c.Expr[Unit] = {
    import c.universe._

    val block = code.tree match {
      case Block(a :: b :: Nil, c) => Block(List(b, a), c)
      case _                       => Block(Nil, Literal(Constant()))
    }

    c.Expr[Unit](block)
  }

  def unique(ints: Int*): Unit = macro unique_impl

  def unique_impl(c: Context)(ints: c.Expr[Int]*): c.Expr[Unit] = {
    import c.universe._

    val set = ints.map(_.tree).collect {
      case Literal(Constant(c)) => c
      case _                    => c.abort(c.enclosingPosition, "Illegal argument, only int literals may be used")
    }.toSet

    if (set.size != ints.length)
      c.abort(c.enclosingPosition, "Int arguments are not unique")

    reify(println("unique test passed"))
  }
}
