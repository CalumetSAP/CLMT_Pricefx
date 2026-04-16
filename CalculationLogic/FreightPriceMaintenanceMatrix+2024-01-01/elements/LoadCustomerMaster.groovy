//if (libs.SharedLib.BatchUtils.isNewBatch()) {
//    def customers = api.global.shipToList as List
//    if (!customers) {
//        api.global.customerMasterData = [:]
//        return
//    }
//
//    def qapi = api.queryApi()
//    def t1 = qapi.tables().customers()
//
//    api.global.customerMasterData = qapi.source(t1, [t1.customerId(), t1.PostalCode], t1.customerId().in(customers)).stream { it.collectEntries {
//        [(it.customerId): beforeDash(it.PostalCode as String)]
//    } }
//}
//
//return null
//
//String beforeDash(String value) {
//    if (!value) return null
//    int i = value.indexOf('-')
//    return (i >= 0) ? value.substring(0, i) : value
//}