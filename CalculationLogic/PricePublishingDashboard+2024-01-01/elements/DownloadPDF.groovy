import net.pricefx.server.dto.calculation.DashboardController

import java.text.SimpleDateFormat

final constants = libs.DashboardConstantsLibrary.PricePublishing

DashboardController controller = api.newController()

def createCalendarFromDateString(dateString, format = "yyyy-MM-dd") {
    def dateFormat = new SimpleDateFormat(format)
    def outputFormat = new SimpleDateFormat("MM/dd/yyyy")
    def date = dateFormat.parse(dateString) // Parse the string into a Date object
    def calendar = Calendar.getInstance()
    calendar.time = date // Set the Calendar's time to the parsed date
    return outputFormat.format(calendar.time)
}

def today = Calendar.getInstance().getTime()

def fileOutputFormat = new SimpleDateFormat("MM_dd_yyyy")

def formattedDate = fileOutputFormat.format(today)

def templateLogicName = "PublishingPDFPriceLetterSPS"
def publishingTemplate = "Price Letter - SPS"
def filename = "Price_Letter_SPS_" + formattedDate // date of run
List<Map<String, Object>> rows = api.local.exportPdf

def configurator = out.Filters
def pricingDate = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRICING_DATE_INPUT_KEY)
def showAdder = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.SHOW_ADDER_INPUT_KEY)
def masterParent = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.MASTER_PARENT_INPUT_KEY)
def label = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.LABEL_INPUT_KEY)

def customer = null

if(label){
    customer = label
} else if (masterParent){
    customer = api.find("C", 0, 1, "customerId", ["name"], Filter.equal("customerId", masterParent))?.find()?.name
}

def variantName = configurator[constants.VARIANT_INPUT_KEY] ?: configurator[constants.VARIANT_NAME_INPUT_KEY]
def additionalNotes = api.global.footers?.get(variantName)?.value

Map parameters = [
        "pricingDate": createCalendarFromDateString(pricingDate),
        "rows": rows,
        "showAdder": showAdder,
        "customer": customer,
        "additionalNotes": additionalNotes
]

controller.addDownloadButton('Download PDF',
        """/formulamanager.executeformula/${templateLogicName}?templateName=${publishingTemplate}&output=pdf&fileName=${filename}""",
        api.jsonEncode(parameters)?.toString())

return controller