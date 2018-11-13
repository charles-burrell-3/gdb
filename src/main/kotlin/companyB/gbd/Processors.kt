package companyB.gbd

abstract class BaseProcessor<T: GbdModel>(protected val trackDrummerKitDao: GbdDao<TrackDrummerKit>) {
    abstract fun list(): List<T>

    protected fun isUsed(input: T, trackDrummerKits: List<TrackDrummerKit>, predicate: (TrackDrummerKit, T)-> Boolean):Boolean =
            null != trackDrummerKits.find {tdk-> predicate(tdk, input)}
}

class DrummerProcessor(trackDrummerKitDao: GbdDao<TrackDrummerKit>, private val drummerDao: GbdDao<Drummer>): BaseProcessor<Drummer>(trackDrummerKitDao) {

    override fun list(): List<Drummer> {
        val trackDrummerKits: List<TrackDrummerKit> = trackDrummerKitDao.list()
        return drummerDao.list().map{drummer -> Drummer(drummer.name, drummer.description, isUsed(drummer, trackDrummerKits, { tdk, d -> tdk.drummer_name.equals(d.name) })) }
    }
}

class KitProcessor(trackDrummerKitDao: GbdDao<TrackDrummerKit>, private val kitDao: GbdDao<Kit>): BaseProcessor<Kit>(trackDrummerKitDao) {
    override fun list(): List<Kit> {
        val trackDrummerKits: List<TrackDrummerKit> = trackDrummerKitDao.list()
        return kitDao.list().map{kit -> Kit(kit.name, kit.category, isUsed(kit, trackDrummerKits,{ tdk, k -> tdk.kit_name.equals(k.name) })) }
    }
}

class TrackDrummerKitProcessor(trackDrummerKitDao: GbdDao<TrackDrummerKit>): BaseProcessor<TrackDrummerKit>(trackDrummerKitDao){
    override fun list(): List<TrackDrummerKit> = trackDrummerKitDao.list()

}

class TrackProcessor(trackDrummerKitDao: GbdDao<TrackDrummerKit>, private  val trackDao: GbdDao<Track>): BaseProcessor<Track>(trackDrummerKitDao){
    override fun list(): List<Track> = trackDao.list()
}