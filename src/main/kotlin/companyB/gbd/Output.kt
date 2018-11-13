package companyB.gbd


fun outputDrummers(drummers: List<Drummer>): String  =
        html(table(drummerRows(drummers), "Name,Description"))

fun outputKits(kits: List<Kit>): String =
        table(kitRows(kits), "Name,Category")

fun outputTracks(tracks: List<Track>): String =
        html(table(trackRows(tracks),"Name,Duration"))

private fun html(table: String): String =
        "<html><head></head><body>$table</body></html>"
private fun trackRows(tracks: List<Track>): String =
        tracks.map {track -> trackRow(track)}.joinToString(separator = "")

private fun trackRow(track:Track): String =
        tableRow("${track.name}${track.time}", false)

private fun drummerRows(drummers: List<Drummer>): String =
        drummers.map { drummer -> drummerRow(drummer)}.joinToString(separator = "")

private fun kitRows(kits: List<Kit>): String =
        kits.map { kit -> kitRow(kit) }.joinToString(separator = "")

private fun kitRow(kit:Kit): String =
        tableRow("${kit.name}${kit.category}", kit.used)

private fun drummerRow(drummer: Drummer): String =
        tableRow("${tableCell(drummer.name)}${tableCell(drummer.description)}",drummer.used)

private fun tableRow(contents: String, isUsed: Boolean): String =
        "<div class='rTableRow ${if(isUsed)"is_used" else ""}'>$contents</div>"

private fun tableCell(contents: String): String = "<div class='rTableCell'>$contents</div>"

private fun table(contents: String, headings: String) =
        "<table class='rTable'>${tableHeader(headings)}$contents</table>"

private fun tableHeader(headings: String): String =
        "<div class='rTableHeading'>${tableHeaders(headings)}</div>"

private fun tableHeaders(headings: String): String =
    headings.split(",").map { header -> tableCell(header) }.joinToString(separator = "")