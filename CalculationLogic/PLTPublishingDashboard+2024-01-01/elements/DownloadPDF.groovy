import net.pricefx.server.dto.calculation.DashboardController
import java.text.SimpleDateFormat

def constants = libs.DashboardConstantsLibrary.PLTDashboard

def pricelistFooters = api.global.pricelistFooters ?: [:]

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

def templateLogicName = "PublishingPDFPriceLetterPLT"
def publishingTemplate = out.LoadPricelist?.UseWatermark ? "Price Letter With Watermark - PLT" : "Price Letter - PLT"
def filename = "Price_Letter_PLT_" + formattedDate // date of run
List<Map<String, Object>> rows = api.local.exportPdf

def configurator = out.Filters
def pricingDate = configurator?.get(constants.PRICING_DATE_INPUT_KEY)
def showJobbers = configurator?.get(constants.SHOW_JOBBER_SRP_MAP_INPUT_KEY)
def pricelist = out.Filters?.get(constants.PRICELIST_INPUT_KEY)
def sorting = out.Filters?.get(constants.SORTING_INPUT_KEY)
def title = out.LoadPricelist?.Name ?: ""
def footer = pricelistFooters?.get(pricelist)

Map parameters = [
        "pricingDate"        : createCalendarFromDateString(pricingDate),
        "rows"               : rows,
        "hasItemsIncluded"   : api.local.hasItemIncluded,
        "hasLegacyPartNo"    : api.local.hasLegacyPartNumber,
        "hasJobbers"         : api.local.hasJobbbers,
        "showJobbers"        : showJobbers,
        "title"              : title,
        "currency"           : api.local.currency,
        "footer"             : footer,
        "sorting"            : sorting
]

controller.addDownloadButton('Download PDF',
        """/formulamanager.executeformula/${templateLogicName}?templateName=${publishingTemplate}&output=pdf&fileName=${filename}""",
        api.jsonEncode(parameters)?.toString())

return controller