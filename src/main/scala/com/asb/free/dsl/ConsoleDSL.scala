package com.asb.free.dsl

import cats.InjectK
import cats.free.Free

object ConsoleDSL {

  sealed trait ConsoleAction[A]

  case class ReadLine() extends ConsoleAction[String]
  case class Write(string: String) extends ConsoleAction[Unit]

  class ConsoleActions[F[_]](implicit I: InjectK[ConsoleAction, F]) {
    type ConsoleActionF[A] = Free[F, A]

    def readLine: ConsoleActionF[String] =
      Free.inject(ReadLine())

    def write(string: String): ConsoleActionF[Unit] =
      Free.inject(Write(string))
  }

  object ConsoleActions {
    def apply[F[_]](implicit I: InjectK[ConsoleAction, F]): ConsoleActions[F] = new ConsoleActions[F]
  }

}
