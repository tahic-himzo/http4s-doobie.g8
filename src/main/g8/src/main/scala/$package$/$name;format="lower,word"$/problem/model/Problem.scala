package $package;format="lower,package"$.$name;format="lower,word"$.problem.model

import io.circe._
import io.circe.derivation.{renaming, _}
import org.http4s.Status

final case class Problem(title: ProblemTitle, statusCode: ProblemStatusCode)

object Problem {
  implicit val encoder: Encoder[Problem] = deriveEncoder(renaming.snakeCase)
  implicit val decoder: Decoder[Problem] = deriveDecoder(renaming.snakeCase)

  def invalidJson: Problem = Problem(
    ProblemTitle("Invalid JSON in request body"),
    ProblemStatusCode(Status.BadRequest.code)
  )

  val unexpectedError: Problem =
    Problem(
      ProblemTitle("Internal Server Error - Please try again later."),
      ProblemStatusCode(Status.InternalServerError.code)
    )
}
