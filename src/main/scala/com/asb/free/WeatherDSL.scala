package com.asb.free

import cats.free.Free
import com.maxmind.geoip2.model.CityResponse

object WeatherDSL {

  sealed trait WeatherAction[A]

  case class GetIp() extends WeatherAction[String]
  case class GetCityResponse(ipAddress: String) extends WeatherAction[CityResponse]
  case class GetWeather(cityResponse: CityResponse) extends WeatherAction[String]
  case class GetTemperature(weather: String) extends WeatherAction[Double]

  def getIp: Free[WeatherAction, String] =
    Free.liftF(GetIp())

  def getCityResponse(ipAddress: String): Free[WeatherAction, CityResponse] =
    Free.liftF(GetCityResponse(ipAddress))

  def getWeather(cityResponse: CityResponse): Free[WeatherAction, String] =
    Free.liftF(GetWeather(cityResponse))

  def getTemperature(weather: String): Free[WeatherAction, Double] =
    Free.liftF(GetTemperature(weather))

}
