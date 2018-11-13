package companyB.gbd

import java.io.InputStream

class CsvUtils {

    private val classLoader: ClassLoader = CsvUtils::class.java.classLoader

    fun getMappings(filename: String): List<Map<String,String>>{
        val mappings: MutableList<Map<String,String>> = mutableListOf()
        val contents: List<String> = getFileContents(filename)
        val headers: List<String> = contents[0].split(",")
        contents.subList(1, contents.size).forEach {line->run {
            val splits: List<String> = line.split(",")
            val map: MutableMap<String, String> = mutableMapOf()
            headers.forEachIndexed { index, header ->
                run {
                    val value: String = splits[index]
                    map[header.trim()] = value.trim()
                }
            }
            mappings.add(map)
        }}
        return mappings
    }

    private fun getFileContents(filename: String): List<String>{
        val stream: InputStream = classLoader.getResourceAsStream(filename)
        val contents: MutableList<String> = mutableListOf()
        stream.bufferedReader().useLines { lines -> contents.addAll(lines)}
        return contents
    }
}

class TimeUtils{

    fun getTimeFromString(timeString: String): Int {
        val splits: List<String> = timeString.split(":")
        val minutes: Int = splits[0].toInt()
        val seconds: Int = splits[1].toInt()
        return (minutes * 60) + seconds
    }

    fun getTotalTimeAsString(time: Int): String {
        val minutes: Int = (time / 60)
        val seconds: String = pad(time % 60)
        return "$minutes:$seconds"

    }
    private fun pad(input: Int): String = if(input > 9) input.toString() else "0$input"
}