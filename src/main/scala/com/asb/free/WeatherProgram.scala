package com.asb.free

import cats.free.Free
import cats.implicits._
import com.asb.free.WeatherDSL._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object WeatherProgram {

  def getTemperatureByIp: Free[WeatherAction, Double] = for {
    ip <- getIp
    cityResponse <- getCityResponse(ip)
    weather <- getWeather(cityResponse)
    temperature <- getTemperature(weather)
  } yield temperature

  def main(args: Array[String]): Unit = {
    val temperatureFuture = getTemperatureByIp.foldMap(WeatherInterpreter)
    val temperature = Await.result(temperatureFuture, 5.seconds)
    println(temperature)
  }

}
