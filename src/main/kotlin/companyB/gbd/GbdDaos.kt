package companyB.gbd

interface GbdDao<T: GbdModel>{
    fun list(): List<T>
}

class GbdDaoCsvImpl<T: GbdModel>(_class: Class<T>, configs: SourceFileConfigs): GbdDao<T> {
    val listings: MutableList<T> = mutableListOf()

    init {
        val xformer: CsvTransformer<T> = CsvTransformer(_class)
        val filepath = "${configs.path}/${_class.simpleName.toLowerCase()}s.csv"
        val utils = CsvUtils()
        val mappings: List<Map<String,String>> = utils.getMappings(filepath)
        listings.addAll(xformer.xfrormList(mappings))
    }
    override fun list(): List<T> = listings
}