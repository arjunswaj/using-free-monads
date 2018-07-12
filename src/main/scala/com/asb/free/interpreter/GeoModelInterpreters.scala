package com.asb.free.interpreter

import cats.data.EitherK
import cats.free.Free
import cats.{Id, ~>}
import com.asb.free.dsl.FileDSL.{FileAction, FileActions}
import com.asb.free.dsl.GeoModelDSL._
import com.asb.free.dsl.IODSL.{IOAction, IOActions}
import com.asb.free.dsl.NetworkDSL.{NetworkAction, NetworkActions}
import com.asb.free.program.GeoModelProgram
import com.maxmind.geoip2.DatabaseReader

object GeoModelInterpreters {

  type P1[A] = EitherK[FileAction, NetworkAction, A]
  type P2[A] = EitherK[IOAction, P1, A]
  type P[A] = EitherK[GeoModelAction, P2, A]

  type PF[A] = Free[P, A]

  val interpreter: GeoModelAction ~> Id = IntermediateInterpreter andThen PrimitiveInterpreter

  object IntermediateInterpreter extends (GeoModelAction ~> PF) {
    implicit val N: NetworkActions[P] = NetworkActions[P]
    implicit val F: FileActions[P] = FileActions[P]
    implicit val IO: IOActions[P] = IOActions[P]
    implicit val G: GeoModelActions[P] = GeoModelActions[P]

    override def apply[A](fa: GeoModelAction[A]): PF[A] = fa match {
      case GetCityResponse(ipAddress) => GeoModelProgram.getCityResponse(ipAddress, "resources/GeoLite2-City.mmdb")
      case _ => throw new Exception("Should not have happened")
    }
  }

  private[this] object PureInterpreter extends (GeoModelAction ~> Id) {
    override def apply[A](fa: GeoModelAction[A]): Id[A] = fa match {
      case GetCityResponseForInetAddress(inetAddress, dbReader) => dbReader.city(inetAddress)
      case GetDatabaseReader(database) => new DatabaseReader.Builder(database).build
      case _ => throw new Exception("Should not have happened")
    }
  }

  object PrimitiveInterpreter extends (PF ~> Id) {
    val i1: P1 ~> Id = FileInterpreter or NetworkInterpreter
    val i2: P2 ~> Id = IOInterpreter or i1
    val interpreter: P ~> Id = PureInterpreter or i2

    override def apply[A](fa: PF[A]): Id[A] = fa.foldMap(interpreter)
  }

}