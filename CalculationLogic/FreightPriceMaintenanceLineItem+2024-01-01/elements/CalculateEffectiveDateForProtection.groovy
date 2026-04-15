if (out.LoadQuotes.PriceType == "1") return null

Date effectiveDate = api.global.effectiveDate
def protectionData = out.LoadExclusions
String protection = protectionData?.attribute2
if (protection) {
    Integer noOfDays = protectionData?.attribute1?.toInteger()
    switch (protection) {
        case "Announcement Date":
            effectiveDate = getDateAddingNoOfDate(effectiveDate, noOfDays)
            break
        case "Effective Date":
            effectiveDate = getDateAddingNoOfDate(effectiveDate, noOfDays)
            break
        case "Specified Day":
            effectiveDate = getDateWithSpecifiedDayProtection(effectiveDate, protectionData?.attribute3, protectionData?.attribute4?.toInteger(),
                    protectionData?.attribute5?.toInteger())
            break
        case "Price Letter Date":
            effectiveDate = getDateAddingNoOfDate(effectiveDate, noOfDays)
            break
    }
}

return effectiveDate

Date getDateAddingNoOfDate (Date date, Integer noOfDays) {
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(date)
    if (noOfDays) {
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays)
    }
    return calendar.getTime()
}

Date getDateWithSpecifiedDayProtection (Date effectiveDate, String movementTiming, Integer movementStartMonth, Integer movementDay) {
    Date resultingDate = effectiveDate
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(effectiveDate)
    switch (movementTiming) {
        case "Month":
            //If the movement day is greater or equal, don't change the month number. Otherwise, increase month by one
            if (movementDay >= calendar.get(Calendar.DAY_OF_MONTH)) {
                calendar.set(Calendar.DAY_OF_MONTH, movementDay)
                resultingDate = calendar.getTime()
            } else {
                calendar.add(Calendar.MONTH, 1)
                calendar.set(Calendar.DAY_OF_MONTH, movementDay)
                resultingDate = calendar.getTime()
            }
            break
        case "Quarter":
            Integer month = calendar.get(Calendar.MONTH) + 1
            Integer dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            //movementStartMonth will always be between 1 to 3 (1 -> first quarter, 2 -> second quarter, 3 -> third quarter)
            List<Integer> quarterMonths = [
                    movementStartMonth,
                    movementStartMonth + 3,
                    movementStartMonth + 6,
                    movementStartMonth + 9
            ]

            if (quarterMonths.contains(month) && movementDay >= dayOfMonth) {
                calendar.set(Calendar.DAY_OF_MONTH, movementDay)
                resultingDate = calendar.getTime()
            } else {
                if (month < quarterMonths[0]) {
                    calendar.set(Calendar.MONTH, quarterMonths[0] - 1)
                    calendar.set(Calendar.DAY_OF_MONTH, movementDay)
                    resultingDate = calendar.getTime()
                } else {
                    boolean found = false

                    for (int i = 0; i < quarterMonths.size() - 1; i++) {
                        if (month >= quarterMonths[i] && month < quarterMonths[i + 1]) {
                            calendar.set(Calendar.MONTH, quarterMonths[i + 1] - 1)
                            calendar.set(Calendar.DAY_OF_MONTH, movementDay)
                            resultingDate = calendar.getTime()
                            found = true
                            break
                        }
                    }

                    if (!found) {
                        calendar.add(Calendar.YEAR, 1)
                        calendar.set(Calendar.MONTH, quarterMonths[0] - 1)
                        calendar.set(Calendar.DAY_OF_MONTH, movementDay)
                        resultingDate = calendar.getTime()
                    }
                }
            }
            break
    }

    return resultingDate
}