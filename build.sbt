name := "akka-restaurant-app"

version := "0.1"

scalaVersion := "2.13.1"
val akkaV         = "2.6.3"
val logbackV      = "1.2.3"
val scalaTestV    = "3.1.0"




libraryDependencies ++= Seq(
  "com.typesafe.akka"       %% "akka-actor"                 % akkaV,
  "com.typesafe.akka"       %% "akka-slf4j"                 % akkaV,
  "ch.qos.logback"           % "logback-classic"            % logbackV,
  "com.typesafe.akka"       %% "akka-testkit"               % akkaV % "test",
  "org.scalatest"           %% "scalatest"                  % scalaTestV
  /*
  "io.kamon" %% "kamon-bundle" % "2.1.4",
  "io.kamon" %% "kamon-apm-reporter" % "2.1.4"
   */
)