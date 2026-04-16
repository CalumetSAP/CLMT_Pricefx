import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    SimpleDateFormat sdfOutput = new SimpleDateFormat("MM/dd/yyyy")
    def conditionTypesMap = [
            "ZPFX": "1",
            "ZCSP": "2",
            "ZBPL": "3",
    ]

    def quotes = api.global.matrixQuotes ?: [:]

    List selectedConditionTypes = api.global.conditionType as List ?: ["ZPFX", "ZCSP"]
    def possiblePriceTypes = selectedConditionTypes?.collect { conditionTypesMap[it] } ?: []

    List<String> secondaryKeys = []

    def conditionRecords, key, conditionType, quoteData, secondaryKey
    def quotesNotInCRData, activeRow, futureRow
    api.global.batchKeys1?.each { sku ->
        conditionRecords = api.global.matrixConditionRecordsByMaterial?.get(sku) ?: []

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

        activeRow = api.global.matrixActiveQuotesNotInCR?.get(sku) ?: []
        futureRow = api.global.matrixFutureQuotesNotInCR?.get(sku) ?: []

        quotesNotInCRData = activeRow + futureRow

        quotesNotInCRData.each { row ->
            if (!row.PriceType) return
            if (!possiblePriceTypes.contains(row.PriceType)) return

            secondaryKey = row.SAPContractNumber + "-" + row.SAPLineID + "-" + row.SoldTo + "-" + row.ShipTo + "-" + sdfOutput.format(row.PriceValidFrom)
            secondaryKeys.add(secondaryKey)
        }
    }

    api.global.matrixSecondayKeys = secondaryKeys
}