package $package;format="lower,package"$.$name;format="lower,word"$.routes

import cats.effect.Effect
import cats.instances.string._
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import cats.syntax.functor._
import $package;format="lower,package"$.$name;format="lower,word"$.logging.Logger
import $package;format="lower,package"$.$name;format="lower,word"$.persistence._
import $package;format="lower,package"$.$name;format="lower,word"$.problem.model.Problem
import $package;format="lower,package"$.$name;format="lower,word"$.problem.syntax.ProblemSyntax._
import $package;format="lower,package"$.$name;format="lower,word"$.routes.query.TargetUserQueryParameter
import $package;format="lower,package"$.$name;format="lower,word"$.routes.requests.CreateFeedbackRequest
import $package;format="lower,package"$.$name;format="lower,word"$.routes.util.RequestDeserializer
import io.circe.syntax.EncoderOps
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class UserFeedbackRoutes[F[_]: Effect](
    feedbackPersistence: FeedbackPersistence[F],
    userPersistence:     UserPersistence[F],
    logger:              Logger)
    extends Http4sDsl[F] {

  val service: HttpRoutes[F] = HttpRoutes
    .of[F] {
      case req @ GET -> Root / "users" / IntVar(userId) / "feedback" / IntVar(feedbackId) =>
        for {
          _           <- logger.debug(req.pathInfo)
          feedbackOpt <- feedbackPersistence.get(userId, feedbackId)
          response    <- feedbackOpt.map(f => Ok(f.asJson)).getOrElse(NotFound())
          _           <- response.as[String].flatMap(body => logger.debug(body))
        } yield response

      case GET -> Root / "users" / IntVar(userId) / "feedback" =>
        for {
          feedback <- feedbackPersistence.listBySourceUser(sourceUser = userId)
          response <- Ok(feedback.asJson)
        } yield response

      case GET -> Root / "users" / "-" / "feedback" :? TargetUserQueryParameter(userId) =>
        for {
          users    <- feedbackPersistence.listByTargetUser(targetUser = userId)
          response <- Ok(users.asJson)
        } yield response

      case request @ POST -> Root / "users" / IntVar(userId) / "feedback" =>
        (for {
          body <- RequestDeserializer.deserialize[F, CreateFeedbackRequest](request)
          _ <- userPersistence.get(userId).flatMap {
            case Some(_) => Effect[F].unit
            case None    => Effect[F].raiseError[Unit](SourceUserDoesNotExist(userId))
          }
          _ <- userPersistence.get(body.targetUser).flatMap {
            case Some(_) => Effect[F].unit
            case None    => Effect[F].raiseError[Unit](TargetUserDoesNotExist(body.targetUser))
          }
          _        <- feedbackPersistence.create(body.title, body.content, userId, body.targetUser)
          response <- Created()
        } yield response).handleErrorWith {
          case err @ SourceUserDoesNotExist(_) => NotFound(err.asProblem.asJson)
          case err @ TargetUserDoesNotExist(_) => BadRequest(err.asProblem.asJson)
          case _                               => InternalServerError(Problem.unexpectedError.asJson)
        }
    }
}
