if (libs.SharedLib.BatchUtils.isNewBatch()) {
    List<Object> quotes = api.global.quotes?.collect()
    HashSet<String> soldTos = new HashSet<>()
    soldTos.add("*")
    soldTos.addAll(quotes?.SoldTo ?: [])
    soldTos.remove(null)

    api.global.exclusionsWithXOnNoOfDays = libs.PricelistLib.Common.getExclusions(soldTos, Filter.in("attribute1", ["X", "x"]))
}

return null