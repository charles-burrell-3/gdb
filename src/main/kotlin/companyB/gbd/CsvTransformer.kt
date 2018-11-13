package companyB.gbd

interface Transformer<T: GbdModel>{
    fun xform(mappings:List<Map<String,String>>): T?
    fun xfrormList(mappings:List<Map<String,String>>): List<T>
}
@Suppress("UNCHECKED_CAST")
class  CsvTransformer<T: GbdModel>(val _class: Class<T>): Transformer<T>{

    private val func: (Map<String,String>) -> T = getFunction()
    private val timeUtils: TimeUtils = TimeUtils()

    override fun xform(mappings: List<Map<String,String>>): T? =
            if(mappings.isEmpty())null
            else func.invoke(mappings[0])

    override fun xfrormList(mappings:List<Map<String,String>>): List<T> = mappings.map{mapping -> func.invoke(mapping)}


    private fun getFunction(): (Map<String, String>) -> T{
        var func: ((Map<String, String>) -> T)? = null
        when (_class) {
            Kit::class.java -> func  = {
                map: Map<String,String> ->
                Kit(name = map["name"]!!,
                        category = map["category"]!!)
            } as (Map<String,String>) -> T

            Drummer::class.java -> func  = {
                map: Map<String,String> ->
                Drummer(name = map["name"]!!,
                        description = map["description"]!!)
            } as (Map<String,String>) -> T

            Track::class.java -> func  = {
                map: Map<String,String> ->
                val time: Int = timeUtils.getTimeFromString(map["time"]!!)
                Track(name = map["name"]!!, time = time, time_string = timeUtils.getTotalTimeAsString(time))
            } as (Map<String,String>) -> T

            TrackDrummerKit::class.java -> func = {
                map: Map<String, String> ->
                TrackDrummerKit(track_name = map["track_name"]!!,
                        drummer_name = map["drummer_name"]!!,
                        kit_name = map["kit_name"]!!)
            } as (Map<String,String>) -> T
        }
        return func!!
    }
}