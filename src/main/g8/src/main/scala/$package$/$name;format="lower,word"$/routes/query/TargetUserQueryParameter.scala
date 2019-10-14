package $package;format="lower,package"$.$name;format="lower,word"$.routes.query

import org.http4s.dsl.impl.QueryParamDecoderMatcher

object TargetUserQueryParameter extends QueryParamDecoderMatcher[Int]("target_user")
