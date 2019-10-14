package $package;format="lower,package"$.$name;format="lower,word"$.problem.model

import io.circe._

final case class ProblemStatusCode(value: Int) extends AnyVal

object ProblemStatusCode {
  implicit val encoder: Encoder[ProblemStatusCode] = Encoder.encodeInt.contramap(_.value)
  implicit val decoder: Decoder[ProblemStatusCode] = Decoder.decodeInt.map(ProblemStatusCode.apply)
}
