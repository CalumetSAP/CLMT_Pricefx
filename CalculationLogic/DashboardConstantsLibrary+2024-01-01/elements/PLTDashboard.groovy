import groovy.transform.Field

@Field final String PRICING_DATE_INPUT_KEY = "PricingDate"
@Field final String PRICING_DATE_INPUT_LABEL = "Pricing Date"
@Field final String PRICELIST_INPUT_KEY = "Pricelist"
@Field final String PRICELIST_INPUT_LABEL = "Pricelist"
@Field final String SHOW_JOBBER_SRP_MAP_INPUT_KEY = "ShowJobberSRPMAP"
@Field final String SHOW_JOBBER_SRP_MAP_INPUT_LABEL = "Show Jobber/SRP/MAP"
@Field final String BRAND_INPUT_KEY = "Brand"
@Field final String BRAND_INPUT_LABEL = "Brand"
@Field final String MATERIAL_INPUT_KEY = "Material"
@Field final String MATERIAL_INPUT_LABEL = "Material"
@Field final String SORTING_INPUT_KEY = "Sorting"
@Field final String SORTING_INPUT_LABEL = "Sorting"

@Field final String MATERIAL_NUMBER = "Material Number"
@Field final String MATERIAL_DESCRIPTION = "Material Description"
@Field final String LEGACY_PART_NUMBER_EA = "Legacy Part #"
@Field final String LEGACY_PART_NUMBER_CASE = "Legacy Part # (CASE)"
@Field final String ITEM_INCLUDED = "Item Included"
@Field final String EFFECTIVE_DATE = "Effective Date"
@Field final String MOQ_PER_UOM = "MOQ/UOM"
@Field final String PRICE = "Price"
@Field final String UOM = "UOM"
@Field final String JOBBER_DEALER_PRICE = "Jobber / Dealer Price"
@Field final String SRP = "Suggested Retail Price"
@Field final String MAP = "MAP"

@Field final String PB_SORT = "PB"
@Field final String SPS_SORT = "SPS"

@Field final List<String> SORT_OPTIONS = [
        PB_SORT,
        SPS_SORT
]