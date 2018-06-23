package com.asb.free.dsl

import cats.InjectK
import cats.free.Free

object WeatherResponseUtilsDSL {

  sealed trait WeatherResponseAction[A]

  case class GetTemperature(response: String) extends WeatherResponseAction[Double]

  class WeatherResponseActions[F[_]](implicit I: InjectK[WeatherResponseAction, F]) {

    type WeatherResponseActionF[A] = Free[F, A]

    def getTemperature(response: String): WeatherResponseActionF[Double] =
      Free.inject(GetTemperature(response))

  }

  object WeatherResponseActions {
    def apply[F[_]](implicit I: InjectK[WeatherResponseAction, F]): WeatherResponseActions[F] = new WeatherResponseActions[F]
  }

}
