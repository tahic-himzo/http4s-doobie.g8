package $package;format="lower,package"$.$name;format="lower,word"$.persistence.sql

import cats.effect._
import $package;format="lower,package"$.$name;format="lower,word"$.config.DatabaseConfig
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

object HikariTransactorFactory {

  def apply[F[_]: Effect: ContextShift](databaseConfig: DatabaseConfig): Resource[F, HikariTransactor[F]] =
    for {
      _             <- Resource.liftF(Effect[F].delay(Class.forName(databaseConfig.driverClassName)))
      connectionEc  <- ExecutionContexts.fixedThreadPool[F](databaseConfig.connectionPoolSize)
      transactionEc <- ExecutionContexts.cachedThreadPool[F]
      transactor    <- HikariTransactor.initial[F](connectionEc, Blocker.liftExecutionContext(transactionEc))
      _ <- Resource.liftF {
        transactor.configure { ds =>
          Effect[F].delay {
            ds.setJdbcUrl(JdbcUrl.apply(databaseConfig.host, databaseConfig.databaseName, databaseConfig.serverTimezone))
            ds.setUsername(databaseConfig.user)
            ds.setPassword(databaseConfig.password)
            ds.setMaximumPoolSize(databaseConfig.connectionPoolSize)
          }
        }
      }
    } yield transactor
}
