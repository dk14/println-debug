# println-tracer

Usage:

    @trace class MyF {
      def call(param: Int): Int = if (param == 0) param else call(param - 1)
      def call2(param2: Int) = param2
      def call3(param2: Int) = ???
    }
    
    (new MyF).call(5)
    (new MyF).call2(666)
    Try{(new MyF).call3(666)}

Output:

     call(param = 5)
      call(param = 4)
       call(param = 3)
        call(param = 2)
         call(param = 1)
          call(param = 0)
          call = 0
         call = 0
        call = 0
       call = 0
      call = 0
     call = 0
     call2(param2 = 666)
     call2 = 666
     call3(param2 = 666)
     call3 = scala.NotImplementedError: an implementation is missing

Note: macroparadise must be added in client module to use this macro 

    autoCompilerPlugins := true,
    resolvers in ThisBuild  += Resolver.sonatypeRepo("releases"),
    libraryDependencies += "org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full,
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)
