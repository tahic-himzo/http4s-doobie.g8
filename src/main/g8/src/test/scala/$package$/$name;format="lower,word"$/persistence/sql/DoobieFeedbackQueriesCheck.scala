package $package;format="lower,package"$.$name;format="lower,word"$.persistence.sql

import cats.effect.{ContextShift, IO}
import $package;format="lower,package"$.$name;format="lower,word"$.config.DatabaseConfig
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import org.scalatest._

class DoobieFeedbackQueriesCheck extends FunSuite with Matchers with doobie.scalatest.IOChecker {
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

  val testDatabaseConfig = DatabaseConfig("localhost", "com.mysql.cj.jdbc.Driver", "$name;format="lower,hyphen"$", "root", "password", 10, "UTC")

  val transactor: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    testDatabaseConfig.driverClassName,
    JdbcUrl.apply(testDatabaseConfig.host, testDatabaseConfig.databaseName, testDatabaseConfig.serverTimezone),
    testDatabaseConfig.user,
    testDatabaseConfig.password
  )

  test("list feedback by user") {
    checkOutput(FeedbackQueries.listBySourceUser(1))
  }
  test("list feedback to user") {
    checkOutput(FeedbackQueries.listByTargetUser(1))
  }
  test("get feedback") {
    checkOutput(FeedbackQueries.get(1, 1))
  }
}
