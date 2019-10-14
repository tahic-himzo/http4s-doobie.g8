package $package;format="lower,package"$.$name;format="lower,word"$

import cats.effect._
import cats.syntax.semigroupk._
import $package;format="lower,package"$.$name;format="lower,word"$.config.AppConfig
import $package;format="lower,package"$.$name;format="lower,word"$.config.core.ConfigEnvironment
import $package;format="lower,package"$.$name;format="lower,word"$.logging.Sl4jLogger
import $package;format="lower,package"$.$name;format="lower,word"$.persistence.sql._
import $package;format="lower,package"$.$name;format="lower,word"$.routes.{HealthRoutes, RoutesErrorHandler, UserFeedbackRoutes, UserRoutes}
import doobie.hikari.HikariTransactor
import org.http4s.syntax.kleisli._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      env        <- ConfigEnvironment.apply[IO](sys.env)
      config     <- AppConfig.load[IO](env, sys.env)
      transactor <- IO.delay(HikariTransactorFactory.apply[IO](config.databaseConfig))
      _          <- transactor.use(program(_))
    } yield ExitCode.Success

  def program[F[_]: ConcurrentEffect](transactor: HikariTransactor[F])(implicit timer: Timer[F]): F[ExitCode] = {
    val logger             = new Sl4jLogger("flourisher")
    val routesErrorHandler = new RoutesErrorHandler[F](logger)
    println(routesErrorHandler)
    val healthRoutes = new HealthRoutes[F]().service

    val userPersistence = new DoobieUserPersistence[F](transactor)
    val userRoutes      = new UserRoutes[F](userPersistence, logger).service

    val feedbackPersistence = new DoobieFeedbackPersistence[F](transactor)
    val userFeedbackRoutes  = new UserFeedbackRoutes[F](feedbackPersistence, userPersistence, logger).service

    val app = userRoutes <+> userFeedbackRoutes <+> healthRoutes
    Server.run[F](app.orNotFound)
  }
}
