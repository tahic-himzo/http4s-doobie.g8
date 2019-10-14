package $package;format="lower,package"$.$name;format="lower,word"$.persistence

import $package;format="lower,package"$.$name;format="lower,word"$.model.User

trait UserPersistence[F[_]] {
  def get(id: Int): F[Option[User]]

  def create(name: String): F[Unit]

  def list: F[List[User]]
}
