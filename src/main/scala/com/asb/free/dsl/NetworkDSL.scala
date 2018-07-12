package com.asb.free.dsl

import java.net.{InetAddress, URI}

import cats.InjectK
import cats.free.Free
import cats.free.Free.inject

object NetworkDSL {

  sealed trait NetworkAction[A]

  case class GetInetAddress(ipAddress: String) extends NetworkAction[InetAddress]
  case class DoGet(uri: URI) extends NetworkAction[String]

  class NetworkActions[F[_]](implicit I: InjectK[NetworkAction, F]) {

    type NetworkActionF[A] = Free[F, A]

    def getInetAddress(ipAddress: String): NetworkActionF[InetAddress] =
      inject(GetInetAddress(ipAddress))

    def doGet(uri: URI): NetworkActionF[String] =
      inject(DoGet(uri))

  }

  object NetworkActions {
    def apply[F[_]](implicit I: InjectK[NetworkAction, F]): NetworkActions[F] = new NetworkActions[F]
  }

}
