//import java.text.SimpleDateFormat
//
//def pricelist = api.currentItem()
//
//if (pricelist) {
//    def sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//    def sdfOutput = new SimpleDateFormat("yyyy-MM-dd")
//
//    def createDate = sdfInput.parse(pricelist.createDate)
//    createDate = sdfOutput.format(createDate)
//
//    def label = "Mass Edit " + createDate
//
//    api.addOrUpdate("PL", ["id": pricelist?.id, "label": label])
//}
