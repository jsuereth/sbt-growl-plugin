sbtPlugin := true

name := "sbt-growl-plugin"

organization := "me.lessis"

version <<= (sbtVersion)("0.1.2-%s".format(_))

resolvers += "less is" at "http://repo.lessis.me"

libraryDependencies += "me.lessis" %% "meow" % "0.1.1"

libraryDependencies += "de.huxhorn.sulky" % "de.huxhorn.sulky.3rdparty.jlayer" % "1.0"

publishTo :=  Some(Resolver.file("lessis repo", new java.io.File("/var/www/repo")))
