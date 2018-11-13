package companyB.gbd

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.Serializable
import java.util.*

@RunWith(Parameterized::class)
class ProcessorProviderTest<T:GbdModel>(private val _class: Class<T>, private val name: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): List<Array<Serializable>> {
            return listOf(
                    arrayOf(Drummer::class.java, "Drummer"),
                    arrayOf(Kit::class.java, "Kit"),
                    arrayOf(TrackDrummerKit::class.java, "TrackDrummerKit"),
                    arrayOf(Track::class.java, "Track")
            )
        }
    }

    @Test
    fun runTest(){
        System.setProperty("env", "test")
        var exceptionThrown: Boolean = false
        try{
            val provider: Optional<BaseProcessor<T>> = Optional.ofNullable(ProcessorProvider.processor(_class))
            assert(provider.isPresent, lazyMessage = {"Expected instance of provider for $name"})
        }
        catch (e: Exception){
            exceptionThrown = true
        }
        assert(!exceptionThrown, lazyMessage = {"Expected exception not to be thrown when getting provider for $name"})
    }
}