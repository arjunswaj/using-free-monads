package com.asb.free.interpreter

import java.net.InetAddress

import cats.{Id, ~>}
import com.asb.free.dsl.NetworkDSL.{DoGet, GetInetAddress, NetworkAction}
import org.apache.http.client.fluent.Request

object NetworkInterpreter extends (NetworkAction ~> Id) {
  val CONNECT_TIMEOUT = 1000
  val SOCKET_TIMEOUT = 1000

  override def apply[A](fa: NetworkAction[A]): Id[A] = fa match {
    case GetInetAddress(ipAddress) =>
      InetAddress.getByName(ipAddress)

    case DoGet(uri) =>
      Request.Get(uri)
      .connectTimeout(CONNECT_TIMEOUT)
      .socketTimeout(SOCKET_TIMEOUT)
      .execute()
      .returnContent()
      .asString()
  }

}
