Calendar calendar = Calendar.getInstance()
calendar.setTime(api.global.calculationDate as Date)
calendar.add(Calendar.YEAR, 1)

return calendar.getTime()