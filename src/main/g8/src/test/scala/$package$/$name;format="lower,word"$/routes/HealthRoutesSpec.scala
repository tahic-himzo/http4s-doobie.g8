package $package;format="lower,package"$.$name;format="lower,word"$.routes

import cats.effect.IO
import org.http4s._
import org.http4s.syntax.kleisli.http4sKleisliResponseSyntax
import org.scalatest.{EitherValues, Matchers, WordSpec}

class HealthRoutesSpec extends WordSpec with Matchers with EitherValues {
  "HealthRoutes" should {
    "signal health with empty OK (GET /health)" in {
      val routes    = new HealthRoutes[IO]().service.orNotFound
      val getHealth = Request[IO](method = Method.GET, uri = uri"""/health""")
      val result    = routes.run(getHealth).unsafeRunSync()

      result.status shouldEqual Status.Ok
    }
  }
}
