package $package;format="lower,package"$.$name;format="lower,word"$.config

import cats.effect.IO
import cats.instances.list._
import cats.syntax.traverse._
import $package;format="lower,package"$.$name;format="lower,word"$.config.core.ConfigEnvironment
import org.scalatest.{Matchers, WordSpec}

class ConfigSpec extends WordSpec with Matchers {

  "The application config" should {
    "succeed to load for all environments" in {
      val configs =
        ConfigEnvironment.all.traverse(env => AppConfig.load[IO](env, Map.empty)).attempt.unsafeRunSync()
      configs shouldBe a[Right[_, _]]
    }
  }
}
