package com.asb.impure

import java.io.{BufferedReader, InputStreamReader}
import java.net.InetAddress
import java.nio.file.{Files, Paths}

import com.maxmind.geoip2.DatabaseReader
import org.apache.http.client.fluent.Request
import org.apache.http.client.utils.URIBuilder
import org.json.JSONObject

object GetWeather {

  def main(args: Array[String]): Unit = {
    val reader = new BufferedReader(new InputStreamReader(System.in))
    try {
      val ipAddressStr = reader.readLine


      val database = Paths.get("resources", "GeoLite2-City.mmdb").toFile
      val weatherKey = Files.readAllLines(Paths.get("resources", "weather.key")).get(0)

      // This creates the DatabaseReader object. To improve performance, reuse
      // the object across lookups. The object is thread-safe.
      val dbreader = new DatabaseReader.Builder(database).build
      try {
        val ipAddress = InetAddress.getByName(ipAddressStr)

        // Replace "city" with the appropriate method for your database, e.g.,
        // "country".
        val response = dbreader.city(ipAddress)
        val longitude = response.getLocation.getLongitude
        val latitude = response.getLocation.getLatitude

        println(s"Latitude: $latitude, Longitude: $longitude")


        val builder = new URIBuilder
        builder.setScheme("http")
          .setHost("api.openweathermap.org")
          .setPath("/data/2.5/weather")
          .setParameter("lat", latitude.toString)
          .setParameter("lon", longitude.toString)
          .setParameter("appid", weatherKey)

        val content = Request.Get(builder.build)
          .connectTimeout(1000)
          .socketTimeout(1000)
          .execute().returnContent().asString()

        val json = new JSONObject(content)
        val temp = json.getJSONObject("main").getDouble("temp")
        println(s"Temperature is $temp")

      } finally {
        dbreader.close()
      }

    } finally {
      reader.close()
    }

  }

}
