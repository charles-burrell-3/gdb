package companyB.gbd
open class GbdModel()
data class Kit(val name: String, val category:String, val used: Boolean = false): GbdModel()
data class Drummer(val name: String, val description: String, val used: Boolean = false): GbdModel()
data class Track(val name: String, val time: Int, val time_string: String): GbdModel()
data class TrackDrummerKit(val track_name: String, val drummer_name: String, val kit_name: String): GbdModel()