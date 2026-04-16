import java.text.SimpleDateFormat

def announcementDate = api.jsonDecode(api.currentItem()?.configuration)?.formulaParameters?.Inputs?.AnnouncementDateInput
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

return api.inputBuilderFactory()
        .createDateUserEntry("AnnouncementDateHeaderInput")
        .setLabel("Announcement Date")
        .setReadOnly(true)
        .setValue(sdf.parse(announcementDate))
        .getInput()