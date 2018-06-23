package com.asb.free.interpreter

import java.net.InetAddress
import java.nio.file.Paths

import cats.{Id, ~>}
import com.asb.free.dsl.GeoModelDSL.{GeoModelAction, GetCityResponse}
import com.maxmind.geoip2.DatabaseReader

object GeoModelInterpreter extends (GeoModelAction ~> Id) {

  override def apply[A](fa: GeoModelAction[A]): Id[A] = fa match {
    case GetCityResponse(ipAddress) =>
      val database = Paths.get("resources", "GeoLite2-City.mmdb").toFile
      val dbreader = new DatabaseReader.Builder(database).build
      try {
        dbreader.city(InetAddress.getByName(ipAddress))
      } finally {
        dbreader.close()
      }
  }

}
