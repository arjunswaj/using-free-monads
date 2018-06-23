package com.asb.free.dsl

import java.net.URI

import cats.InjectK
import cats.free.Free

object NetworkDSL {

  sealed trait NetworkAction[A]

  case class DoGet(uri: URI) extends NetworkAction[String]

  class NetworkActions[F[_]](implicit I: InjectK[NetworkAction, F]) {

    type NetworkActionF[A] = Free[NetworkAction, A]

    def doGet(uri: URI): NetworkActionF[String] =
      Free.inject(DoGet(uri))

  }

  object NetworkActions {
    def apply[F[_]](implicit I: InjectK[NetworkAction, F]): NetworkActions[F] = new NetworkActions[F]
  }

}
