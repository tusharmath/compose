package compose.dsl
import compose._

trait DebugDSL[-A, +B] { self: A ~> B =>
  import Lambda._
  import ExecutionPlan._

  final def debug[B1 >: B](name: String): A ~> B1 = make[A, B1] { Debugger.Debug(self.compile, name) }

  final def show(name: String): A ~> B = make[A, B](Debugger.Show(self.compile, name))

  final def address: Any ~> String = make[Any, String] { Debugger.Address(self.compile) }
}
