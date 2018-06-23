package com.asb.free.dsl

import java.net.URI

import cats.InjectK
import cats.free.Free

object WeatherRequestUtilsDSL {

  sealed trait WeatherRequestAction[A]

  case class GetURL(latitude: Double, longitude: Double, key: String) extends WeatherRequestAction[URI]

  class WeatherRequestActions[F[_]](implicit I: InjectK[WeatherRequestAction, F]) {

    type WeatherRequestActionF[A] = Free[F, A]

    def getUrl(latitude: Double, longitude: Double, key: String): WeatherRequestActionF[URI] =
      Free.inject(GetURL(latitude, longitude, key))

  }

  object WeatherRequestActions {
    def apply[F[_]](implicit I: InjectK[WeatherRequestAction, F]): WeatherRequestActions[F] = new WeatherRequestActions[F]
  }

}
