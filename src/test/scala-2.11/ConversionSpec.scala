import org.scalatest.WordSpec

import com.frenchcoder.scalamones.utils.Conversion._

class ConversionSpec extends WordSpec {

  "Conversion" must {
    "implicitly convert Double to java.lang.Long" in  {
      def compare(jl1:java.lang.Long, jl2:java.lang.Long) = jl1 == jl2

      val jl:java.lang.Long = new java.lang.Long(123456)
      assert(compare(jl, 123456d))
      assert(compare(0d, 0d))
      assert(!compare(0d, -1d))
      assert(compare(java.lang.Long.MAX_VALUE.toDouble, java.lang.Long.MAX_VALUE.toDouble))
    }

    "convert time in millisecond to human readable format" in {
      val serie = Map(0l -> "0s",
        1l -> "<1s",
        1000l -> "1s",
        1000*60l -> "1m",
        1000*60l+17000 -> "1m 17s",
        1000*60*60l -> "1h",
        1000*60*60*24l -> "1d",
        1000*60*60*24*10l + 1000*60*60*14l + 1000*60*27l + 1000*3l -> "10d 14h 27m 3s")

      serie foreach { a => assert(millisToHuman(a._1) == a._2)}

    }
  }

}
