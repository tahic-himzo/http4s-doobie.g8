package $package;format="lower,package"$.$name;format="lower,word"$.routes.requests

import cats.effect.Effect
import io.circe.derivation.{deriveDecoder, deriveEncoder, renaming}
import io.circe.{Decoder, Encoder}
import org.http4s.{circe, EntityDecoder}

final case class CreateUserRequest(name: String)

object CreateUserRequest {
  implicit val encoder: Encoder[CreateUserRequest]                              = deriveEncoder(renaming.snakeCase)
  implicit val decoder: Decoder[CreateUserRequest]                              = deriveDecoder(renaming.snakeCase)
  implicit def entityDecoder[F[_]: Effect]: EntityDecoder[F, CreateUserRequest] = circe.jsonOf[F, CreateUserRequest]
}
