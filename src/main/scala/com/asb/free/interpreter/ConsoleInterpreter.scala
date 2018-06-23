package com.asb.free.interpreter

import java.util.Scanner

import cats.{Id, ~>}
import com.asb.free.dsl.ConsoleDSL.{ConsoleAction, ReadLine, Write}

object ConsoleInterpreter extends (ConsoleAction ~> Id) {
  override def apply[A](fa: ConsoleAction[A]): Id[A] = fa {
    case ReadLine() =>
      val scanner = new Scanner(System.in)
      scanner.next()
    case Write(string) =>
      println(string)
  }
}
