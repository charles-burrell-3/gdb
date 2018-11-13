package companyB.gbd

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.InputStreamReader
import java.io.Reader

data class SourceFileConfigs(val path: String)
data class HashConfigs(val salt: String, val algorithm: String = "MD5")
data class ServerConfigs(val port: Int)

data class GbdConfig(val sourceFiles: SourceFileConfigs, val hashConfigs: HashConfigs, val serverConfigs: ServerConfigs)

class Configurations {

    companion object {
        private val config: GbdConfig = load()
        private fun load(): GbdConfig {
            val env: String = System.getProperty("env", "dev")
            val filepath = "./configs/$env.yml"
            val loader: ClassLoader = Configurations::class.java.classLoader
            val reader: Reader = InputStreamReader(loader.getResourceAsStream(filepath)) as Reader
            val mapper = ObjectMapper(YAMLFactory())
            mapper.registerModule(KotlinModule())
            return reader.use {
                mapper.readValue(it, GbdConfig::class.java)
            }
        }
    }
    fun serverConfigs(): ServerConfigs = config.serverConfigs
    fun sourceFileConfigs(): SourceFileConfigs = config.sourceFiles
    fun hashConfigs(): HashConfigs = config.hashConfigs
}