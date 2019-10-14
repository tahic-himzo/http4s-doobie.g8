package $package;format="lower,package"$.$name;format="lower,word"$.persistence.sql

import cats.effect.Effect
import cats.syntax.functor._
import $package;format="lower,package"$.$name;format="lower,word"$.model.{Feedback, User}
import $package;format="lower,package"$.$name;format="lower,word"$.persistence.FeedbackPersistence
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.fragment.Fragment

class DoobieFeedbackPersistence[F[_]: Effect](xa: HikariTransactor[F]) extends FeedbackPersistence[F] {
  override def listBySourceUser(id: Int): F[List[Feedback]] = FeedbackQueries.listBySourceUser(id).to[List].transact(xa)

  override def listByTargetUser(id: Int): F[List[Feedback]] = FeedbackQueries.listByTargetUser(id).to[List].transact(xa)

  override def get(sourceUserId: Int, feedbackId: Int): F[Option[Feedback]] =
    FeedbackQueries.get(sourceUserId, feedbackId).option.transact(xa)

  override def create(title: String, content: String, sourceUser: Int, targetUser: Int): F[Unit] =
    FeedbackQueries.create(title, content, sourceUser, targetUser).run.transact(xa).as(())
}

object FeedbackQueries {

  case class FeedbackRow(id: Int, title: String, content: String)

  def selectFullFeedback(where: Option[Fragment]): Fragment = {
    val selectFrom = fr"""
          SELECT
            f.id, f.title, f.content, u1.id, u1.name, u2.id, u2.name
          FROM  
            feedback AS f INNER JOIN
              users AS u1 ON f.source_user = u1.id INNER JOIN
              users AS u2 ON f.target_user = u2.id"""
    where.map(selectFrom ++ _).getOrElse(selectFrom)
  }

  def listBySourceUser(id: Int): doobie.Query0[Feedback] =
    selectFullFeedback(Some(sql" WHERE f.source_user = $id"))
      .query[(FeedbackRow, User, User)]
      .map {
        case (feedbackRow, sourceUser, targetUser) =>
          Feedback(feedbackRow.id, feedbackRow.title, feedbackRow.content, sourceUser, targetUser)
      }

  def listByTargetUser(id: Int): doobie.Query0[Feedback] =
    selectFullFeedback(Some(sql" WHERE f.target_user = $id"))
      .query[(FeedbackRow, User, User)]
      .map {
        case (feedbackRow, sourceUser, targetUser) =>
          Feedback(feedbackRow.id, feedbackRow.title, feedbackRow.content, sourceUser, targetUser)
      }

  def get(sourceUserId: Int, feedbackId: Int): doobie.Query0[Feedback] =
    selectFullFeedback(Some(sql" WHERE f.source_user = $sourceUserId AND f.id = $feedbackId "))
      .query[(FeedbackRow, User, User)]
      .map {
        case (feedbackRow, sourceUser, targetUser) =>
          Feedback(feedbackRow.id, feedbackRow.title, feedbackRow.content, sourceUser, targetUser)
      }

  def create(title: String, content: String, sourceUser: Int, targetUser: Int): doobie.Update0 =
    sql"INSERT INTO feedback(title, content, source_user, target_user) VALUES($title, $content, $sourceUser, $targetUser)".update
}
