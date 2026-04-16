def lastHalfHour = Calendar.getInstance()
lastHalfHour.add(Calendar.MINUTE,-30)
lastHalfHour = lastHalfHour.getTime()

def daysFilter = Filter.greaterOrEqual("lastUpdateDate", lastHalfHour)

return daysFilter