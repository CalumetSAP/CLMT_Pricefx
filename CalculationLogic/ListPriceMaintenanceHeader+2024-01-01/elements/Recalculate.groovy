//api.logInfo("ALAN - test", api.stream("XPLI", null, Filter.equal("pricelistId", "319"))?.withCloseable { it.collect() }?.first())
//api.logInfo("ALAN - current item id", api.currentItem().get("id"))
//
//def headerInputs = api.jsonDecode(api.currentItem()?.configuration)?.headerInputs
//api.logInfo("ALAN - currentItem", headerInputs)
//def scale = headerInputs?.find { it.name == "ScaleHeaderInput" }?.value
//api.logInfo("ALAN - scale", scale)
//
//def newItem = [
//        "pricelistId" : api.currentItem().get("id"),
//        "sku": "301907175193",
//        "key2": "US50-61-1",
//        "label": "test"
//]
//api.addOrUpdate("XPLI", newItem)