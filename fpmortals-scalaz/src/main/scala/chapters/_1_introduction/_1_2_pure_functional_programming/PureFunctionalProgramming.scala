package chapters._1_introduction._1_2_pure_functional_programming

import chapters._1_introduction._1_1_abstracting_over_execution._

final class IO[A](val interpret: () => A) {
  def map[B](f: A => B): IO[B] = IO(f(interpret()))
  def flatMap[B](f: A => IO[B]): IO[B] = IO(f(interpret()).interpret())
}

object IO {
  def apply[A](a: => A): IO[A] = new IO(() => a)
}

class TerminalIo extends Terminal[IO] {
  override def read(): IO[String] = IO { io.StdIn.readLine("TerminalIo_read(): ") }

  override def write(s: String): IO[Unit] = IO { println(s"TerminalIo_write($s)") }
}

object PureFunctionalProgramming {
  implicit val terminalIo: Terminal[IO] = new TerminalIo
  implicit val executionIo: Execution[IO] = new Execution[IO] {
    override def chain[A, B](c: IO[A])(f: A => IO[B]): IO[B] = c.flatMap(f)

    override def create[B](b: B): IO[B] = IO(b)
  }

  def main(): Unit = {
    val echoIo: IO[String] = AbstractingOverExecution.echo[IO]
    echoIo.interpret()
  }

}
