def pricingDate = api.local.pricingDate
def pricelists = api.local.pricelists

def filters = [
        Filter.in("PricelistID", pricelists),
        Filter.greaterOrEqual("EffectiveDate", pricingDate),
]

def ctx = api.getDatamartContext()
def ds = ctx.getDataSource("BasePricing")
def query = ctx.newQuery(ds, true)
        .select("ProductId", "ProductId")
        .select("PricelistID", "PricelistID")
        .select("BasePrice", "BasePrice")
        .select("EffectiveDate", "EffectiveDate")
        .select("UOM", "UOM")
        .where(*filters)
        .orderBy("EffectiveDate ASC")
def pricingData = ctx.executeQuery(query)?.getData()?.collect { it }
def latestPricingData = [:]
pricingData.each { record ->
    def key = "${record.ProductId}-${record.PricelistID}"
    if (!latestPricingData.containsKey(key)) {
        latestPricingData[key] = record
    }
}
pricingData = latestPricingData?.values().toList()

def productIds = pricingData*.ProductId
def products = api.stream("P", "sku", ["sku", "label", "attribute12"], Filter.in("sku", productIds))
        ?.withCloseable { it?.collectEntries {
            [(it.sku): [
                    Description         : it.label,
                    ItemNumber          : it.attribute12
            ]]
        } }

def today = new Date()
ds = ctx.getDataSource("ZDGS")
filters = [
        Filter.in("Material", productIds),
        Filter.greaterOrEqual("ValidTo", today)
]
query = ctx.newQuery(ds, true)
        .select("Material", "Material")
        .select("PL", "PL")
        .select("ValidFrom", "ValidFrom")
        .select("CondRecNo", "CondRecNo")
        .select("UoM", "UOM")
        .select("Amount", "Amount")
        .where(*filters)
        .orderBy("ValidFrom ASC")
def zdgs = ctx.executeQuery(query)?.getData()?.collect { it }
def latestZDGS = [:]
zdgs.each { record ->
    def key = "${record.Material}-${record.PL}"
    if (!latestZDGS.containsKey(key)) {
        latestZDGS[key] = record
    }
}
zdgs = latestZDGS?.values().toList()

ds = ctx.getDataSource("ZDGSScales")
filters = [
        Filter.in("CondRecNo", zdgs*.CondRecNo),
]
query = ctx.newQuery(ds, true)
        .select("CondRecNo", "CondRecNo")
        .select("ScaleQuantity", "ScaleQuantity")
        .select("ConditionRate", "ConditionRate")
        .where(*filters)
def zdgsScales = ctx.executeQuery(query)?.getData()?.collect { it }?.groupBy { it.CondRecNo }

zdgs.each{
    it.Scales = zdgsScales[it.CondRecNo]
}

//Adding information to pricingData
pricingData.each{
    it.ItemNumber = products[it.ProductId]?.ItemNumber
    it.Description = products[it.ProductId]?.Description
    it.Channel = it.PricelistID == "01" ? "Wholesale" : (it.PricelistID == "02" ? "Distributor" : "")
    it.ZGDS = zdgs?.groupBy { group -> group.Material+"-"+group.PL }?.get(it.ProductId+"-"+it.PricelistID)
    //TODO: Find MOQ and MOQ UOM and add them to pricingData
}

pricingData = pricingData.groupBy { it.ProductId }.collect { productId, records ->
    def transformedRecord = [
            ProductId       : productId,
            UOM             : records[0].UOM,
            EffectiveDate   : records[0].EffectiveDate,
            ItemNumber      : records[0].ItemNumber,
            Description     : records[0].Description,
            ZGDS            : records[0].ZGDS,
    ]

    records.each { record ->
        transformedRecord["BasePrice_${record.PricelistID}"] = record.BasePrice
    }

    return transformedRecord
}

return pricingData