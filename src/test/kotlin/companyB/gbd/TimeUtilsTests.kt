package companyB.gbd

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class TimeUtilsTests(private val timeString: String, private val expected: Int) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() : List<Array<Any>> {
            return listOf(
                    arrayOf("0:30", 30),
                    arrayOf("1:00", 60),
                    arrayOf("2:00", 120),
                    arrayOf("2:30", 150),
                    arrayOf("2:45", 165)
            )
        }
    }
    val timeUtils: TimeUtils = TimeUtils()

    @Test
    fun testGetTimeFromString(){
        val actual: Int = timeUtils.getTimeFromString(timeString)
        assert(expected == actual, lazyMessage = {"Expected $expected, found $actual for a string value of $timeString"})
    }

    @Test
    fun testGetTotalTimeAsString(){
        val actualTimeAsString: String = timeUtils.getTotalTimeAsString(expected)
        assert(timeString == actualTimeAsString, lazyMessage = {"Expected $timeString, got $actualTimeAsString for a value of $expected"})
    }
}