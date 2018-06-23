package com.asb.free.interpreter

import cats.{Id, ~>}
import com.asb.free.dsl.WeatherRequestUtilsDSL.{GetURL, WeatherRequestAction}
import org.apache.http.client.utils.URIBuilder

object WeatherRequestInterpreter extends (WeatherRequestAction ~> Id) {

  override def apply[A](fa: WeatherRequestAction[A]): Id[A] = fa match {
    case GetURL(latitude, longitude, key) =>
      val builder = new URIBuilder
      builder.setScheme("http")
        .setHost("api.openweathermap.org")
        .setPath("/data/2.5/weather")
        .setParameter("lat", latitude.toString)
        .setParameter("lon", longitude.toString)
        .setParameter("appid", key)
      builder.build

  }

}
