name := "cs250-lab3"

version      := "1.2"

lazy val commonSettings = Seq(
    organization := "berkeley",
    scalaVersion := "2.11.8",
    parallelExecution in Global := false,
    traceLevel   := 15,
    scalacOptions ++= Seq("-deprecation","-unchecked"),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.sonatypeRepo("releases")
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.5",
      "org.scalacheck" %% "scalacheck" % "1.12.4",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value

    )
  )

// Provide a managed dependency on X if -DXVersion="" is supplied on the command line.
val defaultVersions = Map(
  "chisel3" -> "3.1-SNAPSHOT",
  "chisel-iotesters" -> "1.2-SNAPSHOT",
  "dsptools" -> "1.0"
  )

lazy val cde        = (project in file("context-dependent-environments")).
  settings(commonSettings: _*)


lazy val jackhammer = (project in file("jackhammer")).
  settings(commonSettings: _*).
  dependsOn(cde)

lazy val sha3       = (project in file(".")).
  settings(commonSettings: _*).
  settings(Seq(
    libraryDependencies ++= (Seq("chisel3","chisel-iotesters","dsptools").map {
      dep: String => "edu.berkeley.cs" %% dep % sys.props.getOrElse(dep + "Version", defaultVersions(dep)) })
  )).
  dependsOn(cde, jackhammer)
