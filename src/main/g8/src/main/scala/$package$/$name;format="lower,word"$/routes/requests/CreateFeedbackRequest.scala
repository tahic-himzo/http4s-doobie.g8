package $package;format="lower,package"$.$name;format="lower,word"$.routes.requests

import cats.effect.Effect
import io.circe.{Decoder, Encoder}
import io.circe.derivation.{deriveDecoder, deriveEncoder, renaming}
import org.http4s.{circe, EntityDecoder}

final case class CreateFeedbackRequest(title: String, content: String, targetUser: Int)

object CreateFeedbackRequest {
  implicit val encoder: Encoder[CreateFeedbackRequest]                              = deriveEncoder(renaming.snakeCase)
  implicit val decoder: Decoder[CreateFeedbackRequest]                              = deriveDecoder(renaming.snakeCase)
  implicit def entityDecoder[F[_]: Effect]: EntityDecoder[F, CreateFeedbackRequest] = circe.jsonOf[F, CreateFeedbackRequest]
}
