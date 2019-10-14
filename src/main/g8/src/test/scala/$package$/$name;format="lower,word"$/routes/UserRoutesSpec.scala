package $package;format="lower,package"$.$name;format="lower,word"$.routes

import cats.effect.IO
import $package;format="lower,package"$.$name;format="lower,word"$.arbitraries.ConfigArbitraries
import $package;format="lower,package"$.$name;format="lower,word"$.config.AppConfig
import $package;format="lower,package"$.$name;format="lower,word"$.model.User
import $package;format="lower,package"$.$name;format="lower,word"$.routes.requests.CreateUserRequest
import $package;format="lower,package"$.$name;format="lower,word"$.stubs.{InMemoryUserPersistence, NoopLogger}
import io.circe.Json
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.syntax.kleisli.http4sKleisliResponseSyntax
import org.scalatest.{EitherValues, Matchers, WordSpec}

class UserRoutesSpec extends WordSpec with Matchers with EitherValues {
  val config       = AppConfig(ConfigArbitraries.databaseConfig)
  val errorHandler = new RoutesErrorHandler[IO](NoopLogger)

  "UserRoutes" should {
    "create user (POST /users)" in {
      val persistence = new InMemoryUserPersistence[IO]
      val routes      = new UserRoutes[IO](persistence, NoopLogger).service.orNotFound

      val user     = User(1, "Test User")
      val request  = Request[IO](method = Method.POST, uri = uri"""/users""").withEntity(CreateUserRequest(user.name).asJson)
      val response = routes.run(request).unsafeRunSync()

      response.status shouldEqual Status.Created
      persistence.list.unsafeRunSync() shouldEqual List(user)
    }

    "get existing user (GET /users/:id)" in {
      val user        = User(1, "Test User 1")
      val persistence = new InMemoryUserPersistence[IO](initialUsers = List(user))
      val routes      = new UserRoutes[IO](persistence, NoopLogger).service.orNotFound

      val request  = Request[IO](method = Method.GET, uri = uri"""/users/1""")
      val response = routes.run(request).unsafeRunSync()

      response.status shouldEqual Status.Ok
      response.as[Json].unsafeRunSync() shouldEqual user.asJson
    }

    "fail to get not existing user (GET /users/:id)" in {
      //val user        = User(1, "Test User 1")
      val persistence = new InMemoryUserPersistence[IO](initialUsers = List.empty)
      val routes      = new UserRoutes[IO](persistence, NoopLogger).service.orNotFound

      val request  = Request[IO](method = Method.GET, uri = uri"""/users/1""")
      val response = routes.run(request).unsafeRunSync()

      response.status shouldEqual Status.NotFound
      response.as[String].unsafeRunSync() shouldBe ""
    }

    "list users (GET /users)" in {
      val users       = List(User(1, "Test User 1"), User(2, "Test User 2"))
      val persistence = new InMemoryUserPersistence[IO](initialUsers = users)
      val routes      = new UserRoutes[IO](persistence, NoopLogger).service.orNotFound

      val request  = Request[IO](method = Method.GET, uri = uri"""/users""")
      val response = routes.run(request).unsafeRunSync()

      response.status shouldEqual Status.Ok
      response.as[Json].unsafeRunSync() shouldEqual users.asJson
    }
  }
}
