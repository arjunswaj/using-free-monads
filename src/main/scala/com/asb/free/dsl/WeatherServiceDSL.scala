package com.asb.free.dsl

import cats.InjectK
import cats.free.Free
import cats.free.Free.inject
import com.maxmind.geoip2.model.CityResponse

object WeatherServiceDSL {

  sealed trait WeatherServiceAction[A]

  case class GetKey() extends WeatherServiceAction[String]

  case class GetTemperature(cityResponse: CityResponse, key: String) extends WeatherServiceAction[Double]

  class WeatherServiceActions[F[_]](implicit I: InjectK[WeatherServiceAction, F]) {

    type WeatherServiceActionsF[A] = Free[F, A]

    def getKey: WeatherServiceActionsF[String] =
      inject(GetKey())

    def getTemperature(cityResponse: CityResponse, key: String): WeatherServiceActionsF[Double] =
      inject(GetTemperature(cityResponse, key))

    def getTemperature(cityResponse: CityResponse): WeatherServiceActionsF[Double] = for {
      key <- getKey
      temperature <- getTemperature(cityResponse, key)
    } yield temperature

  }

  object WeatherServiceActions {
    def apply[F[_]](implicit I: InjectK[WeatherServiceAction, F]): WeatherServiceActions[F] = new WeatherServiceActions[F]
  }

}
