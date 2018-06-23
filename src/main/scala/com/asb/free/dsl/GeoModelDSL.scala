package com.asb.free.dsl

import cats.InjectK
import cats.free.Free
import com.maxmind.geoip2.model.CityResponse

object GeoModelDSL {

  sealed trait GeoModelAction[A]

  case class GetCityResponse(ipAddress: String) extends GeoModelAction[CityResponse]

  class GeoModelActions[F[_]](implicit I: InjectK[GeoModelAction, F]) {

    type GeoModelActionF[A] = Free[F, A]

    def getCityResponse(ipAddress: String): GeoModelActionF[CityResponse] =
      Free.inject(GetCityResponse(ipAddress))

  }

  object GeoModelActions {
    def apply[F[_]](implicit I: InjectK[GeoModelAction, F]): GeoModelActions[F] = new GeoModelActions[F]
  }

}
