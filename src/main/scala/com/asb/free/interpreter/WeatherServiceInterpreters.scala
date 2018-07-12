package com.asb.free.interpreter

import cats.data.EitherK
import cats.free.Free
import cats.{Id, ~>}
import com.asb.free.dsl.FileDSL.{FileAction, FileActions}
import com.asb.free.dsl.IODSL.{IOAction, IOActions}
import com.asb.free.dsl.WeatherServiceDSL.{GetKey, WeatherServiceAction}
import com.asb.free.program.WeatherServiceProgram

object WeatherServiceInterpreters {

  type P[A] = EitherK[FileAction, IOAction, A]
  type PF[A] = Free[P, A]

  val interpreter: (WeatherServiceAction ~> Id) = IntermediateInterpreter andThen PrimitiveInterpreter

  object IntermediateInterpreter extends (WeatherServiceAction ~> PF) {
    implicit val F: FileActions[P] = FileActions[P]
    implicit val I: IOActions[P] = IOActions[P]

    override def apply[A](fa: WeatherServiceAction[A]): PF[A] = fa match {
      case GetKey() => WeatherServiceProgram.getKey("resources/weather.key")
    }
  }

  object PrimitiveInterpreter extends (PF ~> Id) {
    val interpreter: P ~> Id = FileInterpreter or IOInterpreter

    override def apply[A](fa: PF[A]): Id[A] = fa.foldMap(interpreter)
  }

}
