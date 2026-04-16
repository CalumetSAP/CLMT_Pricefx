if (api.global.isFirstRow) {
    final indexLib = libs.PricelistLib.Index
    Date calculationDate = api.global.calculationDate
    LinkedHashMap<String, List> referencePeriodDateFilter = [
            "1": indexLib.getPreviousMonthAverageFilters(calculationDate),    //Previous month average
            "2": indexLib.getMidMonthAverageFilters(calculationDate),         //Mid-month average 16-15th
            "3": indexLib.getPreviousQuarterAverageFilters(calculationDate),  //Previous quarter average
            "4": indexLib.getRollingQuarterAverageFilters(calculationDate),   //Rolling quarter average
            //5 and 6 and 11 is "LoadLastDaysOfPreviousMonth" and "LoadLastDaysOfPreviousQuarter" and "LoadFirstsDaysOfPreviousMonth"
            "7": indexLib.getThirdWednesdayOfMonthFilters(calculationDate),   //3rd Wednesday of the month
            "8": indexLib.getFourthWednesdayOfMonthFilters(calculationDate),  //4th Wednesday of the month
            "9": indexLib.getMidMonthAverageFilters(calculationDate, 21, 20), //Mid-month average 21-20th
            "10": indexLib.getMidMonthAverageFilters(calculationDate, 26, 25) //Mid-month average 26-25th
    ]
    api.global.referencePeriodDateFilter = referencePeriodDateFilter
}

return api.global.referencePeriodDateFilter[api.local.referencePeriod] ?: []