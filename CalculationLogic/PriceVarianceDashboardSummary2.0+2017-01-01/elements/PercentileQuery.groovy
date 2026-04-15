import net.pricefx.formulaengine.TableContext
import net.pricefx.formulaengine.scripting.Matrix2D

import java.math.RoundingMode

if (api.isSyntaxCheck()) return
if (api.global.pricePointField == null) return

api.global.percetileCalculatedList = []

TableContext tableContext2 = api.global.tableCtx

def calculateExcelStylePercentile(List<BigDecimal> values, double percentile) {
    if (values.isEmpty()) return null
    values.sort()

    double k = (values.size() - 1) * percentile + 1
    int kFloor = Math.floor(k).toInteger()
    int kCeil = Math.ceil(k).toInteger()

    if (k == kFloor) {
        return values[kFloor - 1]
    } else {
        BigDecimal floorValue = values[kFloor - 1]
        BigDecimal ceilValue = values[kCeil - 1]
        return floorValue + (ceilValue - floorValue) * BigDecimal.valueOf(k - kFloor)
    }
}

def calculateTotalOpportunity(List<Map> productData, BigDecimal percentileValue, BigDecimal sumPricepointPercentBasis,
                              BigDecimal sumPricepoint, String likeProduct) {
    if (productData.isEmpty() || percentileValue == null) return BigDecimal.ZERO

    productData.sum { transaction ->
        def price = transaction['price'] as BigDecimal
        def quantity = transaction['invoice_base_qty'] as BigDecimal
        if(api.global.numericPricePoint){
            if (price < percentileValue && quantity != null) {
                (percentileValue - price) * quantity
            } else {
                BigDecimal.ZERO
            }
        }else{
            if (sumPricepointPercentBasis > 0 && (percentileValue - sumPricepoint/sumPricepointPercentBasis) > 0) {
                (percentileValue - sumPricepoint/sumPricepointPercentBasis) * sumPricepoint
            } else {
                BigDecimal.ZERO
            }
        }
    }
}

def calculateStandardDeviation(List<BigDecimal> values, BigDecimal mean) {
    if (values.isEmpty() || mean == null || values.size() == 1) return null
    BigDecimal sumOfSquaredDifferences = values.sum { value ->
        (value - mean) ** 2
    }
    BigDecimal variance = sumOfSquaredDifferences / BigDecimal.valueOf(values.size() - 1)
    BigDecimal.valueOf(Math.sqrt(variance.doubleValue()))
}

if(api.global.numericPricePoint){
    api.global.queryPercentile = "SELECT like_product, price_point/invoice_base_qty AS price, invoice_base_qty, sold_account_number, site_name, price_point AS price_point_selected, price_point_percent_basis, transaction_id FROM transactions"
}else{
    api.global.queryPercentile = "SELECT like_product, price_point/price_point_percent_basis AS price, invoice_base_qty, sold_account_number, site_name, price_point AS price_point_selected, price_point_percent_basis, transaction_id FROM transactions"
}

def allData = tableContext2.executeQuery(api.global.queryPercentile).toResultMatrix()

def groupedData = allData.getEntries().groupBy { it['like_product'] }

groupedData.each { productName, productData ->
    List<BigDecimal> prices = productData.collect { it['price'] as BigDecimal }.findAll { it != null }

    def min = prices ? prices.min()?.setScale(5, RoundingMode.HALF_DOWN) : null
    def max = prices ? prices.max()?.setScale(5, RoundingMode.HALF_DOWN) : null
    def percentile10 = prices ? BigDecimal.valueOf(calculateExcelStylePercentile(prices, 0.10)).setScale(5, RoundingMode.HALF_DOWN) : null
    def percentile25 = prices ? BigDecimal.valueOf(calculateExcelStylePercentile(prices, 0.25))?.setScale(5, RoundingMode.HALF_DOWN) : null
    def percentile50 = prices ? BigDecimal.valueOf(calculateExcelStylePercentile(prices, 0.50))?.setScale(5, RoundingMode.HALF_DOWN) : null
    def percentile75 = prices ? BigDecimal.valueOf(calculateExcelStylePercentile(prices, 0.75))?.setScale(5, RoundingMode.HALF_DOWN) : null
    def percentile90 = prices ? BigDecimal.valueOf(calculateExcelStylePercentile(prices, 0.90))?.setScale(5, RoundingMode.HALF_DOWN) : null

    def soldToList = productData.collect { it['sold_account_number'] }?.unique()?.size() ?: 0
    def siteNameList = productData.collect { it['site_name'] }?.unique()?.size() ?: 0

    def avg = prices ? prices.sum() / prices.size() : null
    def sum = productData.collect { it['price_point_selected'] as BigDecimal }?.sum() ?: BigDecimal.ZERO
    def sumPercentBasis = productData.collect { it['price_point_percent_basis'] as BigDecimal }?.sum() ?: BigDecimal.ZERO
    def std = prices ? calculateStandardDeviation(prices, avg) : null
    def cov = (avg != null && avg != 0 && std != null) ? (std / avg) * 100 : null

    def variation = cov != null ? (cov < 10 ? "LOW" : (cov < 60 ? "MEDIUM" : "HIGH")) : null

//    def totalOpportunity10 = calculateTotalOpportunity(productData, percentile10, sumPercentBasis, sum, productName)
//    def totalOpportunity25 = calculateTotalOpportunity(productData, percentile25, sumPercentBasis, sum, productName)
//    def totalOpportunity50 = calculateTotalOpportunity(productData, percentile50, sumPercentBasis, sum, productName)
//    def totalOpportunity75 = calculateTotalOpportunity(productData, percentile75, sumPercentBasis, sum, productName)
//    def totalOpportunity90 = calculateTotalOpportunity(productData, percentile90, sumPercentBasis, sum, productName)
    def totalOpportunity10 = 0
    def totalOpportunity25 = 0
    def totalOpportunity50 = 0
    def totalOpportunity75 = 0
    def totalOpportunity90 = 0

    productData.each { transaction ->
        def quantity = transaction.get("invoice_base_qty")
        def pricePoint = transaction.get("price")
        def pricePointPercentBasis = transaction.get("price_point_percent_basis")

        def opp10 = api.global.numericPricePoint ? (pricePoint < percentile10
                ? (percentile10 - pricePoint) * quantity
                : 0) :
                (
                        (pricePointPercentBasis > 0 && (percentile10 - pricePoint/pricePointPercentBasis) > 0) ?
                                (percentile10 - pricePoint) * (pricePoint * pricePointPercentBasis)
                        : 0)

        def opp25 = api.global.numericPricePoint ? (pricePoint < percentile25
                ? (percentile25 - pricePoint) * quantity
                : 0) :
                (
                        (pricePointPercentBasis > 0 && (percentile25 - pricePoint) > 0) ?
                                (percentile25 - pricePoint) * (pricePoint * pricePointPercentBasis)
                                : 0)

        def opp50 = api.global.numericPricePoint ? (pricePoint < percentile50
                ? (percentile50 - pricePoint) * quantity
                : 0) :
                (
                        (pricePointPercentBasis > 0 && (percentile50 - pricePoint) > 0) ?
                                (percentile50 - pricePoint) * (pricePoint * pricePointPercentBasis)
                                : 0)

        def opp75 = api.global.numericPricePoint ? (pricePoint < percentile75
                ? (percentile75 - pricePoint) * quantity
                : 0) :
                (
                        (pricePointPercentBasis > 0 && (percentile75 - pricePoint) > 0) ?
                                (percentile75 - pricePoint) * (pricePoint * pricePointPercentBasis)
                                : 0)

        def opp90 = api.global.numericPricePoint ? (pricePoint < percentile90
                ? (percentile90 - pricePoint) * quantity
                : 0) :
                (
                        (pricePointPercentBasis > 0 && (percentile90 - pricePoint) > 0) ?
                                (percentile90 - pricePoint) * (pricePoint * pricePointPercentBasis)
                                : 0)

//        Acelerator.getElementsByType("tableColDyn")?.each {
//            if (it.hide == 'No') {
//                api.local['total_' + it.element] = api.local['total_' + it.element] + (transaction.get(it.element) ?: 0)
//            }
//        }

        totalOpportunity10 += opp10?.toBigDecimal()
        totalOpportunity25 += opp25?.toBigDecimal()
        totalOpportunity50 += opp50?.toBigDecimal()
        totalOpportunity75 += opp75?.toBigDecimal()
        totalOpportunity90 += opp90?.toBigDecimal()

//        totalPricePointSelected += pricePointSelected?.toBigDecimal()
//        totalQuantity += quantity?.toBigDecimal()

    }

    def percentileMap = [
            "like_product": productName,
            "min": min,
            "max": max,
            "percentile10": percentile10,
            "percentile25": percentile25,
            "percentile50": percentile50,
            "percentile75": percentile75,
            "percentile90": percentile90,
            "sold_to": soldToList,
            "site_name": siteNameList,
            "avg": avg,
            "sum": sum,
            "sum_percent_basis": sumPercentBasis,
            "std": std,
            "cov": cov,
            "variation": variation,
            "opportunity10": totalOpportunity10,
            "opportunity25": totalOpportunity25,
            "opportunity50": totalOpportunity50,
            "opportunity75": totalOpportunity75,
            "opportunity90": totalOpportunity90,
    ]

    api.global.percetileCalculatedList.add(percentileMap)
}

TableContext tableContext = api.getTableContext()

def columns = [
        "like_product" : DataType.STRING,
        "percentile10" : DataType.NUMBER,
        "percentile25" : DataType.NUMBER,
        "percentile50" : DataType.NUMBER,
        "percentile75" : DataType.NUMBER,
        "percentile90" : DataType.NUMBER,
        "opportunity10": DataType.NUMBER,
        "opportunity25": DataType.NUMBER,
        "opportunity50": DataType.NUMBER,
        "opportunity75": DataType.NUMBER,
        "opportunity90": DataType.NUMBER,
        "sold_to"      : DataType.NUMBER,
        "site_name"    : DataType.NUMBER,
        "min"          : DataType.NUMBER,
        "std"          : DataType.NUMBER,
        "max"          : DataType.NUMBER,
        "cov"          : DataType.NUMBER,
        "avg"          : DataType.NUMBER,
        "sum"          : DataType.NUMBER,
        "sum_percent_basis"          : DataType.NUMBER,
        "variation"    : DataType.STRING,
]

tableContext.createTable("percentile", columns)

tableContext.loadRows("percentile", api.global.percetileCalculatedList)
api.global.summay = tableContext
