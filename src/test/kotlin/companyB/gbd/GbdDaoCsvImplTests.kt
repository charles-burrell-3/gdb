package companyB.gbd

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.Serializable

@RunWith(Parameterized::class)
class GbdDaoCsvImplTests<T: GbdModel>(val _class: Class<T>, val typeOf: String){

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() : List<Array<Serializable>> {
            return listOf(
                    arrayOf(Kit::class.java, "Kit"),
                    arrayOf(Drummer::class.java, "Drummer"),
                    arrayOf(Track::class.java, "Track"),
                    arrayOf(TrackDrummerKit::class.java, "Track Drummer Kit")
            )
        }
    }


    @Before
    fun before(){
        System.setProperty("env", "test")
        System.out.println("Before: ${System.getProperty("env")}")
    }

    @After
    fun after(){
        System.clearProperty("env")
        System.out.println("After: ${System.getProperty("env")}")
    }
    @Test
    fun testList(){
        val configs: Configurations = Configurations()
        val dao: GbdDao<T> = GbdDaoCsvImpl<T>(_class,configs.sourceFileConfigs())
        val expected: Int = 3
        val list: List<T> = dao.list()
        val actual: Int = list.size
        assert(expected == actual, lazyMessage = {"Expected $expected, found $actual for type $typeOf"})
        val func: (T, Int)-> Unit = verificationFun()
        list.forEachIndexed{index, item -> func.invoke(item, index +1)}

    }

    private fun verificationFun(): (T, Int)->Unit{
        return when (_class) {
            Kit::class.java -> { input, index -> run{
                assert((input as Kit).name.equals("test kit $index"))
            }}
            Drummer::class.java -> { input, index -> run{
                assert((input as Drummer).name.equals("test drummer $index"))
                assert((input as Drummer).description.equals("$index"))
            }}
            TrackDrummerKit::class.java -> { input, index -> run{
                assert((input as TrackDrummerKit).kit_name.equals("test kit $index"))
                assert((input as TrackDrummerKit).drummer_name.equals("test drummer $index"))
                assert((input as TrackDrummerKit).track_name.equals("test track $index"))
            }}
            else -> { input, index -> run{
                assert((input as Track).name.equals("test track $index"))
                assert((input as Track).time.equals(60 * index))
            }}
        }
    }
}