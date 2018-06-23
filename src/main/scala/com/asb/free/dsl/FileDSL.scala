package com.asb.free.dsl

import java.io.File
import java.nio.file.Path

import cats.InjectK
import cats.free.Free

object FileDSL {

  sealed trait FileAction[A]

  case class CreateTempFilePath(filename: String, ext: String) extends FileAction[Path]
  case class GetFilePath(filename: String) extends FileAction[Path]
  case class PathToFile(path: Path) extends FileAction[File]

  class FileActions[F[_]](implicit I: InjectK[FileAction, F]) {

    type FileActionF[A] = Free[F, A]

    def createTempFilePath(filename: String, ext: String): FileActionF[Path] =
      Free.inject(CreateTempFilePath(filename, ext))

    def getFilePath(filename: String): FileActionF[Path] =
      Free.inject(GetFilePath(filename))

    def pathToFile(path: Path): FileActionF[File] =
      Free.inject(PathToFile(path))

    def createTempFile(filename: String, ext: String): FileActionF[File] = for {
      path <- createTempFilePath(filename, ext)
      file <- pathToFile(path)
    } yield file

    def getFile(filename: String): FileActionF[File] = for {
      path <- getFilePath(filename)
      file <- pathToFile(path)
    } yield file

  }

  object FileActions {
    def apply[F[_]](implicit I: InjectK[FileAction, F]): FileActions[F] = new FileActions[F]
  }

}
