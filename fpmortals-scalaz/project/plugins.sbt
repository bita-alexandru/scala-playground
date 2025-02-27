scalacOptions ++= Seq("-unchecked", "-deprecation")
ivyLoggingLevel := UpdateLogging.Quiet

addSbtPlugin("com.lucidchart" % "sbt-scalafmt-coursier" % "1.14")
