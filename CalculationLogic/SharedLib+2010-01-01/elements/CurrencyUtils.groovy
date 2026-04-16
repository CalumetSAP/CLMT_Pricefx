import groovy.transform.Field
import net.pricefx.formulaengine.DatamartContext

@Field Map CCY_DATASOURCE = [NAME        : "ccy",
                             DATE_PATTERN: "yyyy-MM-dd",
                             SOURCE_FIELD: [CCY_FROM         : "CcyFrom",
                                            CCY_TO           : "CcyTo",
                                            CCY_VALID_FROM   : "CcyValidFrom",
                                            CCY_VALID_TO     : "CcyValidTo",
                                            CCY_EXCHANGE_RATE: "CcyExchangeRate"]]
@Field String EXCHANGE_RATE_NOT_FOUND = "EXCHANGE_RATE_NOT_FOUND"

/**
 * Retrieves the main currency code for a given datamart.
 * @param datamartName name of the datamart
 * @return currency code that is defined for a given datamart
 */
String getDatamartCurrencyCode(String datamartName) {
    return api.find("DM", Filter.equal("uniqueName", datamartName))?.getAt(0)?.baseCcyCode
}

/**
 * Return exchange rate from ccyFrom to ccyTo for specified date from datasource ccy
 * If result have more one valid exchange rate, we get the exchange rate with valid from date is maximum
 * If result is empty/null (no data), throw exception EXCHANGE_RATE_NOT_FOUND
 * @param ccyFrom
 * @param ccyTo
 * @param date
 * @return BigDecimal
 */
BigDecimal getExchangeRate(String ccyFrom, String ccyTo, Date date = new Date()) {
    if (ccyFrom == ccyTo) {
        return 1
    }

    String formattedDate = date.format(CCY_DATASOURCE.DATE_PATTERN)
    DatamartContext datamartContext = api.getDatamartContext()
    def dataSourceConnection = datamartContext.getDataSource(CCY_DATASOURCE.NAME)
    DatamartContext.Query query = datamartContext.newQuery(dataSourceConnection, false)
    Map sourceField = CCY_DATASOURCE.SOURCE_FIELD

    query.select(sourceField.CCY_EXCHANGE_RATE)
    query.where(Filter.equal(sourceField.CCY_FROM, ccyFrom),
            Filter.equal(sourceField.CCY_TO, ccyTo),
            Filter.lessThan(sourceField.CCY_VALID_FROM, formattedDate),
            Filter.greaterOrEqual(sourceField.CCY_VALID_TO, formattedDate))
    query.orderBy("${sourceField.CCY_VALID_FROM} DESC")

    List data = datamartContext.executeQuery(query)?.getData().collect()

    if (!data) {
        api.throwException("${EXCHANGE_RATE_NOT_FOUND}|No exchange rate: From:${ccyFrom}, To:${ccyTo}, Date:${formattedDate}")
    }

    return data.getAt(0).getAt(sourceField.CCY_EXCHANGE_RATE)
}

/**
 * Retrieves the list of all available currencies based on currencyCode and validDate.
 * If all parameters are null, return all ccyTo in datasource, other cases return ccyTo based on parameter inputs
 * @param currencyCode - currency code from
 * @param validDate - valid date
 * @return List<String>
 */
List getAvailableCurrencies(String currencyCode, Date validDate) {
    Map sourceField = CCY_DATASOURCE.SOURCE_FIELD
    DatamartContext datamartContext = api.getDatamartContext()
    def dataSourceConnection = datamartContext.getDataSource(CCY_DATASOURCE.NAME)
    DatamartContext.Query query = datamartContext.newQuery(dataSourceConnection, true)
    List filters = []

    if (currencyCode) {
        filters.add(Filter.equal(sourceField.CCY_FROM, currencyCode))
    }

    if (validDate) {
        String formattedValidFrom = validDate.format(CCY_DATASOURCE.DATE_PATTERN)
        filters.add(Filter.lessThan(sourceField.CCY_VALID_FROM, formattedValidFrom))
        filters.add(Filter.greaterOrEqual(sourceField.CCY_VALID_TO, formattedValidFrom))
    }

    query.select(sourceField.CCY_TO)
    query.where(*filters)
    query.orderBy("${sourceField.CCY_TO}")

    return datamartContext.executeQuery(query)?.getData().getColumnValues(0)
}
