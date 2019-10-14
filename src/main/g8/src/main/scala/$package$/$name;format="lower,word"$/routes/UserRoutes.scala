package $package;format="lower,package"$.$name;format="lower,word"$.routes

import cats.effect.Effect
import cats.instances.string._
import cats.syntax.flatMap._
import cats.syntax.functor._
import $package;format="lower,package"$.$name;format="lower,word"$.logging.Logger
import $package;format="lower,package"$.$name;format="lower,word"$.persistence.UserPersistence
import $package;format="lower,package"$.$name;format="lower,word"$.routes.requests.CreateUserRequest
import $package;format="lower,package"$.$name;format="lower,word"$.routes.util.RequestDeserializer
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class UserRoutes[F[_]: Effect](persistence: UserPersistence[F], logger: Logger) extends Http4sDsl[F] {

  val service: HttpRoutes[F] = HttpRoutes
    .of[F] {
      case GET -> Root / "health" =>
        Ok()

      case req @ GET -> Root / "users" =>
        for {
          _        <- logger.debug(req.pathInfo)
          users    <- persistence.list
          response <- Ok(users.asJson)
          _        <- response.as[String].flatMap(body => logger.debug(body))
        } yield response

      case GET -> Root / "users" / IntVar(userId) =>
        for {
          userOpt  <- persistence.get(userId)
          response <- userOpt.map(u => Ok(u.asJson)).getOrElse(NotFound())
        } yield response

      case request @ POST -> Root / "users" =>
        for {
          user     <- RequestDeserializer.deserialize[F, CreateUserRequest](request)
          _        <- persistence.create(user.name)
          response <- Created()
        } yield response
    }
}
