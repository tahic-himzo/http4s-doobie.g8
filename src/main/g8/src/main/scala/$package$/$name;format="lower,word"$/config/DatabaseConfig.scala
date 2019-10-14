package $package;format="lower,package"$.$name;format="lower,word"$.config

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

final case class DatabaseConfig(
    host:               String,
    driverClassName:    String,
    databaseName:       String,
    user:               String,
    password:           String,
    connectionPoolSize: Int,
    serverTimezone:     String)

object DatabaseConfig {

  implicit def configReader: ConfigReader[DatabaseConfig] = deriveReader[DatabaseConfig]
}
