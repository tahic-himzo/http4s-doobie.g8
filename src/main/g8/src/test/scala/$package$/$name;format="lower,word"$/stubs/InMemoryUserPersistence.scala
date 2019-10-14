package $package;format="lower,package"$.$name;format="lower,word"$.stubs

import java.util.concurrent.atomic.AtomicInteger

import cats.effect.Effect
import $package;format="lower,package"$.$name;format="lower,word"$.model.User
import $package;format="lower,package"$.$name;format="lower,word"$.persistence.UserPersistence

import scala.collection.mutable.ArrayBuffer

class InMemoryUserPersistence[F[_]: Effect](initialUsers: List[User] = List.empty) extends UserPersistence[F] {
  private val users         = new ArrayBuffer[User] ++ initialUsers
  private val userIdCounter = new AtomicInteger(1)

  override def create(name: String): F[Unit] = Effect[F].delay {
    users += User(userIdCounter.getAndIncrement(), name)
    ()
  }

  override def list: F[List[User]] = Effect[F].delay(users.toList)

  override def get(id: Int): F[Option[User]] = Effect[F].pure(users.find(_.id == id))
}
