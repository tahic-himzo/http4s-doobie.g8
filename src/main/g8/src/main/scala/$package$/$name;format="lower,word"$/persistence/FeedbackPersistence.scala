package $package;format="lower,package"$.$name;format="lower,word"$.persistence

import $package;format="lower,package"$.$name;format="lower,word"$.model.Feedback

trait FeedbackPersistence[F[_]] {
  def listBySourceUser(sourceUser: Int): F[List[Feedback]]
  def listByTargetUser(targetUser: Int): F[List[Feedback]]
  def get(sourceUserId:            Int, feedbackId: Int): F[Option[Feedback]]
  def create(title:                String, content: String, sourceUser: Int, targetUser: Int): F[Unit]
}
