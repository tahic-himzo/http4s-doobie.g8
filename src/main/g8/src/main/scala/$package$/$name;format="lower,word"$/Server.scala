package $package;format="lower,package"$.$name;format="lower,word"$

import cats.effect._
import cats.syntax.functor._
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder

object Server {

  def run[F[_]: ConcurrentEffect](httpApp: HttpApp[F])(implicit timer: Timer[F]): F[ExitCode] =
    BlazeServerBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
