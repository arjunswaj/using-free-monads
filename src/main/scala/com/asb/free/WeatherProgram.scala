package com.asb.free

import cats.data.EitherK
import cats.free.Free
import cats.implicits._
import cats.{Id, ~>}
import com.asb.free.dsl.ConsoleDSL.{ConsoleAction, ConsoleActions}
import com.asb.free.dsl.FileDSL.{FileAction, FileActions}
import com.asb.free.dsl.GeoModelDSL.{GeoModelAction, GeoModelActions}
import com.asb.free.dsl.IODSL.{IOAction, IOActions}
import com.asb.free.dsl.NetworkDSL.{NetworkAction, NetworkActions}
import com.asb.free.dsl.WeatherRequestUtilsDSL.{WeatherRequestAction, WeatherRequestActions}
import com.asb.free.dsl.WeatherResponseUtilsDSL.{WeatherResponseAction, WeatherResponseActions}
import com.asb.free.interpreter._

object WeatherProgram {

  type P1[A] = EitherK[ConsoleAction, FileAction, A]
  type P2[A] = EitherK[IOAction, P1, A]
  type P3[A] = EitherK[NetworkAction, P2, A]
  type P4[A] = EitherK[GeoModelAction, P3, A]
  type P5[A] = EitherK[WeatherRequestAction, P4, A]
  type P[A] = EitherK[WeatherResponseAction, P5, A]

  def getTemperatureByIp(implicit C: ConsoleActions[P], F: FileActions[P], I: IOActions[P],
                         N: NetworkActions[P], G: GeoModelActions[P], WRQ: WeatherRequestActions[P],
                         WRS: WeatherResponseActions[P]): Free[P, Double] = for {
    ip <- C.readLine
    cityResponse <- G.getCityResponse(ip)
    filePath <- F.getFilePath("resources/weather.key")
    reader <- I.getBufferedReader(filePath)
    key <- I.readLine(reader)
    _ <- I.close(reader)
    uri <- WRQ.getUrl(cityResponse.getLocation.getLatitude,
      cityResponse.getLocation.getLongitude, key)
    response <- N.doGet(uri)
    temperature <- WRS.getTemperature(response)
  } yield temperature

  def main(args: Array[String]): Unit = {
    implicit val C: ConsoleActions[P] = ConsoleActions[P]
    implicit val F: FileActions[P] = FileActions[P]
    implicit val I: IOActions[P] = IOActions[P]
    implicit val N: NetworkActions[P] = NetworkActions[P]
    implicit val G: GeoModelActions[P] = GeoModelActions[P]
    implicit val WRQ: WeatherRequestActions[P] = WeatherRequestActions[P]
    implicit val WRS: WeatherResponseActions[P] = WeatherResponseActions[P]

    val i1: P1 ~> Id = ConsoleInterpreter or FileInterpreter
    val i2: P2 ~> Id = IOInterpreter or i1
    val i3: P3 ~> Id = NetworkInterpreter or i2
    val i4: P4 ~> Id = GeoModelInterpreter or i3
    val i5: P5 ~> Id = WeatherRequestInterpreter or i4
    val interpreter: P ~> Id = WeatherResponseInterpreter or i5

    val temperature = getTemperatureByIp.foldMap(interpreter)
    println(temperature)
  }

}
