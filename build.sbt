name := "night-meet"

version := "1.0"

scalaVersion := "2.11.8"

assemblyJarName in assembly := "night-meet.jar"
mainClass in assembly := Some("ru.izebit.ApplicationLauncher")
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) =>
    xs map {
      _.toLowerCase
    } match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.first
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.filterDistinctLines
    }
  case _ => MergeStrategy.first
}


libraryDependencies ++= Seq(
  "org.springframework.boot" % "spring-boot-starter-web" % "1.4.0.RELEASE" exclude("org.springframework.boot", "spring-boot-starter-tomcat"),
  "org.springframework.boot" % "spring-boot-starter-undertow" % "1.4.0.RELEASE",

  "org.springframework.data" % "spring-data-mongodb" % "1.9.2.RELEASE",
  "org.mongodb" % "mongo-java-driver" % "3.3.0",


  "org.json" % "json" % "20150729",
  "org.apache.httpcomponents" % "fluent-hc" % "4.5.2",

  "com.github.fakemongo" % "fongo" % "2.0.6" % "test",
  "junit" % "junit" % "4.12" % "test",
  "org.mockito" % "mockito-all" % "1.8.4" % "test",
  "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test",
  "org.springframework" % "spring-test" % "4.3.2.RELEASE" % "test"

)


    