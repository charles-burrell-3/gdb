package companyB.gbd


@Suppress("UNCHECKED_CAST")
object ProcessorProvider {
    private val configs: Configurations = Configurations()
    private var drummersDao: GbdDao<Drummer>? = null
    private var kitsDao: GbdDao<Kit>? = null
    private var tracksDao: GbdDao<Track>? = null
    private var trackDrummerKitsDao: GbdDao<TrackDrummerKit>? = null
    private var drummerProcessor: DrummerProcessor? = null
    private var kitProcessor: KitProcessor? = null
    private var trackProcessor: TrackProcessor? = null
    private var trackDrummerKitProcessor: TrackDrummerKitProcessor? = null

    fun <T:GbdModel, R: BaseProcessor<T>>processor(_class: Class<T>): R{
        if (_class == Drummer::class.java) return drummerProcessor() as R
        else if (_class == Kit::class.java) return kitProcessor() as R
        else if (_class == Track::class.java) return trackProcessor() as R
        else return trackDrummerKitProcessor() as R
    }

    private fun drummerProcessor(): DrummerProcessor {
        initTrackDrummerKitDao()
        if(null == drummersDao) drummersDao =  GbdDaoCsvImpl<Drummer>(Drummer::class.java, configs.sourceFileConfigs())
        if(null == drummerProcessor) drummerProcessor = DrummerProcessor(trackDrummerKitsDao!!, drummersDao!!)
        return drummerProcessor!!
    }

    private fun kitProcessor(): KitProcessor{
        initTrackDrummerKitDao()
        if(null == kitsDao) kitsDao = GbdDaoCsvImpl<Kit>(Kit::class.java, configs.sourceFileConfigs())
        if(null == kitProcessor) kitProcessor = KitProcessor(trackDrummerKitsDao!!, kitsDao!!)
        return kitProcessor!!
    }

    private fun trackDrummerKitProcessor(): TrackDrummerKitProcessor{
        initTrackDrummerKitDao()
        if(null == trackDrummerKitProcessor)
            trackDrummerKitProcessor = TrackDrummerKitProcessor(trackDrummerKitsDao!!)
        return trackDrummerKitProcessor!!
    }

    private fun trackProcessor(): TrackProcessor{
        initTrackDrummerKitDao()
        if(null == tracksDao) tracksDao = GbdDaoCsvImpl<Track>(Track::class.java, configs.sourceFileConfigs())
        if(null == trackProcessor) trackProcessor = TrackProcessor(trackDrummerKitsDao!!, tracksDao!!)
        return trackProcessor!!
    }

    private fun initTrackDrummerKitDao(): Unit{
        if(null == trackDrummerKitsDao)
            trackDrummerKitsDao = GbdDaoCsvImpl<TrackDrummerKit>(TrackDrummerKit::class.java, configs.sourceFileConfigs())
    }
}