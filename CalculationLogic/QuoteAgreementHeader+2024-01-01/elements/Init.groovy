final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final lineItemOutputs = libs.QuoteConstantsLibrary.LineItemOutputs

api.local.loginName = api.user("loginName")

api.local.allInputs = [
        lineItemConstants.CONFIGURATOR_NAME,
        lineItemConstants.SHIP_TO_ID,
        lineItemConstants.PLANT_ID,
        lineItemConstants.INCO_TERM_ID,
        lineItemConstants.FREIGHT_TERM_ID,
        lineItemConstants.NAMED_PLACE_ID,
        lineItemConstants.SHIPPING_POINT_ID,
        lineItemConstants.SALES_UOM_ID,
        lineItemConstants.PRICING_UOM_ID,
        lineItemConstants.UNITS_ID,
        lineItemConstants.PRICE_ID,
        lineItemConstants.PER_ID,
        lineItemConstants.CURRENCY_ID,
        lineItemConstants.NUMBER_OF_DECIMALS_ID,
        lineItemConstants.COMPETITOR_PRICE_ID,
        lineItemConstants.MOQ_ID,
        lineItemConstants.PRICING_FORMULA_CONFIGURATOR_NAME,
        lineItemConstants.PRICE_VALID_FROM_ID,
        lineItemConstants.PRICE_VALID_TO_ID,
        lineItemConstants.SALES_PERSON_ID,
        lineItemConstants.FREIGHT_ESTIMATE_ID,
        lineItemConstants.FREIGHT_AMOUNT_ID,
        lineItemConstants.FREIGHT_VALID_TO_ID,
        lineItemConstants.FREIGHT_VALID_FROM_ID,
        lineItemConstants.FREIGHT_UOM_ID,
        lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID,
        lineItemConstants.THIRD_PARTY_CUSTOMER_ID,
        lineItemConstants.MEANS_OF_TRANSPORTATION_ID,
        lineItemConstants.MODE_OF_TRANSPORTATION_ID,
        lineItemConstants.PRICE_LIST_ID,
        lineItemConstants.PRICE_TYPE_ID,
//        lineItemConstants.PRICE_PROTECTION_CONFIGURATOR_NAME,
        lineItemConstants.SCALES_CONFIGURATOR_NAME,
        "User Groups"
]

api.local.allOutputs = [
        "ProductDescription",
        lineItemOutputs.UOM_ID,
        lineItemOutputs.COST_ID,
        lineItemOutputs.FORMULA_APPROVER_ID,
        lineItemOutputs.GUARDRAIL_PRICE_ID,
        lineItemOutputs.DISCOUNT_ID,
        lineItemOutputs.APPROVER_ID,
        lineItemOutputs.SHIP_TO_INDUSTRY_ID,
        lineItemOutputs.SHIP_TO_ADDRESS_ID,
        lineItemOutputs.SHIP_TO_CITY_ID,
        lineItemOutputs.SHIP_TO_STATE_ID,
        lineItemOutputs.SHIP_TO_COUNTRY_ID,
        lineItemOutputs.PH1_ID,
        lineItemOutputs.PH2_ID,
        lineItemOutputs.PH3_ID,
        lineItemOutputs.PH4_ID,
        lineItemOutputs.MATERIAL_PACKAGE_STYLE_ID,
        lineItemOutputs.MESSAGE_ID,
        lineItemOutputs.ACCESS_SEQUENCE_ID,
]

def qp = quoteProcessor
if (qp.isPrePhase()){
    qp.setRenderInfo("userGroupEdit","hide",true)
    qp.setRenderInfo("userGroupViewDetails","hide",true)
}