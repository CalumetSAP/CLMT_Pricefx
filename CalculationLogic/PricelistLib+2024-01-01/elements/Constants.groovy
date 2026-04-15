import groovy.transform.Field

@Field final String PRICING_FORMULA_PL_TYPE = "PricingFormula"
@Field final String MASS_EDIT_PL_TYPE = "MassEdit"
@Field final String LIST_PRICE_MAINTENANCE_PL_TYPE = "ListPriceMaintenance"
@Field final String LIST_PRICE_ZLIS_PL_TYPE = "ListPriceZLIS"
@Field final String PRICE_LIST_ZBPL_PL_TYPE = "PricelistZBPL"
@Field final String FREIGHT_MAINTENANCE_PL_TYPE = "FreightPriceMaintenance"
@Field final String RAIL_FREIGHT_MAINTENANCE_PL_TYPE = "RailFreightPriceMaintenance"

// List Price Maintenance CPT key names
@Field final String LIST_PRICE_MAINTENANCE_JOBBER_NAME = "Jobber" //Deprecated
@Field final String LIST_PRICE_MAINTENANCE_SRP_NAME = "SRP" //Deprecated
@Field final String LIST_PRICE_MAINTENANCE_MAP_NAME = "MAP" //Deprecated

@Field final Map<String, String> PRICE_TYPE_CONDITION_TYPE = [
        "1" : "ZPFX",   //Pricing formula
        "2" : "ZCSP"    //Mass edit
]

@Field final Map<String, Integer> PER_ROUNDING_DECIMALS = [
        "1"     : 2,
        "10"    : 3,
        "100"   : 4,
]

@Field final Map<String, Integer> UOM_ROUNDING_DECIMALS = [
        "LB"        : 4,
        "KG"        : 4,
        "DEFAULT"   : 2,
]

@Field final List<String> ZBPL_SCALES_LINE_NUMBERS = ["1", "4", "7"] //Deprecated

@Field final String DEFAULT_DISTRIBUTION_CHANNEL = "10"
@Field final Integer MAX_NUMBER_OF_DECIMALS_FOR_SCALES = 2

Integer getPerRoundingDecimals (String per) {
    return PER_ROUNDING_DECIMALS[per]
}

Integer getUOMRoundingDecimals (String uom) {
    Integer roundingDecimals = UOM_ROUNDING_DECIMALS[uom]
    if (!roundingDecimals) {
        roundingDecimals = UOM_ROUNDING_DECIMALS["DEFAULT"]
    }
    return roundingDecimals
}