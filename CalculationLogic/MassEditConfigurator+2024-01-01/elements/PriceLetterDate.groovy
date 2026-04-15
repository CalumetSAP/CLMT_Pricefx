Integer priceLetterDayAddition = out.FindPriceLetterDayAddition?.toInteger() ?: 0
Date priceLetterDate = new Date() + priceLetterDayAddition

def entry = libs.BdpLib.UserInputs.createInputDate(
        "PriceLetterDateInput",
        "Price Letter Date",
        true,
        false,
        priceLetterDate
)

return entry