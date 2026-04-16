import java.text.SimpleDateFormat

if (api.global.isFirstRow) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().productExtensionRows("RailRateUpload")

    def fields = [t1.ShippingPoint, t1.Consignee, t1.STCCCode, t1.FreightAgreementExpirationDate, t1.HaulCharges, t1.Distance, t1.DestinationCountry]

    def railRateUploadRows = [:]

    def shippingPointLastFour
    qapi.source(t1, fields)
            .sortBy {cols -> [qapi.orders().ascNullsFirst(cols.FreightAgreementExpirationDate)]}
            .stream { it.each { row ->
                shippingPointLastFour = getLastNDigits(row.ShippingPoint, 4)
                row.Consignee.split(", ").each { consignee ->
                    railRateUploadRows[[shippingPointLastFour, getLastNDigits(consignee, 6), row.STCCCode]] = row
                }
            }}

    api.global.railRateUpload = railRateUploadRows
}

if (!out.NewFreightValidFrom) return [:]
def sdf = new SimpleDateFormat("yyyy-MM-dd")
def validFrom = sdf.format(out.NewFreightValidFrom)
if (!validFrom) return [:]

def shippingPoint = out.LoadQuotes.ShippingPoint
def shipTo = out.LoadQuotes.ShipTo

def railRateUpload = getValidRailRateUpload(api.global.railRateUpload.get([shippingPoint, shipTo, out.LoadSTCCToMaterialCPT.fullSTCC]), validFrom)
if (railRateUpload) return railRateUpload

railRateUpload = getValidRailRateUpload(api.global.railRateUpload.get([shippingPoint, shipTo, out.LoadSTCCToMaterialCPT.sixDigitsSTCC]), validFrom)
if (railRateUpload) return railRateUpload

railRateUpload = getValidRailRateUpload(api.global.railRateUpload.get([shippingPoint, shipTo, "Any"]), validFrom)
if (railRateUpload) return railRateUpload

return [:]

String getLastNDigits (String value, Integer n) {
    int len = value.length()
    return value.substring(Math.max(0, len - n), len)
}

Map getValidRailRateUpload (railRateUpload, validFrom) {
    if (railRateUpload?.FreightAgreementExpirationDate?.toString() >= validFrom) {
        return railRateUpload
    }
    return null
}