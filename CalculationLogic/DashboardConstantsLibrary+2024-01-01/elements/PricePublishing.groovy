import groovy.transform.Field

// Tables
@Field final String CUSTOMER_EXTENSION_PARTNER_FUNCTION = "PartnerKNVP"
@Field final String CUSTOMER_EXTENSION_CUSTOMER_HIERARCHY = "CustomerHierarchyKNVH"

@Field final String PRICING_DATE_INPUT_KEY = "PricingDate"
@Field final String PRICING_DATE_INPUT_LABEL = "Pricing Date"
@Field final String NEXT_PRICING_INPUT_KEY = "NextPricing"
@Field final String NEXT_PRICING_INPUT_LABEL = "Next Pricing"
@Field final String VARIANT_INPUT_KEY = "Variant"
@Field final String VARIANT_INPUT_LABEL = "Variant"
@Field final String VARIANT_NAME_INPUT_KEY = "VariantName"
@Field final String VARIANT_NAME_INPUT_LABEL = "Variant Name"
@Field final String SALES_ORG_INPUT_KEY = "SalesOrg"
@Field final String SALES_ORG_INPUT_LABEL = "Sales Org"
@Field final String DIVISION_INPUT_KEY = "Division"
@Field final String DIVISION_INPUT_LABEL = "Division"
@Field final String LABEL_INPUT_KEY = "Label"
@Field final String LABEL_INPUT_LABEL = "Label"
@Field final String MASTER_PARENT_INPUT_KEY = "MasterParent"
@Field final String MASTER_PARENT_INPUT_LABEL = "Master Parent"
@Field final String SOLD_TO_INPUT_KEY = "SoldTo"
@Field final String SOLD_TO_INPUT_LABEL = "Sold To"
@Field final String SHIP_TO_REGION_INPUT_KEY = "ShipToRegion"
@Field final String SHIP_TO_REGION_INPUT_LABEL = "Ship to Region"
@Field final String SHIP_TO_CITY_INPUT_KEY = "ShipToCity"
@Field final String SHIP_TO_CITY_INPUT_LABEL = "Ship to City"
@Field final String SHIP_TO_INPUT_KEY = "ShipTo"
@Field final String SHIP_TO_INPUT_LABEL = "Ship To"
@Field final String PRODUCT_HIERARCHY_1_INPUT_KEY = "PH1"
@Field final String PRODUCT_HIERARCHY_1_INPUT_LABEL = "PH 1"
@Field final String PRODUCT_HIERARCHY_2_INPUT_KEY = "PH2"
@Field final String PRODUCT_HIERARCHY_2_INPUT_LABEL = "PH 2"
@Field final String PRODUCT_HIERARCHY_3_INPUT_KEY = "PH3"
@Field final String PRODUCT_HIERARCHY_3_INPUT_LABEL = "PH 3"
@Field final String PRODUCT_HIERARCHY_4_INPUT_KEY = "PH4"
@Field final String PRODUCT_HIERARCHY_4_INPUT_LABEL = "PH 4"
@Field final String BRAND_INPUT_KEY = "Brand"
@Field final String BRAND_INPUT_LABEL = "Brand"
@Field final String PRODUCTS_INPUT_KEY = "Products"
@Field final String PRODUCTS_INPUT_LABEL = "Products"
@Field final String CONTRACT_INPUT_KEY = "Contract"
@Field final String CONTRACT_INPUT_LABEL = "Contract"
@Field final String PRICELIST_INPUT_KEY = "Pricelist"
@Field final String PRICELIST_INPUT_LABEL = "Pricelist"
@Field final String CONTRACT_LINE_INPUT_KEY = "ContractLine"
@Field final String CONTRACT_LINE_INPUT_LABEL = "Contract Line"
@Field final String TO_EMAILS_INPUT_KEY = "ToEmails"
@Field final String TO_EMAILS_INPUT_LABEL = "To Email(s)"
@Field final String SHOW_ADDER_INPUT_KEY = "ShowAdder"
@Field final String SHOW_ADDER_INPUT_LABEL = "Show Adder"
@Field final String PLANT_INPUT_KEY = "Plant"
@Field final String PLANT_INPUT_LABEL = "Plant"
@Field final String SALES_PERSON_INPUT_KEY = "SalesPerson"
@Field final String SALES_PERSON_INPUT_LABEL = "Sales Person"
@Field final String SHOW_JOBBER_SRP_MAP_INPUT_KEY = "ShowJobberSRPMAP"
@Field final String SHOW_JOBBER_SRP_MAP_INPUT_LABEL = "Show Jobber/SRP/MAP"

@Field final List<String> VARIANT_INPUT_COLUMNS = [
        DIVISION_INPUT_KEY,
        LABEL_INPUT_KEY,
        MASTER_PARENT_INPUT_KEY,
//        TO_EMAILS_INPUT_KEY,
        SHOW_ADDER_INPUT_KEY
]

@Field final List<String> ENCODED_VARIANT_INPUT_COLUMNS = [
        SALES_ORG_INPUT_KEY,
        SOLD_TO_INPUT_KEY,
        SHIP_TO_REGION_INPUT_KEY,
        SHIP_TO_CITY_INPUT_KEY,
        SHIP_TO_INPUT_KEY,
        PRODUCT_HIERARCHY_1_INPUT_KEY,
        PRODUCT_HIERARCHY_2_INPUT_KEY,
        PRODUCT_HIERARCHY_3_INPUT_KEY,
        PRODUCT_HIERARCHY_4_INPUT_KEY,
        BRAND_INPUT_KEY,
        PRODUCTS_INPUT_KEY,
        CONTRACT_INPUT_KEY,
        PRICELIST_INPUT_KEY,
        CONTRACT_LINE_INPUT_KEY
]

@Field final List<String> ENCODED_PB_VARIANT_INPUT_COLUMNS = [
        SALES_ORG_INPUT_KEY,
        SOLD_TO_INPUT_KEY,
        SHIP_TO_REGION_INPUT_KEY,
        SHIP_TO_CITY_INPUT_KEY,
        SHIP_TO_INPUT_KEY,
        PRODUCT_HIERARCHY_1_INPUT_KEY,
        PRODUCT_HIERARCHY_2_INPUT_KEY,
        PRODUCT_HIERARCHY_3_INPUT_KEY,
        PRODUCT_HIERARCHY_4_INPUT_KEY,
        BRAND_INPUT_KEY,
        PRODUCTS_INPUT_KEY,
        CONTRACT_INPUT_KEY,
        PRICELIST_INPUT_KEY,
        CONTRACT_LINE_INPUT_KEY,
        PLANT_INPUT_KEY,
        SALES_PERSON_INPUT_KEY
]

@Field final String SALES_REP_NAME = "Sales Rep Name"
@Field final String PRODUCT_CODE_DESCRIPTION = "Product Code / Description"
@Field final String CUSTOMER_MATERIAL_NUMBER = "Customer Material # / Additional Notes"
@Field final String ORIGIN = "Origin"
@Field final String DELIVERED_LOCATION = "Delivered Location"
@Field final String MODE_OF_SALES = "Mode of Sale"
@Field final String EFFECTIVE_DATE = "Effective Date"
@Field final String FILL_WEIGHT = "Fill Weight"
@Field final String WPG = "WPG"
@Field final String MIN_QTY_PER_UOM = "Min Qty / UOM"
@Field final String PRICE_PER_UOM = "Price / UOM"
@Field final String INCOTERMS_PER_FREIGHT = "Incoterms / Freight / Named Place"
@Field final String INDEX_INDICATOR = "Index Indicator"
@Field final String PriceType = "PriceType"
@Field final String ContractNumber = "Contract Number"
@Field final String QuoteId = "Quote ID"
@Field final String ContractItem = "Contract Item"
@Field final String SalesOrg = "Sales Org"
@Field final String Division = "Division"
@Field final String Pricelist = "Pricelist"
@Field final String QuoteLastUpdate = "Quote Last Update"
@Field final String MATERIAL_NUMBER = "Material Number"
@Field final String MATERIAL_DESCRIPTION = "Material Description"
@Field final String LEGACY_PART_NUMBER = "Legacy Part #"
@Field final String PRICE = "Price"
@Field final String PRICE_UOM = "Price UOM"
@Field final String MOQ_PER_UOM = "MOQ / UOM"
@Field final String PB_INCOTERMS_PER_FREIGHT = "Incoterms / Freight Terms"
@Field final String JOBBERS = "Jobbers"
@Field final String SRP = "SRP"
@Field final String MAP = "MAP"

@Field final String SOLD_TO = "Sold To"
@Field final String SOLD_TO_NAME = "Sold-to Name"
@Field final String SHIP_TO = "Ship To"
@Field final String SHIP_TO_NAME = "Ship-to Name"
@Field final String CUSTOMER_GROUP = "Customer Group"
@Field final String SHIP_TO_INDUSTRY = "Ship-to Industry"
@Field final String EXPIRATION_DATE = "Expiration Date"
@Field final String NET_WEIGHT_UOM = "Net Weight UoM"
@Field final String ORIGIN_PRICE = "Origin Price"
@Field final String FREIGHT_PRICE = "Freight Price"
@Field final String DELIVERED_PRICE = "Delivered Price"
@Field final String CURRENCY = "Currency"
@Field final String ORIGIN_STANDARD_UOM_PRICE = "Origin Standard UoM Price"
@Field final String FREIGHT_STANDARD_UOM_PRICE = "Freight Standard UoM Price"
@Field final String DELIVERED_STANDARD_UOM_PRICE = "Delivered Standard UoM Price"
@Field final String STANDARD_UOM = "Standard UoM"
@Field final String SALES_REP_NUMBER = "Sales Rep Number"
@Field final String SHIPPPING_POINT_RECEIVING_PT = "Shipping Point/Receiving Pt"
@Field final String PRODUCT_HIERARCHY = "Product Hierarchy"
@Field final String PRODUCT_HIERARCHY_LEVEL_2 = "Product hierarchy Level 2"
@Field final String SOLD_TO_INCLUDED_IN_EXCLUSION_TABLE = "Sold-to included in Exclusions Table"
@Field final String COMMENT_FROM_EXCLUSION_TABLE = "Comment from Exclusions table"
@Field final String FREIGHT_TERM = "Freight Term"
@Field final String FREIGHT_VALID_FROM = "Freight Valid From"
@Field final String FREIGHT_VALID_TO = "Freight Valid To"
@Field final String SHIP_TO_CITY = "Ship-to City"
@Field final String SHIP_TO_STATE = "Ship-to State"
@Field final String SHIP_TO_ZIP = "Ship-to Zip"
@Field final String SHIP_TO_COUNTRY = "Ship-to Country"
@Field final String NAMED_PLACE = "Named Place"

@Field final String CLEAR_ALL = "Clear all"


