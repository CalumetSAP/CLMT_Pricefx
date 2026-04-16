def getESTTimeZoneDate(calendar, date) {

	calendar?.setTime(date)

	def dateTime = new org.joda.time.DateTime(calendar)

	def estTimeZone = api.getTimeZone("EST5EDT")
	def estDate = dateTime.withZone(estTimeZone)

	return estDate.withTimeAtStartOfDay().toDate()
}