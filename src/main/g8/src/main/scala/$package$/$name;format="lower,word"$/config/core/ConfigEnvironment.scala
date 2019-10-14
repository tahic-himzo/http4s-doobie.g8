package $package;format="lower,package"$.$name;format="lower,word"$.config.core

import cats.syntax.flatMap._
import cats.effect.Effect

sealed abstract class ConfigEnvironment protected (val name: String)

object ConfigEnvironment {

  object Local extends ConfigEnvironment("local")
  val all = List(Local)

  def apply[F[_]: Effect](env: Map[String, String]): F[ConfigEnvironment] =
    Effect[F]
      .pure(env.get("env"))
      .flatMap {
        case Some("local") => Effect[F].pure(Local)
        case None          => Effect[F].raiseError(new RuntimeException("Environment Variable 'env' is not defined"))
      }
}
