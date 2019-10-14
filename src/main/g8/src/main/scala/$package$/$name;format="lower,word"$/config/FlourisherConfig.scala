package $package;format="lower,package"$.$name;format="lower,word"$.config

import cats.effect.Effect
import $package;format="lower,package"$.$name;format="lower,word"$.config.core.{ConfigEnvironment, ConfigLoader}
import pureconfig._
import pureconfig.generic.ProductHint
import pureconfig.generic.semiauto._

final case class AppConfig(databaseConfig: DatabaseConfig)

object AppConfig {

  def load[F[_]: Effect](env: ConfigEnvironment, envProps: Map[String, String]): F[AppConfig] =
    ConfigLoader.load("$name;format="lower,word"$", env, envProps)

  implicit def configReader: ConfigReader[AppConfig] =
    deriveReader[AppConfig]

  implicit def productHint[T]: ProductHint[T] =
    ProductHint[T](allowUnknownKeys = false, useDefaultArgs = false)
}
