if (api.isInputGenerationExecution() || !quoteProcessor.isPrePhase()) return

def indexValuesList = api.local.indexValuesList
//def priceType = InputPriceType?.input?.getValue()
//def referencePeriod = PFReferencePeriod?.entry?.getFirstInput()?.getValue()

if (!indexValuesList) return

final indexLib = libs.PricelistLib.Index
Date calculationDate = libs.QuoteLibrary.DateUtils.getToday()
//
//switch (referencePeriod) {
//    case "1":
//        return indexLib.getPreviousMonthAverageFilters(calculationDate)
//    case "2":
//        return indexLib.getMidMonthAverageFilters(calculationDate)
//    case "3":
//        return indexLib.getPreviousQuarterAverageFilters(calculationDate)
//    case "4":
//        return indexLib.getRollingQuarterAverageFilters(calculationDate)
//    case "7":
//        return indexLib.getThirdWednesdayOfMonthFilters(calculationDate)
//    case "8":
//        return indexLib.getFourthWednesdayOfMonthFilters(calculationDate)
//    case "9":
//        return indexLib.getMidMonthAverageFilters(calculationDate, 21, 20)
//    case "10":
//        return indexLib.getMidMonthAverageFilters(calculationDate, 26, 25)
//    default:
//        return null
//}

return [
        "1" : indexLib.getPreviousMonthAverageFilters(calculationDate),
        "2" : indexLib.getMidMonthAverageFilters(calculationDate),
        "3" : indexLib.getPreviousQuarterAverageFilters(calculationDate),
        "4" : indexLib.getRollingQuarterAverageFilters(calculationDate),
        "7" : indexLib.getThirdWednesdayOfMonthFilters(calculationDate),
        "8" : indexLib.getFourthWednesdayOfMonthFilters(calculationDate),
        "9" : indexLib.getMidMonthAverageFilters(calculationDate, 21, 20),
        "10": indexLib.getMidMonthAverageFilters(calculationDate, 26, 25),
]