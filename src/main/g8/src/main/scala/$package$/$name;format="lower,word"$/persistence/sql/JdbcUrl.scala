package $package;format="lower,package"$.$name;format="lower,word"$.persistence.sql

object JdbcUrl {

  def apply(databaseHost: String, databaseName: String, serverTimezone: String): String =
    s"jdbc:mysql://$databaseHost/$databaseName?serverTimezone=$serverTimezone"
}
