package com.asb.free.program

import com.asb.free.dsl.FileDSL.FileActions
import com.asb.free.dsl.IODSL.IOActions
import com.asb.free.dsl.NetworkDSL.NetworkActions
import com.asb.free.dsl.WeatherRequestUtilsDSL.WeatherRequestActions
import com.asb.free.dsl.WeatherResponseUtilsDSL.WeatherResponseActions
import com.asb.free.interpreter.WeatherServiceInterpreters.{P, PF}
import com.maxmind.geoip2.model.CityResponse

object WeatherServiceProgram {

  def getTemperature(cityResponse: CityResponse, key: String)
                    (implicit WRQ: WeatherRequestActions[P], N: NetworkActions[P],
                     WRS: WeatherResponseActions[P]): PF[Double] = for {
    uri <- WRQ.getUrl(cityResponse.getLocation.getLatitude,
      cityResponse.getLocation.getLongitude, key)
    response <- N.doGet(uri)
    temperature <- WRS.getTemperature(response)
  } yield temperature


  def getKey(keyPath: String)(implicit F: FileActions[P], I: IOActions[P]): PF[String] = for {
    filePath <- F.getFilePath(keyPath)
    reader <- I.getBufferedReader(filePath)
    key <- I.readLine(reader)
    _ <- I.close(reader)
  } yield key

}
