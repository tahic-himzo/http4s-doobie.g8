package $package;format="lower,package"$.$name;format="lower,word"$.routes

import cats.effect.IO
import $package;format="lower,package"$.$name;format="lower,word"$.arbitraries.ConfigArbitraries
import $package;format="lower,package"$.$name;format="lower,word"$.config.AppConfig
import $package;format="lower,package"$.$name;format="lower,word"$.model.{Feedback, User}
import $package;format="lower,package"$.$name;format="lower,word"$.problem.syntax.ProblemSyntax._
import $package;format="lower,package"$.$name;format="lower,word"$.routes.requests.CreateFeedbackRequest
import $package;format="lower,package"$.$name;format="lower,word"$.stubs.{InMemoryFeedbackPersistence, InMemoryUserPersistence, NoopLogger}
import io.circe.Json
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.syntax.kleisli.http4sKleisliResponseSyntax
import org.scalatest.{EitherValues, Matchers, WordSpec}

class UserFeedbackRoutesSpec extends WordSpec with Matchers with EitherValues {
  val config       = AppConfig(ConfigArbitraries.databaseConfig)
  val errorHandler = new RoutesErrorHandler[IO](NoopLogger)

  "UserFeedbackRoutes" should {

    val users           = List(User(1, "Source User"), User(2, "Target User"))
    val userPersistence = new InMemoryUserPersistence[IO](initialUsers = users)
    val feedback =
      List(
        Feedback(1, "some-title", "some-content", users.head, users.tail.head),
        Feedback(2, "some-other-title", "some-other-content", users.tail.head, users.head)
      )

    "create feedback (POST /users/:parent/feedback)" in {
      val feedbackPersistence = new InMemoryFeedbackPersistence[IO](userPersistence)
      val routes              = new UserFeedbackRoutes[IO](feedbackPersistence, userPersistence, NoopLogger).service.orNotFound

      val singleFeedback = feedback.head
      val createFeedbackRequest =
        CreateFeedbackRequest(singleFeedback.title, singleFeedback.content, singleFeedback.targetUser.id)
      val request =
        Request[IO](method = Method.POST, uri = uri"""/users/1/feedback""").withEntity(createFeedbackRequest.asJson)
      val response = routes.run(request).unsafeRunSync()

      response.status shouldEqual Status.Created
      feedbackPersistence.listBySourceUser(id = 1).unsafeRunSync() shouldEqual List(singleFeedback)
    }

    "list feedback sent by user (GET /users/:parent/feedback)" in {

      val feedbackPersistence = new InMemoryFeedbackPersistence[IO](userPersistence, initialFeedback = feedback)
      val routes              = new UserFeedbackRoutes[IO](feedbackPersistence, userPersistence, NoopLogger).service.orNotFound

      val request  = Request[IO](method = Method.GET, uri = uri"""/users/1/feedback""")
      val response = routes.run(request).unsafeRunSync()

      response.status shouldEqual Status.Ok
      response.as[Json].unsafeRunSync() shouldEqual List(feedback.head).asJson
    }

    "list feedback sent to user (GET /users/-/feedback?target_user=:id)" in {
      val feedbackPersistence = new InMemoryFeedbackPersistence[IO](userPersistence, initialFeedback = feedback)
      val routes              = new UserFeedbackRoutes[IO](feedbackPersistence, userPersistence, NoopLogger).service.orNotFound

      val request  = Request[IO](method = Method.GET, uri = uri"""/users/-/feedback?target_user=1""")
      val response = routes.run(request).unsafeRunSync()

      response.status shouldEqual Status.Ok
      response.as[Json].unsafeRunSync() shouldEqual List(feedback.tail.head).asJson
    }

    "get existing feedback (GET /users/:parent/feedback/:id)" in {
      val feedbackPersistence = new InMemoryFeedbackPersistence[IO](userPersistence, initialFeedback = feedback)
      val routes              = new UserFeedbackRoutes[IO](feedbackPersistence, userPersistence, NoopLogger).service.orNotFound

      val request  = Request[IO](method = Method.GET, uri = uri"""/users/1/feedback/1""")
      val response = routes.run(request).unsafeRunSync()

      response.status shouldEqual Status.Ok
      response.as[Json].unsafeRunSync() shouldEqual feedback.head.asJson
    }

    "fail to get not existing feedback (GET /users/:parent/feedback/:id)" in {
      val feedbackPersistence = new InMemoryFeedbackPersistence[IO](userPersistence, initialFeedback = feedback)
      val routes              = new UserFeedbackRoutes[IO](feedbackPersistence, userPersistence, NoopLogger).service.orNotFound

      val request  = Request[IO](method = Method.GET, uri = uri"""/users/1/feedback/2""")
      val response = routes.run(request).unsafeRunSync()

      response.status shouldEqual Status.NotFound
      response.as[String].unsafeRunSync() shouldBe ""
    }

    "return 404 with problem when trying create feedback for not existing source user (POST /users/:parent/feedback)" in {
      val feedbackPersistence = new InMemoryFeedbackPersistence[IO](userPersistence)
      val routes              = new UserFeedbackRoutes[IO](feedbackPersistence, userPersistence, NoopLogger).service.orNotFound

      val singleFeedback = feedback.head
      val createFeedbackRequest =
        CreateFeedbackRequest(singleFeedback.title, singleFeedback.content, singleFeedback.targetUser.id)
      val request =
        Request[IO](method = Method.POST, uri = uri"""/users/3/feedback""").withEntity(createFeedbackRequest.asJson)
      val response = routes.run(request).unsafeRunSync()

      response.status shouldEqual Status.NotFound
      response.as[Json].unsafeRunSync() shouldEqual SourceUserDoesNotExist(id = 3).asProblem.asJson
    }

    "return 400 with problem when trying create feedback for not existing target user (POST /users/:parent/feedback)" in {
      val feedbackPersistence = new InMemoryFeedbackPersistence[IO](userPersistence)
      val routes              = new UserFeedbackRoutes[IO](feedbackPersistence, userPersistence, NoopLogger).service.orNotFound

      val singleFeedback        = feedback.head
      val createFeedbackRequest = CreateFeedbackRequest(singleFeedback.title, singleFeedback.content, 3)
      val request =
        Request[IO](method = Method.POST, uri = uri"""/users/1/feedback""").withEntity(createFeedbackRequest.asJson)
      val response = routes.run(request).unsafeRunSync()

      response.status shouldEqual Status.BadRequest
      response.as[Json].unsafeRunSync() shouldEqual TargetUserDoesNotExist(id = 3).asProblem.asJson
    }
  }
}
