object Main {

  private def `_1_1_abstracting_over_execution.AbstractingOverExecution`(): Unit = {
    import chapters._1_introduction._1_1_abstracting_over_execution.AbstractingOverExecution
    AbstractingOverExecution.main()
  }

  private def `_1_2_pure_functional_programming`(): Unit = {
    import chapters._1_introduction._1_2_pure_functional_programming.PureFunctionalProgramming
    PureFunctionalProgramming.main()
  }

  def main(args: Array[String]): Unit = {
    println("Hello world!")

//    `_1_1_abstracting_over_execution.AbstractingOverExecution`()
    `_1_2_pure_functional_programming`()
  }
}
