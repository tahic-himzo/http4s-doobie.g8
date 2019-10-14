package $package;format="lower,package"$.$name;format="lower,word"$.problem.model

import io.circe._

object ProblemTitle {
  implicit val encoder: Encoder[ProblemTitle] = Encoder.encodeString.contramap(_.value)
  implicit val decoder: Decoder[ProblemTitle] = Decoder.decodeString.map(ProblemTitle.apply)
}

final case class ProblemTitle(value: String) extends AnyVal
