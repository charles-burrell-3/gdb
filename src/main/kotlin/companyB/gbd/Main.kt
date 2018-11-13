package companyB.gbd

import io.javalin.Javalin
import io.javalin.rendering.JavalinRenderer
import io.javalin.rendering.template.JavalinPebble
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader


private val timeUtils: TimeUtils = TimeUtils()
private val drummerProcessor: DrummerProcessor = ProcessorProvider.processor(Drummer::class.java)
private val trackProcessor: TrackProcessor = ProcessorProvider.processor(Track::class.java)
private val kitProcessor: KitProcessor = ProcessorProvider.processor(Kit::class.java)
private val trackDrummerKitProcessor: TrackDrummerKitProcessor = ProcessorProvider.processor(TrackDrummerKit::class.java)
private val configs: ServerConfigs = Configurations().serverConfigs()
private val templateFile: String = "templates"
private val mainTemplateFile: String = "$templateFile/main.peb"
private val links: Map<String,String> = mapOf("kits" to "Available kits",
        "drummers" to "Available Drummers", "tracks" to "Recorded Tracks",
        "tdks" to "Tracks with their Drummers and Kits")

data class TrackDrummerKitWrapper(val name: String, val drummersKits: String)
data class DrummerKitWrapper(val name: String, val text: String, val used: Boolean, val other: Any? = null)
class BannerClass{}

fun main(args: Array<String>){
     JavalinRenderer.register(JavalinPebble, ".peb", ".pebble")
    val logger: Logger = LoggerFactory.getLogger("GBD")
    val app: Javalin = Javalin.create()
    app.enableStaticFiles("css/")
    app.enableStaticFiles("static/")
    app.start(configs.port)
    logger.info("Starting on port ${configs.port}")
    logger.info("\n\n${getBannerText()}\n\n")
    val links: List<String> = genListing(configs.port)
    links.forEach{link -> logger.info(link)}
    app.get("/kits",{ctx -> ctx.render(mainTemplateFile, getKitsMapping())})
    app.get("/drummers") { ctx -> ctx.render(mainTemplateFile, getDrummersMapping()) }
    app.get("/tracks") { ctx -> ctx.render(mainTemplateFile, getTracksMapping()) }
    app.get("/tdks", {ctx -> ctx.render("$templateFile/trackdrummerkits.peb", getTrackDrummerKitsMapping())})

}

private fun getDrummersMapping(): Map<String,Any>{
    val drummers: List<Drummer> = drummerProcessor.list().sortedBy{drummer -> drummer.name}
    val items: List<DrummerKitWrapper> = drummers.map {drummer -> DrummerKitWrapper(drummer.name, drummer.description, drummer.used)}
    val title = "Drummers"
    val headers: List<String> = listOf("Name", "Description")
    val total: Int = items.size
    val used: Int = items.filter {item -> item.used}.count()
    return mapOf("title" to title, "items" to items, "headers" to headers, "footer_name" to "Drummers Used","footer_text" to "$used out of $total total")
}

private fun getKitsMapping(): Map<String,Any>{
    val kits: List<Kit> = kitProcessor.list().sortedBy{kit -> kit.name}
    val items: List<DrummerKitWrapper> = kits.map {kit -> DrummerKitWrapper(kit.name, kit.category, kit.used)}
    val title = "Kits"
    val headers: List<String> = listOf("Name", "Category")
    val total: Int = items.size
    val used: Int = items.filter { item -> item.used }.count()
    return mapOf("title" to title, "items" to items, "headers" to headers, "footer_name" to "Kits Used","footer_text" to "$used out of $total total")
}

private fun getTracksMapping(): Map<String,Any>{
    val tracks: List<Track> = trackProcessor.list().sortedBy{track -> track.name}
    val items: List<DrummerKitWrapper> = tracks.map{track -> DrummerKitWrapper(track.name, track.time_string, false) }
    val title = "Tracks"
    val headers: List<String> = listOf("Name", "Time")
    val total: Int = tracks.map { track -> track.time }.fold(0, {total, next -> total + next})
    return mapOf("title" to title, "items" to items, "headers" to headers, "footer_name" to "Total Time","footer_text" to timeUtils.getTotalTimeAsString(total))
}

private fun getTrackDrummerKitsMapping(): Map<String, Any>{
    val wrappers: MutableList<TrackDrummerKitWrapper> = mutableListOf()
    val trackDrummerKits: List<TrackDrummerKit> = trackDrummerKitProcessor.list()
    val mappings: Map<String, List<TrackDrummerKit>> = trackDrummerKits.groupBy {key -> key.track_name}
    mappings.forEach {name, tdks -> run{
        val expes = StringBuffer()
        tdks.forEach{tdk -> run{
            if(tdk.drummer_name.isNotBlank() && tdk.kit_name.isNotBlank()){
                expes.append("${if(expes.isNotEmpty())" and " else "" } ${tdk.drummer_name} playing the ${tdk.kit_name} kit\n")
            }
        }}
        wrappers.add(TrackDrummerKitWrapper(name, expes.toString()))
    } }
    val title = "Tracks Rollup"
    val headers: List<String> = listOf("Name", "Drummers/Kits")
    val most_used_drummer = getMostUsedDrummer(trackDrummerKits)
    val most_used_kit = getMostUsedKit(trackDrummerKits)
    return mapOf("title" to title, "tdks" to wrappers.sortedBy{wrapper -> wrapper.name}, "headers" to headers,
            "most_used_drummer" to most_used_drummer, "most_used_kit" to most_used_kit)
}

private fun getMostUsedDrummer(trackDrummerKits: List<TrackDrummerKit>): String  = getMostUsed(trackDrummerKits, {tdk -> tdk.drummer_name})
private fun getMostUsedKit(trackDrummerKits: List<TrackDrummerKit>): String  = getMostUsed(trackDrummerKits, {tdk -> tdk.kit_name})

private fun getMostUsed(trackDrummerKits: List<TrackDrummerKit>, func: (TrackDrummerKit) -> String): String{
    var count = 0
    var name = ""
    trackDrummerKits.sortedBy{tdk -> tdk.track_name}.groupBy{tdk -> func.invoke(tdk)}.entries.forEach{entry -> run{
        if(entry.value.size > count){
            count = entry.value.size
            name = entry.key
        }
    }}
    return if(count > 0) "$name ($count)" else  ""
}

private fun getBannerText(): String{
    val loader: ClassLoader = BannerClass::class.java.classLoader
    val stream = loader.getResourceAsStream("static/banner.txt")
    val reader = BufferedReader(InputStreamReader(stream))
    return reader.readText()

}

private fun genListing(port: Int): List<String> =
        links.map {entry -> "http://localhost:$port/${entry.key} -> Listing of ${entry.value}"}.sortedBy {link -> link}


