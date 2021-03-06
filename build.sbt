name := "Using Free Monads"

version := "0.1"

scalaVersion := "2.11.11"

scalacOptions += "-Ypartial-unification"

// https://mvnrepository.com/artifact/com.maxmind.geoip2/geoip2
libraryDependencies += "com.maxmind.geoip2" % "geoip2" % "2.12.0"
// https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.5"
// https://mvnrepository.com/artifact/org.apache.httpcomponents/fluent-hc
libraryDependencies += "org.apache.httpcomponents" % "fluent-hc" % "4.5.5"
// https://mvnrepository.com/artifact/org.json/json
libraryDependencies += "org.json" % "json" % "20180130"
// https://mvnrepository.com/artifact/org.typelevel/cats-core
libraryDependencies += "org.typelevel" %% "cats-core" % "1.1.0"
// https://mvnrepository.com/artifact/org.typelevel/cats-free
libraryDependencies += "org.typelevel" %% "cats-free" % "1.1.0"

