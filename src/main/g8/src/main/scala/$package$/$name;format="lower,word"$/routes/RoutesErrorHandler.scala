package $package;format="lower,package"$.$name;format="lower,word"$.routes

import cats.Show
import cats.effect.Effect
import cats.syntax.flatMap._
import $package;format="lower,package"$.$name;format="lower,word"$.logging.Logger
import $package;format="lower,package"$.$name;format="lower,word"$.problem.model.Problem
import $package;format="lower,package"$.$name;format="lower,word"$.routes.util.DeserializationFailedError
import io.circe.syntax._
import org.http4s.Response
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class RoutesErrorHandler[F[_]: Effect](logger: Logger) extends Http4sDsl[F] {

  val handle: Throwable => F[Response[F]] = err =>
    logger.warn(err)(Effect[F], Show.fromToString) >> {
      err match {
        case _: DeserializationFailedError =>
          BadRequest(Problem.invalidJson.asJson)
        case _: Throwable =>
          InternalServerError(Problem.unexpectedError.asJson)
      }
  }
}
