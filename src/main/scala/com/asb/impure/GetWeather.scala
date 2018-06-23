package com.asb.impure

import java.io.{BufferedReader, InputStreamReader}
import java.net.InetAddress
import java.nio.file.{Files, Paths}

import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.model.CityResponse
import org.apache.http.client.fluent.Request
import org.apache.http.client.utils.URIBuilder
import org.json.JSONObject

object GetWeather {

  def main(args: Array[String]): Unit = {

    val weather = Option.apply(getIP)
      .map(getCityResponse)
      .map(getWeather)
      .map(getTemperature)
      .getOrElse("")

    println(weather)

  }

  def getIP: String = {
    val reader = new BufferedReader(new InputStreamReader(System.in))
    try {
      reader.readLine
    } finally {
      reader.close()
    }
  }

  def getCityResponse(ipAddress: String): CityResponse = {
    val database = Paths.get("resources", "GeoLite2-City.mmdb").toFile
    val dbreader = new DatabaseReader.Builder(database).build
    try {
      dbreader.city(InetAddress.getByName(ipAddress))
    } finally {
      dbreader.close()
    }
  }

  def getWeather(response: CityResponse): String = {
    val weatherKey = Files.readAllLines(Paths.get("resources", "weather.key")).get(0)
    val longitude = response.getLocation.getLongitude
    val latitude = response.getLocation.getLatitude
    val builder = new URIBuilder
    builder.setScheme("http")
      .setHost("api.openweathermap.org")
      .setPath("/data/2.5/weather")
      .setParameter("lat", latitude.toString)
      .setParameter("lon", longitude.toString)
      .setParameter("appid", weatherKey)

    Request.Get(builder.build)
      .connectTimeout(1000)
      .socketTimeout(1000)
      .execute().returnContent().asString()
  }

  def getTemperature(content: String): Double = {
    val json = new JSONObject(content)
    json.getJSONObject("main").getDouble("temp")
  }

}
