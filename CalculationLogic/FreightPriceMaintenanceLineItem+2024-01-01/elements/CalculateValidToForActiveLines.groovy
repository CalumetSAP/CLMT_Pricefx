import java.text.SimpleDateFormat

if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def secondaryKeys = api.global.matrixSecondayKeys ?: []
    def quotes = api.global.quotes ?: [:]
    def effectiveDate = api.global.effectiveDate as Date
    def expirationDate = api.global.expirationDate as Date
    if (!secondaryKeys || !effectiveDate) {
        api.global.newValidToMap = [:]
    } else {
        SimpleDateFormat keySdf = new SimpleDateFormat("MM/dd/yyyy")

        def grouped = [:].withDefault { [] }

        def parts, groupKey, quotesData
        secondaryKeys?.each { key ->
            parts = key.split("-")
            groupKey = parts[0] + "|" + parts[1]
            quotesData = quotes?.get(groupKey + "|" + parts[4])
            if (!quotesData) return
            grouped[groupKey] << quotesData
        }

        def resultMap = [:]

        grouped.each { key, rows ->
            if (!rows) return

            def sorted = rows.findAll { it.PriceValidFrom }.sort { it.PriceValidFrom }

            def currentLine = sorted
                    .findAll { it.PriceValidFrom <= effectiveDate }
                    ?.max { it.PriceValidFrom }

            def futureLine = sorted
                    .findAll { it.PriceValidFrom > effectiveDate }
                    ?.min { it.PriceValidFrom }

            if (currentLine && futureLine) {
                Date futurePriceFrom     = futureLine.PriceValidFrom as Date

                Date newPriceValidTo     = null

                if (expirationDate && futurePriceFrom && !expirationDate.before(futurePriceFrom)) {
                    Calendar cal = Calendar.getInstance()
                    cal.time = futurePriceFrom
                    cal.add(Calendar.DAY_OF_MONTH, -1)
                    newPriceValidTo = cal.time
                }

                if (newPriceValidTo) {
                    def keyForCurrent = currentLine.SAPContractNumber + "|" +
                            currentLine.SAPLineID + "|" +
                            keySdf.format(currentLine.PriceValidFrom)

                    resultMap[keyForCurrent] = [
                            NewPriceValidTo   : newPriceValidTo
                    ]
                }
            }
        }

        api.global.newValidToMap = resultMap ?: [:]
    }
}

return api.global.newValidToMap[api.local.contractNumber + "|" + api.local.contractLine + "|" + api.local.validFrom] ?: [:]

def getDefaultValidFrom(effectiveDateFromProtection, quotesData, conditionType) {
    if (effectiveDateFromProtection) return effectiveDateFromProtection

    def priceType = quotesData?.PriceType
    def recalculationDate = quotesData?.RecalculationDate
    def recalculationPeriod = quotesData?.RecalculationPeriod

    def conditionTypesMap = [
            "1": "ZPFX",
            "2": "ZCSP",
            "3": "ZBPL",
    ]

    def condType = conditionTypesMap[priceType] ?: conditionType

    return condType == "ZPFX" ? libs.PricelistLib.Index.getRecalculationDate(api.global.effectiveDate, recalculationDate, recalculationPeriod) : out.CalculateEffectiveDateForProtection
}