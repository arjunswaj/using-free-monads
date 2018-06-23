package com.asb.free

import cats.free.Free
import com.asb.free.WeatherDSL._

object WeatherProgram {

  def getTemperatureByIp: Free[WeatherAction, Double] = for {
    ip <- getIp
    cityResponse <- getCityResponse(ip)
    weather <- getWeather(cityResponse)
    temperature <- getTemperature(weather)
  } yield temperature

  def main(args: Array[String]): Unit = {
    val temperature = getTemperatureByIp
    println(temperature)
  }

}
