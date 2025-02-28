package chapters._1_introduction._1_1_abstracting_over_execution

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

trait Terminal[C[_]] {
  def read(): C[String]
  def write(s: String): C[Unit]
}

object `package` {
  type Now[T] = T
}

class TerminalSync extends Terminal[Now] {
  override def read(): Now[String] = io.StdIn.readLine("TerminalSync_read(): ")

  override def write(s: String): Now[Unit] = println(s"TerminalSync_write(String): $s")
}

class TerminalAsync(implicit ec: ExecutionContext) extends Terminal[Future] {
  override def read(): Future[String] = Future {
    io.StdIn.readLine("TerminalAsync_read(): ")
  }

  override def write(s: String): Future[Unit] = Future {
    println(s"TerminalAsync_write(String): $s")
  }
}

trait Execution[C[_]] {
  def chain[A, B](c: C[A])(f: A => C[B]): C[B]
  def create[B](b: B): C[B]
}

object Execution {
  implicit class Ops[A, C[_]](c: C[A]) {
    def flatMap[B](f: A => C[B])(implicit e: Execution[C]): C[B] =
      e.chain(c)(f)

    def map[B](f: A => B)(implicit e: Execution[C]): C[B] =
      e.chain(c)(f andThen e.create)
  }

  implicit val executionNow: Execution[Now] = new Execution[Now] {
    override def chain[A, B](c: A)(f: A => B): B = f(c)

    override def create[B](b: B): B = b
  }

  implicit def executionFuture(implicit ec: ExecutionContext): Execution[Future] = new Execution[Future] {
    override def chain[A, B](c: Future[A])(f: A => Future[B]): Future[B] = c.flatMap(f)

    override def create[B](b: B): Future[B] = Future.successful(b)
  }
}

object AbstractingOverExecution {
  import Execution.Ops
  import scala.concurrent.ExecutionContext.Implicits.global

  def _echo[C[_]](t: Terminal[C], e: Execution[C]): C[String] =
    e.chain(t.read()) { in: String =>
      e.chain(t.write(in)) { _: Unit =>
        e.create(in)
      }
    }

  def echo[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
    for {
      in <- t.read()
      _ <- t.write(in)
    } yield in

  implicit val terminalNow: Terminal[Now] = new TerminalSync
  implicit val terminalFuture: Terminal[Future] = new TerminalAsync()

  def main(): Unit = {
    echo[Now]: Now[String]

    val futureEcho: Future[String] = echo[Future]
    Await.result(futureEcho, Duration.Inf)
  }

}
