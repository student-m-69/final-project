name := "fileconv"
version := "1.0.0"
scalaVersion := "3.3.3"

// Java compilation settings
javacOptions ++= Seq("-source", "21", "-target", "21")

// Main class
Compile / mainClass := Some("com.fileconv.App")

// Dependencies
libraryDependencies ++= Seq(
  // JSON
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.0",
  // XML
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.17.0",
  // CSV
  "com.opencsv" % "opencsv" % "5.9",
  // Testing
  "org.junit.jupiter" % "junit-jupiter" % "5.10.2" % Test,
  "org.junit.platform" % "junit-platform-launcher" % "1.10.2" % Test,
  "org.mockito" % "mockito-core" % "5.11.0" % Test,
  "org.mockito" % "mockito-junit-jupiter" % "5.11.0" % Test,
  "net.aichler" % "jupiter-interface" % "0.11.1" % Test
)

// Register jupiter test framework
testFrameworks += new TestFramework("net.aichler.jupiter.api.JupiterFramework")

// Cross paths off for pure Java
crossPaths := false
autoScalaLibrary := false

// Assembly (fat JAR)
assembly / mainClass := Some("com.fileconv.App")
assembly / assemblyJarName := "fileconv.jar"
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", _*) => MergeStrategy.concat
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
