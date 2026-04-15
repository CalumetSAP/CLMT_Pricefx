import java.text.SimpleDateFormat
import java.time.LocalDate

if (api.global.isFirstRow) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().productExtensionRows("TruckRateUpload")
    def filters = [
            t1.VehicleGroup.isNotNull(),
            t1."Valid From".isNotNull(),
            t1.OriginPostalCode.isNotNull()
    ]
    if (api.global.modeOfTransportation) filters.add(t1.ShippingType.equal(api.global.modeOfTransportation as String))
    if (api.global.meansOfTransportation) filters.add(t1.VehicleGroup.in(api.global.meansOfTransportation as List))

    def filter = qapi.exprs().and(*filters)
    def fields = [t1.Carrier, t1."Valid From", t1.ValidTo, t1.VehicleGroup, t1.Consignee, t1.OriginPostalCode, t1.DestinationPostalCode, t1.Rate,
                  t1.DestinationCountry, t1.RouteMiles, t1.CarrierSCAC, t1.CarrierDescription, t1.Match, t1.RateCurrency]

    def rows = qapi.source(t1, fields, filter).stream { it.collect { it } } ?: []

    def firstPart
    api.global.truckList = rows.collect {
        firstPart = it.VehicleGroup + "|" + it.OriginPostalCode
        it.Consignee ? (firstPart + "|" + getLast6(it.Consignee as String)) : (firstPart + "|" + it.DestinationPostalCode)
    }?.toSet()?.toList() ?: []

    def truckRateUploadMap = [:]

    def destination
    rows.each {
        destination = it.Consignee ? getLast6(it.Consignee as String) : it.DestinationPostalCode
        if (!destination) return

        if (!truckRateUploadMap.containsKey(it.VehicleGroup)) truckRateUploadMap[it.VehicleGroup] = [:]
        if (!truckRateUploadMap[it.VehicleGroup].containsKey(it.OriginPostalCode)) truckRateUploadMap[it.VehicleGroup][it.OriginPostalCode] = [:]
        if (!truckRateUploadMap[it.VehicleGroup][it.OriginPostalCode].containsKey(destination)) truckRateUploadMap[it.VehicleGroup][it.OriginPostalCode][destination] = []
        truckRateUploadMap[it.VehicleGroup][it.OriginPostalCode][destination].add(it)
    }

    api.global.truckRateUpload = truckRateUploadMap
    api.global.truckRateUploadCache = [:]
}

def vehicleGroup = out.LoadQuotes.MeansOfTransportation ?: ""
def shipTo = out.LoadQuotes.ShipTo ?: ""
def origin = out.LoadShippingPointCPT.ZIP ?: ""
def isConsignee = api.global.truckList?.contains(vehicleGroup + "|" + origin + "|" + shipTo)

def destination = isConsignee ? shipTo : (out.LoadCustomer.PostalCode ?: "")

def sdf = new SimpleDateFormat("yyyy-MM-dd")

String manualOverride = api.getManualOverride("NewFreightValidFrom")
Date validFrom = manualOverride ? sdf.parse(manualOverride) : getDefaultValidFrom(out.CalculateEffectiveDateForProtection, out.LoadQuotes, out.LoadConditionRecords.ConditionType)

def validFromKey = validFrom ? validFrom.format("yyyy-MM-dd") : ""
def cacheKey = vehicleGroup + "|" + origin + "|" + destination + "|" + validFromKey

if (api.global.truckRateUploadCache.containsKey(cacheKey)) return api.global.truckRateUploadCache[cacheKey]

List truckRows = api.global.truckRateUpload[vehicleGroup]?.get(origin)?.get(destination) ?: []

def data = getData(truckRows, isConsignee, out.LoadCustomer.Country, validFrom)

api.global.truckRateUploadCache[cacheKey] = data

return data

String getLast6(String value) {
    if (!value) return null
    int n = 6
    int len = value.length()
    return value.substring(Math.max(0, len - n), len)
}

def getData(List rows, boolean isConsignee, country, validFrom) {
    if (!rows || !validFrom) return [
            DestinationCountry: country,
            RateRows          : null,
            Consignee         : isConsignee ? "Y" : "N",
            Miles             : null,
    ]

    Calendar cal = Calendar.getInstance()
    cal.setTime(validFrom)

    LocalDate validFromLocalDate = LocalDate.of(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
    )

    List filtered = rows.findAll { r ->
        r."Valid From" && r.ValidTo && (r."Valid From" <= validFromLocalDate && r.ValidTo >= validFromLocalDate)
    }

    if (!filtered) {
        return [
                DestinationCountry: country,
                RateRows          : null,
                Consignee         : isConsignee ? "Y" : "N",
                Miles             : null,
        ]
    }

    def latestPerCarrier = filtered
            .groupBy { it.Carrier }
            .collect { carrier, lst ->
                lst.findAll { it."Valid From" != null }
                        .max { it."Valid From" } ?: lst[0]
            }

    def rateRows = latestPerCarrier.findAll { it.Rate != null }

    def routeMiles = rows.find { it.RouteMiles != null }?.RouteMiles

    return  [
            DestinationCountry: country,
            RateRows          : rateRows,
            Consignee         : isConsignee ? "Y" : "N",
            Miles             : routeMiles,
    ]
}

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