package $package;format="lower,package"$.$name;format="lower,word"$.problem

import $package;format="lower,package"$.$name;format="lower,word"$.problem.model.Problem

trait ProblemConverter[T] {
  def convert(t: T): Problem
}
