package $package;format="lower,package"$.$name;format="lower,word"$.logging

import cats.Show
import cats.effect.Effect
import cats.syntax.show.toShow
import org.slf4j
import org.slf4j.LoggerFactory

trait Logger {
  def error[F[_]: Effect, A: Show](err: Throwable, a: A): F[Unit]

  def debug[F[_]: Effect, A: Show](msg: A): F[Unit]

  def warn[F[_]: Effect, A: Show](msg: A): F[Unit]

  def info[F[_]: Effect, A: Show](msg: A): F[Unit]
}

class Sl4jLogger(name: String) extends Logger {
  private val logger: slf4j.Logger = LoggerFactory.getLogger(name)

  override def error[F[_]: Effect, A: Show](err: Throwable, a: A): F[Unit] =
    Effect[F].delay(logger.error(a.show, err))

  override def debug[F[_]: Effect, A: Show](a: A): F[Unit] =
    Effect[F].delay(logger.debug(a.show))

  override def warn[F[_]: Effect, A: Show](a: A): F[Unit] =
    Effect[F].delay(logger.warn(a.show))

  override def info[F[_]: Effect, A: Show](a: A): F[Unit] =
    Effect[F].delay(logger.info(a.show))
}
