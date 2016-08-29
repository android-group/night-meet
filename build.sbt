name := "night-meet"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies ++= Seq(
  "org.springframework.boot" % "spring-boot-starter-web" % "1.4.0.RELEASE" exclude("org.springframework.boot", "spring-boot-starter-tomcat"),
  "org.springframework.boot" % "spring-boot-starter-undertow" % "1.4.0.RELEASE",

  "org.springframework.data" % "spring-data-mongodb" % "1.9.2.RELEASE",
  "org.mongodb" % "mongo-java-driver" % "3.3.0",


  "org.json" % "json" % "20150729",
  "org.apache.httpcomponents" % "fluent-hc" % "4.5.2",

  "com.github.fakemongo" % "fongo" % "2.0.6" % "test",
  "junit" % "junit" % "4.12" % "test",
  "org.mockito" % "mockito-all" % "2.0.2-beta" % "test",
  "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test",
  "org.springframework" % "spring-test" % "4.3.2.RELEASE" % "test"

)


    