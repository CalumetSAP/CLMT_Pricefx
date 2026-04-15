import net.pricefx.common.api.InputType

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final lineItemOutputs = libs.QuoteConstantsLibrary.LineItemOutputs

api.local.pricingAndFreightInputs = [
        (lineItemConstants.SHIP_TO_ID)                        : [Type: InputType.OPTION],
        (lineItemConstants.PLANT_ID)                          : [Type: InputType.OPTION],
        (lineItemConstants.INCO_TERM_ID)                      : [Type: InputType.OPTION],
        (lineItemConstants.FREIGHT_TERM_ID)                   : [Type: InputType.OPTION],
        (lineItemConstants.NAMED_PLACE_ID)                    : [Type: InputType.STRINGUSERENTRY],
        (lineItemConstants.SHIPPING_POINT_ID)                 : [Type: InputType.OPTION],
        (lineItemConstants.SALES_UOM_ID)                      : [Type: InputType.OPTION],
        (lineItemConstants.PRICING_UOM_ID)                    : [Type: InputType.OPTION],
        (lineItemConstants.UNITS_ID)                          : [Type: InputType.INTEGERUSERENTRY],
        (lineItemConstants.PRICE_ID)                          : [Type: InputType.USERENTRY, DataType: "float"],
        (lineItemConstants.PER_ID)                            : [Type: InputType.USERENTRY, DataType: "float"],
        (lineItemConstants.CURRENCY_ID)                       : [Type: InputType.OPTION],
        (lineItemConstants.NUMBER_OF_DECIMALS_ID)             : [Type: InputType.OPTION],
        (lineItemConstants.COMPETITOR_PRICE_ID)               : [Type: InputType.USERENTRY, DataType: "float"],
        (lineItemConstants.MOQ_ID)                            : [Type: InputType.INTEGERUSERENTRY],
        (lineItemConstants.PRICING_FORMULA_CONFIGURATOR_NAME) : [
                Type  : InputType.CONFIGURATOR,
                URL   : lineItemConstants.PRICING_FORMULA_CONFIGURATOR_URL,
                Width : "80%",
                Height: "80%"
        ],
        (lineItemConstants.PRICE_VALID_FROM_ID)               : [Type: InputType.DATEUSERENTRY],
        (lineItemConstants.PRICE_VALID_TO_ID)                 : [Type: InputType.DATEUSERENTRY],
        (lineItemConstants.SALES_PERSON_ID)                   : [Type: InputType.OPTION],
        (lineItemConstants.FREIGHT_ESTIMATE_ID)               : [Type: InputType.BOOLEANUSERENTRY],
        (lineItemConstants.FREIGHT_AMOUNT_ID)                 : [Type: InputType.USERENTRY, DataType: "float"],
        (lineItemConstants.FREIGHT_VALID_TO_ID)               : [Type: InputType.DATEUSERENTRY],
        (lineItemConstants.FREIGHT_VALID_FROM_ID)             : [Type: InputType.DATEUSERENTRY],
        (lineItemConstants.FREIGHT_UOM_ID)                    : [Type: InputType.OPTION],
        (lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID)       : [Type: InputType.STRINGUSERENTRY],
        (lineItemConstants.THIRD_PARTY_CUSTOMER_ID)           : [Type: InputType.STRINGUSERENTRY],
        (lineItemConstants.MEANS_OF_TRANSPORTATION_ID)        : [Type: InputType.OPTION],
        (lineItemConstants.MODE_OF_TRANSPORTATION_ID)         : [Type: InputType.OPTION],
        (lineItemConstants.PRICE_LIST_ID)                     : [Type: InputType.OPTION],
        (lineItemConstants.PRICE_TYPE_ID)                     : [Type: InputType.OPTION],
        (lineItemConstants.SCALES_CONFIGURATOR_NAME)          : [
                Type  : InputType.CONFIGURATOR,
                URL   : lineItemConstants.SCALES_CONFIGURATOR_URL,
                Width : "80%",
                Height: "80%"
        ],
//        (lineItemConstants.PRICE_PROTECTION_CONFIGURATOR_NAME): [
//                Type  : InputType.CONFIGURATOR,
//                URL   : lineItemConstants.PRICE_PROTECTION_CONFIGURATOR_URL,
//                Width : "80%",
//                Height: "80%"
//        ]
]

//api.local.pricingAndFreightHiddenInputs = [
//        lineItemConstants.CONFIGURATOR_NAME
//]

api.local.pricingAndFreightOutputs = [
        "Material",
        "ProductDescription",
        lineItemOutputs.UOM_ID,
        lineItemOutputs.MATERIAL_PACKAGE_STYLE_ID,
        lineItemOutputs.MESSAGE_ID,
        lineItemOutputs.ACCESS_SEQUENCE_ID,
        lineItemOutputs.COST_ID,
        lineItemOutputs.FORMULA_APPROVER_ID,
        lineItemOutputs.APPROVER_ID,
        lineItemOutputs.GUARDRAIL_PRICE_ID,
        lineItemOutputs.SHIP_TO_INDUSTRY_ID,
        lineItemOutputs.SHIP_TO_ADDRESS_ID,
        lineItemOutputs.SHIP_TO_CITY_ID,
        lineItemOutputs.SHIP_TO_STATE_ID,
        lineItemOutputs.SHIP_TO_COUNTRY_ID,
        lineItemOutputs.PH1_ID,
        lineItemOutputs.PH2_ID,
        lineItemOutputs.PH3_ID,
        lineItemOutputs.PH4_ID,
        lineItemOutputs.SCALES_INDICATOR_ID,
        lineItemOutputs.APPROVAL_SEQUENCE_ID,
]