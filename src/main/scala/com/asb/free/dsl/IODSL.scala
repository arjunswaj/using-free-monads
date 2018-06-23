package com.asb.free.dsl

import java.io.{BufferedReader, Closeable}
import java.nio.file.Path

import cats.InjectK
import cats.free.Free

object IODSL {

  sealed trait IOAction[A]

  case class GetBufferedReader(path: Path) extends IOAction[BufferedReader]
  case class ReadLine(bufferedReader: BufferedReader) extends IOAction[String]
  case class Close(closeable: Closeable) extends IOAction[Unit]

  class IOActions[F[_]](implicit I: InjectK[IOAction, F]) {

    type IOActionF[A] = Free[F, A]

    def getBufferedReader(path: Path): IOActionF[BufferedReader] =
      Free.inject(GetBufferedReader(path))

    def readLine(bufferedReader: BufferedReader): IOActionF[String] =
      Free.inject(ReadLine(bufferedReader))

    def close(closeable: Closeable): IOActionF[Unit] =
      Free.inject(Close(closeable))

  }

  object IOActions {
    def apply[F[_]](implicit I: InjectK[IOAction, F]): IOActions[F] = new IOActions[F]
  }

}
