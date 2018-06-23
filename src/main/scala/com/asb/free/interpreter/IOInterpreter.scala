package com.asb.free.interpreter

import java.nio.file.Files

import cats.{Id, ~>}
import com.asb.free.dsl.IODSL.{Close, GetBufferedReader, IOAction, ReadLine}

object IOInterpreter extends (IOAction ~> Id) {

  override def apply[A](fa: IOAction[A]): Id[A] = fa match {
    case GetBufferedReader(path) => Files.newBufferedReader(path)
    case ReadLine(reader) => reader.readLine()
    case Close(closeable) => closeable.close()
  }

}
