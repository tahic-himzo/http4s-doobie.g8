package $package;format="lower,package"$.$name;format="lower,word"$.model

import io.circe.Encoder
import io.circe.derivation.{deriveEncoder, renaming}

final case class Feedback(id: Int, title: String, content: String, sourceUser: User, targetUser: User)

object Feedback {
  implicit def encoder: Encoder[Feedback] = deriveEncoder[Feedback](renaming.snakeCase)
}
