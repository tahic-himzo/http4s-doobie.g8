package $package;format="lower,package"$.$name;format="lower,word"$.persistence.sql

import cats.effect.Effect
import cats.syntax.functor._
import $package;format="lower,package"$.$name;format="lower,word"$.model.User
import $package;format="lower,package"$.$name;format="lower,word"$.persistence.UserPersistence
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.syntax.connectionio.toConnectionIOOps

class DoobieUserPersistence[F[_]: Effect](xa: HikariTransactor[F]) extends UserPersistence[F] {
  override def get(id: Int): F[Option[User]] = DoobieUserQueries.get(id).option.transact(xa)

  override def create(name: String): F[Unit] = DoobieUserQueries.create(name).run.transact(xa).as(())

  override def list: F[List[User]] = DoobieUserQueries.list.to[List].transact(xa)
}

object DoobieUserQueries {

  def list: doobie.Query0[User] =
    sql"select id, name from users".query[User]

  def get(id: Int): doobie.Query0[User] =
    sql"select id, name from users where id = \$id".query[User]

  def create(name: String): doobie.Update0 =
    sql"INSERT INTO users(name) VALUES(\${name})".update
}
