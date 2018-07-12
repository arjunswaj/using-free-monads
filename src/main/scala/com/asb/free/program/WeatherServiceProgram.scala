package com.asb.free.program

import com.asb.free.dsl.FileDSL.FileActions
import com.asb.free.dsl.IODSL.IOActions
import com.asb.free.interpreter.WeatherServiceInterpreters.{P, PF}

object WeatherServiceProgram {

  def getKey(keyPath: String)(implicit F: FileActions[P], I: IOActions[P]): PF[String] = for {
    filePath <- F.getFilePath(keyPath)
    reader <- I.getBufferedReader(filePath)
    key <- I.readLine(reader)
    _ <- I.close(reader)
  } yield key

}
