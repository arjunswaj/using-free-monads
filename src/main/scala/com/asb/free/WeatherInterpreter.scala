package com.asb.free

import cats.~>
import com.asb.free.WeatherDSL._
import com.asb.impure
import com.asb.impure.GetWeather._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object WeatherInterpreter extends (WeatherAction ~> Future) {
  override def apply[A](fa: WeatherAction[A]): Future[A] = fa match {
    case GetIp() => Future.successful(getIP)
    case GetCityResponse(ipAddress) => Future.successful(impure.GetWeather.getCityResponse(ipAddress))
    case GetWeather(cityResponse) => Future(impure.GetWeather.getWeather(cityResponse))
    case GetTemperature(weather) => Future.successful(impure.GetWeather.getTemperature(weather))
  }
}
