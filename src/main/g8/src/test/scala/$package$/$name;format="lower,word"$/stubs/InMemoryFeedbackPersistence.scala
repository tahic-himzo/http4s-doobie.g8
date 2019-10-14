package $package;format="lower,package"$.$name;format="lower,word"$.stubs

import java.util.concurrent.atomic.AtomicInteger

import cats.effect.Effect
import cats.syntax.functor._
import cats.syntax.flatMap._
import $package;format="lower,package"$.$name;format="lower,word"$.model.{Feedback, User}
import $package;format="lower,package"$.$name;format="lower,word"$.persistence.FeedbackPersistence
import $package;format="lower,package"$.$name;format="lower,word"$.routes.{SourceUserDoesNotExist, TargetUserDoesNotExist}

import scala.collection.mutable.ArrayBuffer

class InMemoryFeedbackPersistence[F[_]: Effect](
    userPersistence: InMemoryUserPersistence[F],
    initialFeedback: List[Feedback] = List.empty)
    extends FeedbackPersistence[F] {
  private val feedback          = new ArrayBuffer[Feedback] ++ initialFeedback
  private val feedbackIdCounter = new AtomicInteger(1)

  override def create(title: String, content: String, sourceUser: Int, targetUser: Int): F[Unit] =
    for {
      sourceUser <- userPersistence.get(sourceUser).flatMap {
        case Some(user) => Effect[F].pure(user)
        case None       => Effect[F].raiseError[User](SourceUserDoesNotExist(sourceUser))
      }
      targetUser <- userPersistence.get(targetUser).flatMap {
        case Some(user) => Effect[F].pure(user)
        case None       => Effect[F].raiseError[User](TargetUserDoesNotExist(targetUser))
      }
      _ <- Effect[F].delay(feedback += Feedback(feedbackIdCounter.getAndIncrement(), title, content, sourceUser, targetUser))
    } yield ()

  override def listBySourceUser(id: Int): F[List[Feedback]] = Effect[F].pure(feedback.filter(_.sourceUser.id == id).toList)

  override def listByTargetUser(id: Int): F[List[Feedback]] = Effect[F].pure(feedback.filter(_.targetUser.id == id).toList)

  override def get(sourceUserId: Int, feedbackId: Int): F[Option[Feedback]] =
    Effect[F].pure(feedback.find(f => f.id == feedbackId && f.sourceUser.id == sourceUserId))
}
