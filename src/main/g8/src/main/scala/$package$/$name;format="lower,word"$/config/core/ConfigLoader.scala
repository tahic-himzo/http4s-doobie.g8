package $package;format="lower,package"$.$name;format="lower,word"$.config.core

import cats.effect.Effect
import com.typesafe.config._
import pureconfig.generic.ProductHint
import pureconfig.{ConfigReader, ConfigSource}

object ConfigLoader {

  def load[F[_]: Effect, ConfigType: ConfigReader](
      namespace: String,
      env:       ConfigEnvironment,
      envProps:  Map[String, String]): F[ConfigType] = {

    val resolveOptions = ConfigResolveOptions.noSystem().appendResolver(new FallbackResolver(envProps))
    val configName     = s"application.\${env.name}.conf"
    val config         = ConfigFactory.load(configName, ConfigParseOptions.defaults(), resolveOptions)

    ConfigSource.fromConfig(config).at(namespace).load[ConfigType] match {
      case Left(err) =>
        val errorString = err.toList.mkString(",")
        Effect[F].raiseError(new RuntimeException(s"Unable to load config: \${errorString}"))
      case Right(pureConfig) =>
        Effect[F].pure(pureConfig)
    }
  }

  implicit def productHint[T]: ProductHint[T] = ProductHint[T](allowUnknownKeys = false, useDefaultArgs = false)
}

class FallbackResolver(props: Map[String, String]) extends ConfigResolver {

  override def lookup(path: String): ConfigValue = props.get(path).map(ConfigValueFactory.fromAnyRef).orNull

  override def withFallback(fallback: ConfigResolver): ConfigResolver = fallback
}
