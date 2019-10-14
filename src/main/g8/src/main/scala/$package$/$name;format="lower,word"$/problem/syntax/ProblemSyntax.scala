package $package;format="lower,package"$.$name;format="lower,word"$.problem.syntax

import $package;format="lower,package"$.$name;format="lower,word"$.problem.ProblemConverter
import $package;format="lower,package"$.$name;format="lower,word"$.problem.model.Problem

object ProblemSyntax {

  implicit class ProblemConverterOps[A](value: A) {

    def asProblem(implicit pc: ProblemConverter[A]): Problem =
      pc.convert(value)
  }

}
