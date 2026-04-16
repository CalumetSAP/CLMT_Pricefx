def quotesRow = out.LoadQuotes

String adderUOM = api.getManualOverride("AdderUOM") ?: quotesRow?.AdderUOM
api.local.adderUOM = adderUOM

def numberOfDecimals = (api.getManualOverride("NumberOfDecimals") ?: quotesRow?.NumberofDecimals) ?: "2"
api.local.numberOfDecimals = numberOfDecimals

def adderOverride = api.getManualOverride("Adder")
BigDecimal adderValue = adderOverride != null ? adderOverride.toBigDecimal() : quotesRow?.Adder?.toBigDecimal()
adderValue = libs.SharedLib.RoundingUtils.round(adderValue, numberOfDecimals.toInteger())
api.local.adder = adderValue

String index1 = api.getManualOverride("Index1") ?: quotesRow?.IndexNumberOne
String index2 = api.getManualOverride("Index2") ?: quotesRow?.IndexNumberTwo
String index3 = api.getManualOverride("Index3") ?: quotesRow?.IndexNumberThree

Integer indexQty = 0
if (index1) indexQty += 1
if (index2) indexQty += 1
if (index3) indexQty += 1

def index1PercentOverride = api.getManualOverride("Index1Percent")
def index2PercentOverride = api.getManualOverride("Index2Percent")
def index3PercentOverride = api.getManualOverride("Index3Percent")
BigDecimal indexPercent = indexQty ? 1/indexQty : 0
api.local.index1Percent = index1 ? (index1PercentOverride != null ? index1PercentOverride : indexPercent) : BigDecimal.ZERO
api.local.index2Percent = index2 ? (index2PercentOverride != null ? index2PercentOverride : indexPercent) : BigDecimal.ZERO
api.local.index3Percent = index3 ? (index3PercentOverride != null ? index3PercentOverride : indexPercent) : BigDecimal.ZERO

api.local.index1 = index1
api.local.index2 = index2
api.local.index3 = index3

String referencePeriodValueDescription = api.getManualOverride("ReferencePeriod")
api.local.referencePeriod = referencePeriodValueDescription ?
        out.LoadReferencePeriodOptions[referencePeriodValueDescription] :
        quotesRow?.ReferencePeriod

return null