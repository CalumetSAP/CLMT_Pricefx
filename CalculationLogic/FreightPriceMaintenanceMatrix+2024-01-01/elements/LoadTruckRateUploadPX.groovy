//if (libs.SharedLib.BatchUtils.isNewBatch()) {
//    def qapi = api.queryApi()
//    def t1 = qapi.tables().productExtensionRows("TruckRateUpload")
//    def filters = []
//    if (api.global.modeOfTransportation) filters.add(t1.ShippingType.equal(api.global.modeOfTransportation as String))
//    if (api.global.meansOfTransportation) filters.add(t1.VehicleGroup.in(api.global.meansOfTransportation as List))
//    if (api.global.effectiveDate) {
//        filters.add(t1."Valid From".lessOrEqual(qapi.exprs().dateOnly(api.global.effectiveDate as Date)))
//        filters.add(t1.ValidTo.greaterOrEqual(qapi.exprs().dateOnly(api.global.effectiveDate as Date)))
//    }
//
//    def filter = qapi.exprs().and(*filters)
//
//    api.global.truckRateUpload = qapi.source(t1, [t1.Consignee, t1.OriginPostalCode, t1.DestinationPostalCode], filter).stream { it.collect {
//        it.Consignee ? (it.OriginPostalCode + "|" + getLast6(it.Consignee as String)) : (it.OriginPostalCode + "|" + it.DestinationPostalCode)
//    } }
//}
//
//return null
//
//String getLast6(String value) {
//    if (!value) return null
//    int n = 6
//    int len = value.length()
//    return value.substring(Math.max(0, len - n), len)
//}