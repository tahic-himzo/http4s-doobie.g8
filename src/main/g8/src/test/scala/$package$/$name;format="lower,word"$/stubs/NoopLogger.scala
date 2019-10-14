package $package;format="lower,package"$.$name;format="lower,word"$.stubs

import cats.Show
import cats.effect.Effect
import $package;format="lower,package"$.$name;format="lower,word"$.logging.Logger

object NoopLogger extends Logger {
  override def error[F[_]: Effect, A: Show](err: Throwable, a: A): F[Unit] = Effect[F].unit

  override def debug[F[_]: Effect, A: Show](msg: A): F[Unit] = Effect[F].unit

  override def warn[F[_]: Effect, A: Show](msg: A): F[Unit] = Effect[F].unit

  override def info[F[_]: Effect, A: Show](msg: A): F[Unit] = Effect[F].unit
}
