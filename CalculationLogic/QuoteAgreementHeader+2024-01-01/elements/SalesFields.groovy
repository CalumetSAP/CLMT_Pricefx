import net.pricefx.common.api.InputType

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final lineItemOutputs = libs.QuoteConstantsLibrary.LineItemOutputs

api.local.salesInputs = [
        (lineItemConstants.CONFIGURATOR_NAME)    : [
                Type  : InputType.CONFIGURATOR,
                URL   : lineItemConstants.CONFIGURATOR_URL,
                Width : "80%",
                Height: "80%"
        ],
        (lineItemConstants.PRICE_ID)             : [Type: InputType.USERENTRY, DataType: "float"],
        (lineItemConstants.PER_ID)               : [Type: InputType.USERENTRY, DataType: "float"],
        (lineItemConstants.CURRENCY_ID)          : [Type: InputType.OPTION],
        (lineItemConstants.PRICE_VALID_FROM_ID)  : [Type: InputType.DATEUSERENTRY],
        (lineItemConstants.PRICE_VALID_TO_ID)    : [Type: InputType.DATEUSERENTRY],
        (lineItemConstants.SCALES_CONFIGURATOR_NAME)          : [
                Type  : InputType.CONFIGURATOR,
                URL   : lineItemConstants.SCALES_CONFIGURATOR_URL,
                Width : "80%",
                Height: "80%"
        ]
]

//api.local.salesHiddenInputs = [
//        lineItemConstants.SHIP_TO_ID,
//        lineItemConstants.PLANT_ID,
//        lineItemConstants.INCO_TERM_ID,
//        lineItemConstants.FREIGHT_TERM_ID,
//        lineItemConstants.NAMED_PLACE_ID,
//        lineItemConstants.SHIPPING_POINT_ID,
//        lineItemConstants.SALES_UOM_ID,
//        lineItemConstants.PRICING_UOM_ID,
//        lineItemConstants.UNITS_ID,
//        lineItemConstants.COMPETITOR_PRICE_ID,
//        lineItemConstants.MOQ_ID,
//        lineItemConstants.SALES_PERSON_ID,
//        lineItemConstants.FREIGHT_ESTIMATE_ID,
//        lineItemConstants.FREIGHT_AMOUNT_ID,
//        lineItemConstants.FREIGHT_VALID_TO_ID,
//        lineItemConstants.FREIGHT_UOM_ID,
//        lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID,
//        lineItemConstants.THIRD_PARTY_CUSTOMER_ID,
//        lineItemConstants.MEANS_OF_TRANSPORTATION_ID,
//        lineItemConstants.MODE_OF_TRANSPORTATION_ID,
//        lineItemConstants.PRICE_LIST_ID,
//        lineItemConstants.PRICE_TYPE_ID,
//]

api.local.salesOutputs = [
        lineItemOutputs.FORMULA_APPROVER_ID,
        lineItemOutputs.GUARDRAIL_PRICE_ID,
        lineItemOutputs.APPROVER_ID,
        lineItemOutputs.APPROVAL_SEQUENCE_ID,
        lineItemOutputs.SCALES_INDICATOR_ID,
]