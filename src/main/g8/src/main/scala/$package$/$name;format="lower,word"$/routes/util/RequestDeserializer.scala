package $package;format="lower,package"$.$name;format="lower,word"$.routes.util

import cats.effect.Effect
import cats.syntax.flatMap._
import org.http4s.{EntityDecoder, Request}

object RequestDeserializer {

  def deserialize[F[_]: Effect, A](request: Request[F])(implicit decoder: EntityDecoder[F, A]): F[A] =
    request
      .attemptAs[A]
      .value
      .flatMap {
        case Right(value) => Effect[F].pure(value)
        case Left(err) =>
          request.as[String].flatMap(body => Effect[F].raiseError(DeserializationFailedError(err.getMessage, body)))
      }
}

case class DeserializationFailedError(message: String, original: String) extends Exception
