import java.text.SimpleDateFormat

def effectiveDateOverride = api.getManualOverride("EffectiveDate")
if (effectiveDateOverride) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
    return sdf.parse(effectiveDateOverride)
}


Date effectiveDate = api.global.effectiveDate
String protection = out.LoadExclusions?.attribute2
if (protection) {
    Integer noOfDays = out.LoadExclusions?.attribute1?.toInteger()
    switch (protection) {
        case "Announcement Date":
            effectiveDate = getDateAddingNoOfDate(api.global.announcementDate, noOfDays)
            break
        case "Effective Date":
            effectiveDate = getDateAddingNoOfDate(effectiveDate, noOfDays)
            break
        case "Specified Day":
            effectiveDate = getDateWithSpecifiedDayProtection(effectiveDate, out.LoadExclusions?.attribute3, out.LoadExclusions?.attribute4?.toInteger(), out.LoadExclusions?.attribute5?.toInteger())
            break
        case "Price Letter Date":
            effectiveDate = getDateAddingNoOfDate(api.global.priceLetterDate, noOfDays)
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
            Integer month = calendar.get(Calendar.MONTH)+1
            //movementStartMonth will always be between 1 to 3 (1 -> first quarter, 2 -> second quarter, 3 -> third quarter)
            List<Integer> quarterMonths = [movementStartMonth, movementStartMonth+3, movementStartMonth+6, movementStartMonth+9]
            if (quarterMonths.contains(month) && movementDay >= calendar.get(Calendar.DAY_OF_MONTH)) {
                calendar.set(Calendar.DAY_OF_MONTH, movementDay)
                resultingDate = calendar.getTime()
            } else {
                for (int i = 0; i < 4; i++) {
                    if (i == 3) { //When the loop reaches the last index that means I have to go to the first quarter of the next year
                        calendar.add(Calendar.YEAR, 1)
                        calendar.set(Calendar.MONTH, quarterMonths[0]-1) //The "-1" is because months go from 0 to 11
                        calendar.set(Calendar.DAY_OF_MONTH, movementDay)
                        resultingDate = calendar.getTime()
                    } else if (month >= quarterMonths[i] && month < quarterMonths[i+1]) {
                        calendar.set(Calendar.MONTH, quarterMonths[i+1]-1)
                        calendar.set(Calendar.DAY_OF_MONTH, movementDay)
                        resultingDate = calendar.getTime()
                        break
                    }
                }
            }
            break
    }

    return resultingDate
}