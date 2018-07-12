package com.asb.free

import cats.data.EitherK
import cats.free.Free
import cats.{Id, ~>}
import com.asb.free.dsl.ConsoleDSL.{ConsoleAction, ConsoleActions}
import com.asb.free.dsl.GeoModelDSL.{GeoModelAction, GeoModelActions}
import com.asb.free.dsl.NetworkDSL.{NetworkAction, NetworkActions}
import com.asb.free.dsl.WeatherRequestUtilsDSL.{WeatherRequestAction, WeatherRequestActions}
import com.asb.free.dsl.WeatherResponseUtilsDSL.{WeatherResponseAction, WeatherResponseActions}
import com.asb.free.dsl.WeatherServiceDSL.{WeatherServiceAction, WeatherServiceActions}
import com.asb.free.interpreter._

object WeatherProgram {

  type P1[A] = EitherK[ConsoleAction, WeatherServiceAction, A]
  type P2[A] = EitherK[NetworkAction, P1, A]
  type P3[A] = EitherK[GeoModelAction, P2, A]
  type P4[A] = EitherK[WeatherRequestAction, P3, A]
  type P[A] = EitherK[WeatherResponseAction, P4, A]

  def getTemperatureByIp(implicit C: ConsoleActions[P], W: WeatherServiceActions[P],
                         N: NetworkActions[P], G: GeoModelActions[P], WRQ: WeatherRequestActions[P],
                         WRS: WeatherResponseActions[P]): Free[P, Double] = for {
    ip <- C.readLine
    cityResponse <- G.getCityResponse(ip)
    key <- W.getKey
    uri <- WRQ.getUrl(cityResponse.getLocation.getLatitude,
      cityResponse.getLocation.getLongitude, key)
    response <- N.doGet(uri)
    temperature <- WRS.getTemperature(response)
  } yield temperature

  def main(args: Array[String]): Unit = {
    implicit val C: ConsoleActions[P] = ConsoleActions[P]
    implicit val W: WeatherServiceActions[P] = WeatherServiceActions[P]
    implicit val N: NetworkActions[P] = NetworkActions[P]
    implicit val G: GeoModelActions[P] = GeoModelActions[P]
    implicit val WRQ: WeatherRequestActions[P] = WeatherRequestActions[P]
    implicit val WRS: WeatherResponseActions[P] = WeatherResponseActions[P]

    val i1: P1 ~> Id = ConsoleInterpreter or WeatherServiceInterpreters.interpreter
    val i2: P2 ~> Id = NetworkInterpreter or i1
    val i3: P3 ~> Id = GeoModelInterpreter or i2
    val i4: P4 ~> Id = WeatherRequestInterpreter or i3
    val interpreter: P ~> Id = WeatherResponseInterpreter or i4

    val temperature = getTemperatureByIp.foldMap(interpreter)
    println(temperature)
  }

}
