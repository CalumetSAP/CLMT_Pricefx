import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

List<String> secondaryKeys = []

def conditionRecords = out.LoadConditionRecords

def fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy")
def conditionTypesMap = [
        "ZPFX": "1",
        "ZCSP": "2",
        "ZBPL": "3",
]

def quotes = api.global.quotes ?: [:]

def key, conditionType, quoteData, secondaryKey
conditionRecords.each { row ->
    key = row.ContractNr + "|" + row.ContractLine
    conditionType = conditionTypesMap[row.ConditionType]

    quoteData = quotes[key]
    if (!quoteData) return

    if (quoteData.FreightTerm == "1") return
    if (quoteData.PriceType != conditionType) return

    secondaryKey = quoteData.SAPContractNumber + "-" + quoteData.SAPLineID + "-" + quoteData.SoldTo + "-" + quoteData.ShipTo + "-" + row.validFrom.format(fmt)
    secondaryKeys.add(secondaryKey)
}

def quotesNotInCRData = out.LoadQuotesNotInCR
List selectedConditionTypes = api.global.conditionType as List ?: ["ZPFX", "ZCSP"]
def possiblePriceTypes = selectedConditionTypes?.collect { conditionTypesMap[it] } ?: []

SimpleDateFormat sdfOutput = new SimpleDateFormat("MM/dd/yyyy")
quotesNotInCRData.each { row ->
    if (!row.PriceType) return
    if (!possiblePriceTypes.contains(row.PriceType)) return

    secondaryKey = row.SAPContractNumber + "-" + row.SAPLineID + "-" + row.SoldTo + "-" + row.ShipTo + "-" + sdfOutput.format(row.PriceValidFrom)
    secondaryKeys.add(secondaryKey)
}

return secondaryKeys