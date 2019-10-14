package $package;format="lower,package"$.$name;format="lower,word"$.model

import io.circe.Encoder
import io.circe.derivation.{deriveEncoder, renaming}

final case class User(id: Int, name: String)

object User {
  implicit def encoder: Encoder[User] = deriveEncoder[User](renaming.snakeCase)
}
