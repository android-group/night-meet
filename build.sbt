name := "night-meet"

version := "1.0"


libraryDependencies ++= Seq(
  "org.springframework.boot" % "spring-boot-starter-web" % "1.4.0.RELEASE" exclude("org.springframework.boot", "spring-boot-starter-tomcat"),
  "org.springframework.boot" % "spring-boot-starter-undertow" % "1.4.0.RELEASE",

  "org.springframework.data" % "spring-data-mongodb" % "1.9.2.RELEASE",
  "org.mongodb" % "mongo-java-driver" % "2.11.0",


  "org.json" % "json" % "20150729",
  "commons-lang" % "commons-lang" % "2.6",
  "org.apache.httpcomponents" % "fluent-hc" % "4.5.2"
)


    