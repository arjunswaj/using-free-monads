package com.asb.free.program

import com.asb.free.dsl.FileDSL.FileActions
import com.asb.free.dsl.GeoModelDSL.GeoModelActions
import com.asb.free.dsl.IODSL.IOActions
import com.asb.free.dsl.NetworkDSL.NetworkActions
import com.asb.free.interpreter.GeoModelInterpreters.{P, PF}
import com.maxmind.geoip2.model.CityResponse

object GeoModelProgram {

  def getCityResponse(ipAddress: String, databasePath: String)
                     (implicit N: NetworkActions[P], F: FileActions[P],
                      IO: IOActions[P], G: GeoModelActions[P]): PF[CityResponse] = for {
    database <- F.getFile(databasePath)
    databaseReader <- G.getDatabaseReader(database)
    inetAddress <- N.getInetAddress(ipAddress)
    cityResponse <- G.getCityResponseForInetAddress(inetAddress, databaseReader)
    _ <- IO.close(databaseReader)
  } yield cityResponse

}
