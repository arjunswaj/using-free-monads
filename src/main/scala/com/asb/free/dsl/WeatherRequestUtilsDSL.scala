package com.asb.free.dsl

import java.io.BufferedReader
import java.net.URI

import cats.InjectK
import cats.free.Free

object WeatherRequestUtilsDSL {

  sealed trait WeatherRequestAction[A]

  case class GetKey(bufferedReader: BufferedReader) extends WeatherRequestAction[String]
  case class GetURL(latitude: Double, longitude: Double, key: String) extends WeatherRequestAction[URI]

  class WeatherRequestActions[F[_]](implicit I: InjectK[WeatherRequestAction, F]) {

    type WeatherRequestActionF[A] = Free[F, A]

    def getKey(bufferedReader: BufferedReader): WeatherRequestActionF[String] =
      Free.inject(GetKey(bufferedReader))

    def getUrl(latitude: Double, longitude: Double, key: String): WeatherRequestActionF[URI] =
      Free.inject(GetURL(latitude, longitude, key))

  }

  object WeatherRequestActions {
    def apply[F[_]](implicit I: InjectK[WeatherRequestAction, F]): WeatherRequestActions[F] = new WeatherRequestActions[F]
  }

}
