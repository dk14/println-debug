import scala.util.Try

/**
 * Created by user on 9/24/14.
 */



object Test extends App {


  @trace class MyF {
    def call(param: Int): Int = if (param == 0) param else call(param - 1)
    def call2(param2: Int) = param2
    def call3(param2: Int) = ???
  }

  (new MyF).call(5)
  (new MyF).call2(666)
  Try{(new MyF).call3(666)}



}