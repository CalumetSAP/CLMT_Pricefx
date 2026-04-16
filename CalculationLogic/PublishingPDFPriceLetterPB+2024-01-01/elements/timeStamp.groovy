import java.text.SimpleDateFormat

def calendar = Calendar.getInstance()

calendar?.setTime(calendar?.getTime())

def dateTime = new org.joda.time.DateTime(calendar)

def estTimeZone = api.getTimeZone("America/New_York")

def estDate = dateTime.withZone(estTimeZone)

def inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
def outputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm")

return outputFormat.format(inputFormat.parse(estDate.toDateTime().toString()))