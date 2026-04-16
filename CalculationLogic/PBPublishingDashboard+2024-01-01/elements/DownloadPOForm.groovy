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

def templateLogicName = "PublishingExcelCustomerSpecificPB"
def publishingTemplate = "Customer Specific - PB"
def filename = "Customer_Specific_International_PB_" + formattedDate // date of run
List<Map<String, Object>> rows = api.local.exportPdf

def configurator = out.Filters
def pricingDate = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRICING_DATE_INPUT_KEY)
def masterParent = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.MASTER_PARENT_INPUT_KEY)
def soldTo = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.SOLD_TO_INPUT_KEY)
def shipTo = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.SHIP_TO_INPUT_KEY)
def contract = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.CONTRACT_INPUT_KEY)

def variantName = configurator[constants.VARIANT_INPUT_KEY] ?: configurator[constants.VARIANT_NAME_INPUT_KEY]
def additionalNotes = api.global.footers?.get(variantName)

Map parameters = [
        "pricingDate"    : createCalendarFromDateString(pricingDate),
        "rows"           : rows,
        "customer"       : masterParent,
        "soldTo"         : soldTo,
        "shipTo"         : shipTo,
        "contract"       : contract,
        "additionalNotes": additionalNotes,
]

controller.addDownloadButton('Download PO Form',
        """/formulamanager.executeformula/${templateLogicName}?templateName=${publishingTemplate}&output=xls&fileName=${filename}""",
        api.jsonEncode(parameters)?.toString())

return controller