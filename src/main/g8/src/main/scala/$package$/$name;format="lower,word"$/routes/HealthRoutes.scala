package $package;format="lower,package"$.$name;format="lower,word"$.routes

import cats.effect.Effect
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class HealthRoutes[F[_]: Effect] extends Http4sDsl[F] {

  val service: HttpRoutes[F] = HttpRoutes
    .of[F] {
      case GET -> Root / "health" =>
        Ok()
    }
}
