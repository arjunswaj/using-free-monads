package com.asb.free.interpreter

import cats.data.EitherK
import cats.free.Free
import cats.{Id, ~>}
import com.asb.free.dsl.FileDSL.{FileAction, FileActions}
import com.asb.free.dsl.IODSL.{IOAction, IOActions}
import com.asb.free.dsl.NetworkDSL.{NetworkAction, NetworkActions}
import com.asb.free.dsl.WeatherRequestUtilsDSL.{WeatherRequestAction, WeatherRequestActions}
import com.asb.free.dsl.WeatherResponseUtilsDSL.{WeatherResponseAction, WeatherResponseActions}
import com.asb.free.dsl.WeatherServiceDSL.{GetKey, GetTemperature, WeatherServiceAction}
import com.asb.free.program.WeatherServiceProgram

object WeatherServiceInterpreters {

  type P1[A] = EitherK[FileAction, IOAction, A]
  type P2[A] = EitherK[WeatherRequestAction, P1, A]
  type P3[A] = EitherK[NetworkAction, P2, A]
  type P[A] = EitherK[WeatherResponseAction, P3, A]
  type PF[A] = Free[P, A]

  val interpreter: (WeatherServiceAction ~> Id) = IntermediateInterpreter andThen PrimitiveInterpreter

  object IntermediateInterpreter extends (WeatherServiceAction ~> PF) {
    implicit val F: FileActions[P] = FileActions[P]
    implicit val I: IOActions[P] = IOActions[P]
    implicit val N: NetworkActions[P] = NetworkActions[P]
    implicit val WRQ: WeatherRequestActions[P] = WeatherRequestActions[P]
    implicit val WRS: WeatherResponseActions[P] = WeatherResponseActions[P]

    override def apply[A](fa: WeatherServiceAction[A]): PF[A] = fa match {
      case GetKey() => WeatherServiceProgram.getKey("resources/weather.key")
      case GetTemperature(cityResponse, key) => WeatherServiceProgram.getTemperature(cityResponse, key)
    }
  }

  object PrimitiveInterpreter extends (PF ~> Id) {
    val i1: P1 ~> Id = FileInterpreter or IOInterpreter
    val i2: P2 ~> Id = WeatherRequestInterpreter or i1
    val i3: P3 ~> Id = NetworkInterpreter or i2
    val interpreter: P ~> Id = WeatherResponseInterpreter or i3

    override def apply[A](fa: PF[A]): Id[A] = fa.foldMap(interpreter)
  }

}
