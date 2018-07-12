package com.asb.free.dsl

import cats.InjectK
import cats.free.Free
import cats.free.Free.inject

object WeatherServiceDSL {

  sealed trait WeatherServiceAction[A]

  case class GetKey() extends WeatherServiceAction[String]

  class WeatherServiceActions[F[_]](implicit I: InjectK[WeatherServiceAction, F]) {

    type WeatherServiceActionsF[A] = Free[F, A]

    def getKey: WeatherServiceActionsF[String] =
      inject(GetKey())
  }

  object WeatherServiceActions {
    def apply[F[_]](implicit I: InjectK[WeatherServiceAction, F]): WeatherServiceActions[F] = new WeatherServiceActions[F]
  }

}
