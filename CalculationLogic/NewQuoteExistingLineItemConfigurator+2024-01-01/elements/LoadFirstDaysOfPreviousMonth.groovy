//if (api.isInputGenerationExecution()) return

//final indexLib = libs.PricelistLib.Index
//
//def indexValues = PFIndexNumber?.entry?.getFirstInput()?.getValue()
//def selectedReferencePeriod = PFReferencePeriod?.entry?.getFirstInput()?.getValue()
//def priceType = InputPriceType?.input?.getValue()
//
//if(indexLib.validateOnloadingOfDaysFilters(priceType, indexValues, selectedReferencePeriod, "11")) return
//
//Date calculationDate = libs.QuoteLibrary.DateUtils.getToday()
//def sortBy = "key3"
//
//return indexLib.getIndexGroupedByKey(indexValues, indexLib.getPreviousMonthAverageFilters(calculationDate), sortBy)
