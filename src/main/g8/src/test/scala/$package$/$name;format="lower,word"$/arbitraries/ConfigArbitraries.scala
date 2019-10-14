package $package;format="lower,package"$.$name;format="lower,word"$.arbitraries

import $package;format="lower,package"$.$name;format="lower,word"$.config.DatabaseConfig

object ConfigArbitraries {

  val databaseConfig: DatabaseConfig =
    DatabaseConfig("some-host", "some-driver", "some-db-name", "some-user", "some-pw", 10, "some-timezone")
}
