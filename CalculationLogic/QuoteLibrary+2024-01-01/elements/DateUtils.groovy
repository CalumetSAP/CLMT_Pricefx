def getToday() {
    return Calendar.getInstance().getTime()
}

def sumDays(Date date, days) {
    if (!date || !days) return date
    def newDate = Calendar.getInstance()
    newDate.setTime(date)
    newDate.add(Calendar.DAY_OF_MONTH, days)
    return newDate.getTime()
}

Date parseToDate(String date, String format = "yyyy-MM-dd") {
    return Date.parse(format, date)
}