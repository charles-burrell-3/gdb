package companyB.gbd

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test

class ProcessorsTests {

    private val drummer: String = "Drummer"
    private val track: String = "Track"
    private val kit: String = "Kit"
    private val category: String = "category"
    private val upper: Int = 10

    private val tracks: List<Track> = (0 until upper).map{index -> Track("Track $index", 60 * (index +1), "${60 * (index +1)}") }
    private val drummers: List<Drummer> = (0 until upper).map { index -> Drummer("$drummer $index", "Description") }
    private val kits: List<Kit> = (0 until upper).map { index -> Kit("$kit $index", "$category $index") }
    private val trackDrummerKits: List<TrackDrummerKit> =
            (0 until upper step 2).map {index -> TrackDrummerKit("$track $index", "$drummer $index", "$kit $index") }

    private var trackDrummerKitDao: GbdDao<TrackDrummerKit>? = null

    @Before
    fun before(){
        trackDrummerKitDao = mock()
        whenever(trackDrummerKitDao!!.list()).thenReturn(trackDrummerKits)
    }

    @Test
    fun testDrummerProcessor(){
        val drummerDao: GbdDao<Drummer> = mock()
        whenever(drummerDao.list()).thenReturn(drummers)
        runTest(drummer, { DrummerProcessor(trackDrummerKitDao!!, drummerDao) }, { d->d.name}, { d -> d.used})
    }

    @Test
    fun testKitProcessor(){
        val kitDao: GbdDao<Kit> = mock()
        whenever(kitDao.list()).thenReturn(kits)
        runTest(kit, { KitProcessor(trackDrummerKitDao!!, kitDao) }, { k -> k.name}, { k -> k.used})
    }

    @Test
    fun testTrackProcessor(){
        val trackDao: GbdDao<Track> = mock()
        whenever(trackDao.list()).thenReturn(tracks)
        val processor = TrackProcessor(trackDrummerKitDao!!, trackDao)
        val actualTracks: List<Track> = processor.list()
        actualTracks.forEachIndexed{index, track -> run{
            assert(tracks[index].name == track.name)
            assert(tracks[index].time == track.time)
        }}
    }

    @Test
    fun testTrackDrummerKitProcessor(){
        val processor: TrackDrummerKitProcessor = TrackDrummerKitProcessor(trackDrummerKitDao!!)
        val tracksFromDao: List<TrackDrummerKit> = processor.list()
        tracksFromDao.forEach { track -> assert(null != trackDrummerKits.find { trackDrummerKit -> trackDrummerKit.track_name.equals(track.track_name)
        && trackDrummerKit.drummer_name.equals(track.drummer_name)
        && trackDrummerKit.kit_name.equals(track.kit_name)})}
    }

    private fun <T: GbdModel, P: BaseProcessor<T>>runTest(prefix: String, supplier: () -> P, nameFunc: (T)-> String, isUsedFunc: (T)-> Boolean){
        val valids: List<String> = (0 until upper).map {index -> "$prefix $index"}
        val used: List<String> = (0 until upper step 2).map { index -> "$prefix $index"}
        val processor: BaseProcessor<T> = supplier.invoke()
        val  listings: List<T> = processor.list()
        assert(upper == listings.size, lazyMessage = {"Expected $upper, found ${listings.size}"})
        (0 until upper).forEach{index -> run{
            listings.forEach { listing -> run{
                val name: String = nameFunc.invoke(listing)
                val thingIsUsed: Boolean = isUsedFunc.invoke(listing)
                assert(valids.contains(name), lazyMessage = {"$name not found in ${valids.joinToString(separator = ",")}"})
               assert(thingIsUsed == used.contains(name), lazyMessage = {"Looking for:$name ->Is Used:$thingIsUsed -> Used: ${used.joinToString(separator = ",")}"})
            }  }
        }}

    }
}