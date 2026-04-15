def configurator = out.InlineConfigurator
def pricingDate = configurator."PricingDateInput"
def pricelists = configurator."PriclistsInput"

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
        .select("UOM", "UOM")
        .where(*filters)
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

pricingData.each{
    it.ItemNumber = products[it.ProductId]?.ItemNumber
    it.Description = products[it.ProductId]?.Description
    it.Channel = it.PricelistID == "01" ? "Wholesale" : (it.PricelistID == "02" ? "Distributor" : "")

    //TODO: Find MOQ and MOQ UOM and add them to pricingData
}

return pricingData