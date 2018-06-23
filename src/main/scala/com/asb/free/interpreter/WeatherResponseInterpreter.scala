package com.asb.free.interpreter

import cats.{Id, ~>}
import com.asb.free.dsl.WeatherResponseUtilsDSL.{GetTemperature, WeatherResponseAction}
import org.json.JSONObject

object WeatherResponseInterpreter extends (WeatherResponseAction ~> Id) {

  override def apply[A](fa: WeatherResponseAction[A]): Id[A] = fa match {
    case GetTemperature(response) =>
      Option.apply(response)
        .map(new JSONObject(_))
        .map(_.getJSONObject("main"))
        .map(_.getDouble("temp"))
        .getOrElse(0.0d)
  }

}
