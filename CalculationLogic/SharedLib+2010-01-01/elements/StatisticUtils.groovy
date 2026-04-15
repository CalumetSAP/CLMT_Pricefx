/**
 * Get median from list of numbers
 * @param items. List of number
 * @return the median value from the list
 * */
Number getMedian(List<Number> items) {
    if (!items) {
        return
    }
    items = items.sort()
    def size = items?.size
    def modulus = size % 2
    def median
    if (modulus == 0) {
        def firstIdx = (size / 2) as int
        def secondIdx = (firstIdx - 1) as int
        median = (items.get(firstIdx) + items.get(secondIdx)) / 2
    } else {
        median = items.get((((size - 1) / 2) as int))
    }

    return median
}

/**
 * Get the trend line data points for a given list of series data points.
 * @param points List the series to draw out the trend line data points.
 * The list format: [[x:x1,y:y1],[x:x2,y:y2],...]
 * @return List a list contains the max and min points coordinate of the trend line
 * with the following order: [min point, max point]
 * Each point coordinate has the following format: [x: some value, y: some value]
 */
List generateRegressionLineData(List points) {
    Map regressionLineCoefficients = calculateRegressionCoefficients(points)

    if (!regressionLineCoefficients) {
        return []
    }

    Map maxPointData = retrieveMaxXAxisPoint(points)
    List maxPointRegressionData = calculateRegressionPoint(maxPointData, regressionLineCoefficients)
    Map minPointData = retrieveMinXAxisPoint(points)
    List minPointRegressionData = calculateRegressionPoint(minPointData, regressionLineCoefficients)

    return [minPointRegressionData, maxPointRegressionData]
}

/**
 * Calculate the coefficients for a trend line, which are the slope and yIntercept.
 * For more info, the formula used to calculate any trend line point is y = slope*x + yIntercept.
 * @param points List the list of points to draw out the trend line
 * @return Map the calculated coefficients with the following format:
 * [slope: some value, yIntercept: some value]
 */
Map calculateRegressionCoefficients(List points) {
    if (!points || points.size() < 2) {
        return [:]
    }

    BigDecimal sumX = 0
    BigDecimal sumY = 0
    BigDecimal sumXY = 0
    BigDecimal sumXX = 0

    points.each { Map pointData ->
        sumX += pointData.x
        sumY += pointData.y
        sumXY += pointData.x * pointData.y
        sumXX += pointData.x * pointData.x
    }

    int pointCount = points.size()
    BigDecimal denominator = pointCount * sumXX - sumX * sumX

    if (!denominator) {
        return [:]
    }

    BigDecimal slope = (pointCount * sumXY - sumX * sumY) / denominator
    BigDecimal yIntercept = (sumY * sumXX - sumX * sumXY) / denominator

    return [slope     : slope,
            yIntercept: yIntercept]
}

/**
 * Calculate the coordinate of a trend line point. The formula used to calculate is y = slope*x + yIntercept.
 * Example: given slope = 1.75, yIntercept = 1.3, the equation will be y = 1.75x + 1.3,
 * where x and y are the coordinate of a point on the series that need to draw the trend line.
 * With the coefficients in the example, given a point with x = 2 => y = 1.75*2 + 1.3 = 4.8.
 * Therefore the input point is [x:2, y:any value], the calculated trend line point will be [x:2, y:4.8].
 * @param pointData Map the coordinate of a point on the series that need to draw the trend line with the following format: [x: some value, y: some value]
 * @param regressionLineCoefficients Map the calculated trend line coefficients with the following format: [slope: some value, yIntercept: some value]
 * @return List the coordinate of the trend line point with the following format: [X, Y]
 */
List calculateRegressionPoint(Map pointData, Map regressionLineCoefficients) {
    BigDecimal pointXValue = pointData.x
    BigDecimal pointYValue = calculateRegressionLineYValue(regressionLineCoefficients, pointXValue)

    return [pointXValue, pointYValue]
}

/**
 * Calculate the Y coordinate of a trend line point. The formula used to calculate is y = slope*x + yIntercept.
 * @param regressionLineCoefficients Map the calculated trend line coefficients with the following format: [slope: some value, yIntercept: some value]
 * @param xValue BigDecimal the x coordinate of the series data point
 * @return BigDecimal the y coordinate of the trend line point
 */
BigDecimal calculateRegressionLineYValue(Map regressionLineCoefficients, BigDecimal xValue) {
    return regressionLineCoefficients.slope * xValue + regressionLineCoefficients.yIntercept
}

/**
 * Get the max point on the X axis.
 * @param points List the list of points to get the max point.
 * @return Map the found point coordinate with the format [x: some value, y: some value]
 */
protected Map retrieveMaxXAxisPoint(List points) {
    return points.max { Map point1, Map point2 -> point1.x <=> point2.x }
}

/**
 * Get the min point on the X axis.
 * @param points List the list of points to get the min point.
 * @return Map the found point coordinate with the format [x: some value, y: some value]
 */
protected Map retrieveMinXAxisPoint(List points) {
    return points.min { Map point1, Map point2 -> point1.x <=> point2.x }
}
