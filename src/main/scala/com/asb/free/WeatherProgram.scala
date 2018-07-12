package com.asb.free

import cats.data.EitherK
import cats.free.Free
import cats.{Id, ~>}
import com.asb.free.dsl.ConsoleDSL.{ConsoleAction, ConsoleActions}
import com.asb.free.dsl.GeoModelDSL.{GeoModelAction, GeoModelActions}
import com.asb.free.dsl.WeatherServiceDSL.{WeatherServiceAction, WeatherServiceActions}
import com.asb.free.interpreter._

object WeatherProgram {

  type P1[A] = EitherK[ConsoleAction, WeatherServiceAction, A]
  type P[A] = EitherK[GeoModelAction, P1, A]

  def getTemperatureByIp(implicit C: ConsoleActions[P], W: WeatherServiceActions[P], G: GeoModelActions[P]): Free[P, Double] = for {
    ip <- C.readLine
    cityResponse <- G.getCityResponse(ip)
    temperature <- W.getTemperature(cityResponse)
  } yield temperature

  def main(args: Array[String]): Unit = {
    implicit val C: ConsoleActions[P] = ConsoleActions[P]
    implicit val W: WeatherServiceActions[P] = WeatherServiceActions[P]
    implicit val G: GeoModelActions[P] = GeoModelActions[P]

    val i1: P1 ~> Id = ConsoleInterpreter or WeatherServiceInterpreters.interpreter
    val interpreter: P ~> Id = GeoModelInterpreter or i1

    val temperature = getTemperatureByIp.foldMap(interpreter)
    println(temperature)
  }

}
