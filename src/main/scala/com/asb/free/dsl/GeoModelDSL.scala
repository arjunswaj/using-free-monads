package com.asb.free.dsl

import java.io.File
import java.net.InetAddress

import cats.InjectK
import cats.free.Free
import cats.free.Free.inject
import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.model.CityResponse

object GeoModelDSL {

  sealed trait GeoModelAction[A]

  case class GetCityResponse(ipAddress: String) extends GeoModelAction[CityResponse]
  case class GetCityResponseForInetAddress(inetAddress: InetAddress, databaseReader: DatabaseReader) extends GeoModelAction[CityResponse]
  case class GetDatabaseReader(database: File) extends GeoModelAction[DatabaseReader]

  class GeoModelActions[F[_]](implicit I: InjectK[GeoModelAction, F]) {

    type GeoModelActionF[A] = Free[F, A]

    def getCityResponse(ipAddress: String): GeoModelActionF[CityResponse] =
      inject(GetCityResponse(ipAddress))

    def getCityResponseForInetAddress(inetAddress: InetAddress, databaseReader: DatabaseReader): GeoModelActionF[CityResponse] =
      inject(GetCityResponseForInetAddress(inetAddress, databaseReader))

    def getDatabaseReader(database: File): GeoModelActionF[DatabaseReader] =
      inject(GetDatabaseReader(database))
  }

  object GeoModelActions {
    def apply[F[_]](implicit I: InjectK[GeoModelAction, F]): GeoModelActions[F] = new GeoModelActions[F]
  }

}
