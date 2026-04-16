//if (api.isInputGenerationExecution()) return
//
//def priceType = InputPriceType?.input?.getValue()
//def referencePeriod = PFReferencePeriod?.entry?.getFirstInput()?.getValue()
//
//if (priceType != "1" || !(api.local.indexHasChanged || api.local.priceHasChanged || api.local.adderHasChanged || api.local.freightAmountHasChanged) || !referencePeriod) return
//
//final indexLib = libs.PricelistLib.Index
//Date calculationDate = libs.QuoteLibrary.DateUtils.getToday()
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