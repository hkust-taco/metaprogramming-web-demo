package mlscript.ucs

import mlscript._
import mlscript.utils.shorthands._

import helpers._

class PartialTermError(term: PartialTerm, message: Str) extends Error(message)

/**
  * A `PartialTerm` represents a possibly incomplete term.
  * We'd better precisely track detailed locations of each parts.
  * 
  * @param fragments fragment terms that used to build this `PartialTerm`.
  */
sealed abstract class PartialTerm {
  val fragments: Ls[Term]
  def addTerm(term: Term, newDefs: Bool): PartialTerm.Total
  def addOp(op: Var): PartialTerm.Half
  def addTermOp(term: Term, op: Var, newDefs: Bool): PartialTerm.Half =
    this.addTerm(term, newDefs).addOp(op)
}

object PartialTerm {
  final case object Empty extends PartialTerm {
    val fragments: Ls[Term] = Nil
    def addTerm(term: Term, newDefs: Bool): Total = Total(term, term :: Nil)
    def addOp(op: Var): Half =
      throw new PartialTermError(this, s"expect a term but operator $op was given")
  }

  final case class Total(term: Term, fragments: Ls[Term]) extends PartialTerm {
    def addTerm(term: Term, newDefs: Bool): Total =
      throw new PartialTermError(this, s"expect an operator but term $term was given")
    def addOp(op: Var): Half = Half(term, op, op :: fragments)
  }

  final case class Half(lhs: Term, op: Var, fragments: Ls[Term]) extends PartialTerm {
    def addTerm(rhs: Term, newDefs: Bool): Total = {
      val (realRhs, extraExprOpt) = separatePattern(rhs, newDefs)
      val leftmost = mkBinOp(lhs, op, realRhs, newDefs)
      extraExprOpt match {
        case N            => Total(leftmost, fragments)
        case S(extraExpr) => Total(mkBinOp(leftmost, Var("and"), extraExpr, newDefs), extraExpr :: fragments)
      }
    }
    def addOp(op: Var): Half =
      throw new PartialTermError(this, s"expect a term but operator $op was given")
  }
}
