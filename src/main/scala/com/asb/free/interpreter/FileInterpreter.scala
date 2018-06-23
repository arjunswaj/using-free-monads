package com.asb.free.interpreter

import java.io.File
import java.nio.file.Files

import cats.{Id, ~>}
import com.asb.free.dsl.FileDSL.{CreateTempFilePath, FileAction, GetFilePath, PathToFile}

object FileInterpreter extends (FileAction ~> Id) {

  override def apply[A](fa: FileAction[A]): Id[A] = fa match {
    case CreateTempFilePath(filename, ext) => Files.createTempFile(filename, ext)
    case GetFilePath(filename) => new File(filename).toPath
    case PathToFile(path) => path.toFile
  }

}
