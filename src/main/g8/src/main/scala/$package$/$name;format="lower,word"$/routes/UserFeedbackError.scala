package $package;format="lower,package"$.$name;format="lower,word"$.routes

import $package;format="lower,package"$.$name;format="lower,word"$.problem.ProblemConverter
import $package;format="lower,package"$.$name;format="lower,word"$.problem.model._

sealed trait UserFeedbackError extends Exception

case class SourceUserDoesNotExist(id: Int) extends UserFeedbackError

object SourceUserDoesNotExist {
  implicit val problemConverter: ProblemConverter[SourceUserDoesNotExist] = (err: SourceUserDoesNotExist) =>
    Problem(
      ProblemTitle(s"Source user '${err.id}' does not exist"),
      ProblemStatusCode(100)
    )
}
case class TargetUserDoesNotExist(id: Int) extends UserFeedbackError

object TargetUserDoesNotExist {
  implicit val problemConverter: ProblemConverter[TargetUserDoesNotExist] = (err: TargetUserDoesNotExist) =>
    Problem(
      ProblemTitle(s"Target user '${err.id}' does not exist"),
      ProblemStatusCode(200)
    )
}
