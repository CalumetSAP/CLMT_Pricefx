import groovy.transform.Field

// LINE ITEM INPUTS
@Field final String PLANT_ID = "PlantInput"
@Field final String PLANT_LABEL = "Plant"
@Field final String INCO_TERM_ID = "IncoTermInput"
@Field final String INCO_TERM_LABEL = "Incoterm"
@Field final String FREIGHT_TERM_ID = "FreightTermInput"
@Field final String FREIGHT_TERM_LABEL = "Freight Term"
@Field final String UNITS_ID = "UnitsInput"
@Field final String UNITS_LABEL = "Units"
@Field final String PRICE_ID = "PriceInput"
@Field final String PRICE_LABEL = "Product Price"
@Field final String NAMED_PLACE_ID = "NamedPlaceInput"
@Field final String NAMED_PLACE_LABEL = "Named Place"
@Field final String PRICING_FORMULA_ID = "PricingFormulaInput"
@Field final String PRICING_FORMULA_LABEL = "Pricing Formula"
@Field final String PRICE_VALID_FROM_ID = "PriceValidFromInput"
@Field final String PRICE_VALID_FROM_LABEL = "Price Valid From"
@Field final String PRICE_VALID_TO_ID = "PriceValidToInput"
@Field final String PRICE_VALID_TO_LABEL = "Price Valid To"
@Field final String SALES_UOM_ID = "SalesUOMInput"
@Field final String SALES_UOM_LABEL = "Sales UOM"
@Field final String SHIP_TO_ID = "LineItemShipToInput"
@Field final String SHIP_TO_LABEL = "Ship to"
@Field final String NUMBER_OF_DECIMALS_ID = "NumberOfDecimalsInput"
@Field final String NUMBER_OF_DECIMALS_LABEL = "Number of Decimal Places"
@Field final String MOQ_ID = "MOQInput"
@Field final String MOQ_LABEL = "MOQ"
@Field final String SALES_PERSON_ID = "SalesPersonInput"
@Field final String SALES_PERSON_LABEL = "Sales Person"
@Field final String PRICING_UOM_ID = "PricingUOMInput"
@Field final String PRICING_UOM_LABEL = "Price UOM"
@Field final String CURRENCY_ID = "CurrencyInput"
@Field final String CURRENCY_LABEL = "Currency"
@Field final String COMPETITOR_PRICE_ID = "CompetitorPriceInput"
@Field final String COMPETITOR_PRICE_LABEL = "Competitor Price"
@Field final String FREIGHT_ESTIMATE_ID = "FreightEstimateInput"
@Field final String FREIGHT_ESTIMATE_LABEL = "Freight Request"
@Field final String FREIGHT_AMOUNT_ID = "FreightAmountInput"
@Field final String FREIGHT_AMOUNT_LABEL = "Freight Amount"
@Field final String FREIGHT_VALID_TO_ID = "FreightValidToInput"
@Field final String FREIGHT_VALID_TO_LABEL = "Freight Valid to"
@Field final String FREIGHT_VALID_FROM_ID = "FreightValidFromInput"
@Field final String FREIGHT_VALID_FROM_LABEL = "Freight Valid From"
@Field final String FREIGHT_UOM_ID = "FreightUOMInput"
@Field final String FREIGHT_UOM_LABEL = "Freight UOM"
@Field final String SHIPPING_POINT_ID = "ShippingPointInput"
@Field final String SHIPPING_POINT_LABEL = "Shipping Point"
@Field final String CUSTOMER_MATERIAL_NUMBER_ID = "CustomerMaterialNumberInput"
@Field final String CUSTOMER_MATERIAL_NUMBER_LABEL = "Customer Material #"
@Field final String THIRD_PARTY_CUSTOMER_ID = "ThirdPartyCustomerInput"
@Field final String THIRD_PARTY_CUSTOMER_LABEL = "3rd Party Customer Support"
@Field final String MEANS_OF_TRANSPORTATION_ID = "MeansOfTransportationInput"
@Field final String MEANS_OF_TRANSPORTATION_LABEL = "Means Of Transportation"
@Field final String MODE_OF_TRANSPORTATION_ID = "ModeOfTransportationInput"
@Field final String MODE_OF_TRANSPORTATION_LABEL = "Mode Of Transportation"
@Field final String PRICE_LIST_ID = "PriceListInput"
@Field final String PRICE_LIST_LABEL = "Price List (PLT)"
@Field final String SAP_CONTRACT_ID = "SapContractInput"
@Field final String SAP_CONTRACT_LABEL = "SAP Contract"
@Field final String LINE_NUMBER_ID = "LineNumberInput"
@Field final String LINE_NUMBER_LABEL = "Line Number"
@Field final String COST_ID = "CostInput"
@Field final String COST_LABEL = "Cost"
@Field final String COST_HIDDEN_ID = "CostHiddenInput"
@Field final String REJECTION_REASON_ID = "RejectionReasonInput"
@Field final String REJECTION_REASON_LABEL = "Rejection Reason"
@Field final String APPROVER_HIDDEN_ID = "ApproverHiddenInput"
@Field final String APPROVER_LEVEL_HIDDEN_ID = "ApproverLevelHiddenInput"
@Field final String FORMULA_APPROVER_HIDDEN_ID = "FormulaApproverHiddenInput"
@Field final String PER_ID = "PerInput"
@Field final String PER_LABEL = "Per"
@Field final String PRICE_TYPE_ID = "PriceTypeInput"
@Field final String PRICE_TYPE_LABEL = "Price Type"
@Field final String PREVIOUS_PRICE_TYPE_ID = "PreviousPriceTypeInput"
@Field final String MATERIAL_ID = "MaterialInput"
@Field final String MATERIAL_LABEL = "Material"
@Field final String DESCRIPTION_ID = "DescriptionInput"
@Field final String DESCRIPTION_LABEL = "Description"
@Field final String CONFIGURATOR_NAME = "Inputs"
@Field final String CONFIGURATOR_LABEL = "Inputs"
@Field final String CONFIGURATOR_URL = "QuoteLineItemConfigurator"
@Field final String SCALES_CONFIGURATOR_NAME = "Scales"
@Field final String SCALES_CONFIGURATOR_URL = "ScalesLineItemConfigurator"
@Field final String SCALES_ID = "ScalesMatrixInput"
@Field final String SCALES_LABEL = "Scales"
@Field final String PRICING_FORMULA_CONFIGURATOR_NAME = "Pricing Formula"
@Field final String PRICING_FORMULA_CONFIGURATOR_URL = "PricingFormulaLineItemConfigurator"
@Field final String PF_CONFIGURATOR_INDEX_NUMBER_ID = "IndexNumberInput"
@Field final String PF_CONFIGURATOR_INDEX_NUMBER_LABEL = "Index Number"
@Field final String PF_CONFIGURATOR_REFERENCE_PERIOD_ID = "ReferencePeriodInput"
@Field final String PF_CONFIGURATOR_REFERENCE_PERIOD_LABEL = "Reference Period"
@Field final String PF_CONFIGURATOR_ADDER_ID = "AdderInput"
@Field final String PF_CONFIGURATOR_ADDER_LABEL = "Adder"
@Field final String PF_CONFIGURATOR_ADDER_UOM_ID = "AdderUOMInput"
@Field final String PF_CONFIGURATOR_ADDER_UOM_LABEL = "Adder UOM"
@Field final String PF_CONFIGURATOR_ADDER_NUMBER_OF_DECIMAL_PLACES_ID = "AdderNumberOfDecimalPlacesInput"
@Field final String PF_CONFIGURATOR_ADDER_NUMBER_OF_DECIMAL_PLACES_LABEL = "Adder Number of Decimal Places"
@Field final String PF_CONFIGURATOR_RECALCULATION_DATE_ID = "RecalculationDateInput"
@Field final String PF_CONFIGURATOR_RECALCULATION_DATE_LABEL = "Recalculation Date"
@Field final String PF_CONFIGURATOR_RECALCULATION_PERIOD_ID = "RecalculationPeriodInput"
@Field final String PF_CONFIGURATOR_RECALCULATION_PERIOD_LABEL = "Recalculation Period"
@Field final String PRICE_PROTECTION_CONFIGURATOR_NAME = "Price Protection"
@Field final String PRICE_PROTECTION_CONFIGURATOR_URL = "PriceProtectionLineItemConfigurator"
@Field final String PP_CONFIGURATOR_PRICE_PROTECTION_ID = "PriceProtectionInput"
@Field final String PP_CONFIGURATOR_PRICE_PROTECTION_LABEL = "Price Protection"
@Field final String PP_CONFIGURATOR_NUMBER_OF_DAYS_ID = "NumberOfDaysInput"
@Field final String PP_CONFIGURATOR_NUMBER_OF_DAYS_LABEL = "Number of Days"
@Field final String PP_CONFIGURATOR_MOVEMENT_TIMING_ID = "MovementTimingInput"
@Field final String PP_CONFIGURATOR_MOVEMENT_TIMING_LABEL = "Movement Timing"
@Field final String PP_CONFIGURATOR_MOVEMENT_START_ID = "MovementStartInput"
@Field final String PP_CONFIGURATOR_MOVEMENT_START_LABEL = "Movement Start Month"
@Field final String PP_CONFIGURATOR_MOVEMENT_DAY_ID = "MovementDayInput"
@Field final String PP_CONFIGURATOR_MOVEMENT_DAY_LABEL = "Movement Day"
@Field final String NEW_QUOTE_CONFIGURATOR_NAME = "Inputs"
@Field final String NEW_QUOTE_CONFIGURATOR_LABEL = "Inputs"
@Field final String NEW_QUOTE_CONFIGURATOR_URL = "NewQuoteLineItemConfigurator"
@Field final String EXISTING_QUOTE_CONFIGURATOR_NAME = "Inputs"
@Field final String EXISTING_QUOTE_CONFIGURATOR_URL = "NewQuoteExistingLineItemConfigurator"
@Field final String UOM_ID = "UOMInput"
@Field final String UOM_LABEL = "Base Unit of Measure"
@Field final String MATERIAL_PACKAGE_STYLE_ID = "MaterialPackageStyleInput"
@Field final String MATERIAL_PACKAGE_STYLE_LABEL = "Material Package Style"
@Field final String SALES_SHIPPING_METHOD_ID = "SalesShippingMethodInput"
@Field final String SALES_SHIPPING_METHOD_LABEL = "Sales Shipping Method"
@Field final String DELIVERED_PRICE_ID = "DeliveredPriceInput"
@Field final String DELIVERED_PRICE_LABEL = "Delivered Price"
@Field final String PH1_ID = "PH1Input"
@Field final String PH1_LABEL = "PH1"
@Field final String PH2_ID = "PH2Input"
@Field final String PH2_LABEL = "PH2"
@Field final String PH3_ID = "PH3Input"
@Field final String PH3_LABEL = "PH3"
@Field final String PH4_ID = "PH4Input"
@Field final String PH4_LABEL = "PH4"
@Field final String SHIP_TO_INDUSTRY_ID = "ShipToIndustryInput"
@Field final String SHIP_TO_INDUSTRY_LABEL = "Ship-To Industry"
@Field final String SHIP_TO_ADDRESS_ID = "ShipToAddressInput"
@Field final String SHIP_TO_ADDRESS_LABEL = "Ship-To Address"
@Field final String SHIP_TO_CITY_ID = "ShipToCityInput"
@Field final String SHIP_TO_CITY_LABEL = "Ship-To City"
@Field final String SHIP_TO_STATE_ID = "ShipToStateInput"
@Field final String SHIP_TO_STATE_LABEL = "Ship-To State"
@Field final String SHIP_TO_ZIP_ID = "ShipToZipInput"
@Field final String SHIP_TO_ZIP_LABEL = "Ship-To Zip"
@Field final String SHIP_TO_COUNTRY_ID = "ShipToCountryInput"
@Field final String SHIP_TO_COUNTRY_LABEL = "Ship-To Country"
@Field final String RECOMMENDED_PRICE_ID = "RecommendedPriceInput"
@Field final String RECOMMENDED_PRICE_LABEL = "Recommended Price"
@Field final String APPROVAL_SEQUENCE_HIDDEN_ID = "ApprovalSequenceHidden"
@Field final String MOQ_UOM_ID = "MOQUOMInput"
@Field final String MOQ_UOM_LABEL = "MOQ UOM"
@Field final String ERROR_MESSAGE_HIDDEN_ID = "ErrorMessageHiddenInput"
@Field final String APPROVER_ERROR_MESSAGE_HIDDEN_ID = "ApproverErrorMessageHiddenInput"
@Field final String LEGACY_MATERIAL_NUMBER_ID = "LegacyMaterialNumberInput"
@Field final String LEGACY_MATERIAL_NUMBER_LABEL = "Legacy Material Number"
@Field final String MATERIAL_MARGIN_ID = "MaterialMarginInput"
@Field final String MATERIAL_MARGIN_LABEL = "Material Margin"
@Field final String GUARDRAIL_VALUES_HIDDEN_ID = "GuardrailValuesHidden"
@Field final String PRICE_COMPLETED_HIDDEN_ID = "PriceCompletedHiddenInput"
@Field final String NUMBER_OF_DECIMALS_HIDDEN_ID = "NumberOfDecimalsHiddenInput"
@Field final String FREIGHT_TERM_HIDDEN_ID = "FreightTermHiddenInput"
@Field final String GUARDRAIL_ERROR_HIDDEN_ID = "GuardrailErrorHiddenInput"
@Field final String APPROVER_ERROR_HIDDEN_ID = "ApproverErrorHiddenInput"
@Field final String INDEX_INDICATOR_ID = "IndexIndicatorInput"
@Field final String INDEX_INDICATOR_LABEL = "Index Indicator"
@Field final String DO_NOT_FILTER_HIDDEN_ID = "DoNotFilterHiddenInput"
@Field final String DATA_SOURCE_VALUES_HIDDEN_ID = "DataSourceValuesHiddenInput"
@Field final String C4C_UOM_HIDDEN_ID = "C4CUOMHiddenInput"
@Field final String CONFIGURATOR_HAS_CHANGED_ID = "ConfiguratorHasChangedInput"
@Field final String LINE_HAS_CHANGED_ID = "LineHasChangedInput"
@Field final String LINE_HAS_CHANGED_LABEL = "Line Has Changed"
@Field final String HEADER_ONLY_FLAG_ID = "HeaderOnlyFlagInput"
@Field final String HEADER_ONLY_FLAG_LABEL = "Header Only Flag"
@Field final String PRICE_CHANGE_FLAG_ID = "PriceChangeFlagInput"
@Field final String PRICE_CHANGE_FLAG_LABEL = "Price Change Flag"
@Field final String PRICE_TYPE_HAS_CHANGED_ID = "PriceTypeHasChangedHiddenInput"
@Field final String REJECTION_REASON_HAS_CHANGED_ID = "RejectionReasonHasChangedHiddenInput"
@Field final String PT_CHANGED_FROM_CUSTOMER_TO_INDEX_ID = "PTChangedFromCustomerToIndexInput"
@Field final String PT_CHANGED_FROM_CUSTOMER_TO_INDEX_LABEL = "Price Type Changed from Customer Price to Index Price"
@Field final String PT_CHANGED_FROM_INDEX_TO_CUSTOMER_ID = "PTChangedFromIndexToCustomerInput"
@Field final String PT_CHANGED_FROM_INDEX_TO_CUSTOMER_LABEL = "Price Type Changed from Index Price to Customer Price"
@Field final String SHOULD_USE_DEFAULT_VALUES_ID = "ShouldUseDefaultValuesInput"
@Field final String FREIGHT_TERM_CHANGE_FLAG_ID = "FreightTermChangeFlagInput"
@Field final String FREIGHT_TERM_CHANGE_FLAG_LABEL = "Freight Term Change Flag"
@Field final String PREVIOUS_PRICELIST_ID = "PreviousPricelistInput"
@Field final String PREVIOUS_MOQ_ID = "PreviousMOQInput"
@Field final String SCALES_HAS_CHANGED_ID = "ScalesHasChangedInput"
@Field final String FREIGHT_HAS_CHANGED_ID = "FreightHasChangedInput"
@Field final String INDEX_DATA_HAS_CHANGED_ID = "IndexDataHasChangedInput"
@Field final String FREIGHT_PREVIOUS_VALUES_ID = "FreightPreviousValuesInput"
@Field final String SAP_CHANGES_FLAG_ID = "SAPChangesFlagInput"
@Field final String LINE_IS_REJECTED_ID = "LineIsRejectedInput"
@Field final String LINE_IS_REJECTED_LABEL = "Line is Rejected"

// REQUIRED FIELDS
@Field final List<String> REQUIRED_SALES_INPUTS = [
        PRICE_TYPE_ID,
        MOQ_ID,
        MOQ_UOM_ID,
        PLANT_ID,
        INCO_TERM_ID,
        SALES_SHIPPING_METHOD_ID,
        NUMBER_OF_DECIMALS_ID,
        SALES_PERSON_ID,
        FREIGHT_UOM_ID
]

@Field final List<String> REQUIRED_PRICING_INPUTS = [
        SHIP_TO_ID,
        PRICE_TYPE_ID,
        PER_ID,
        MOQ_ID,
        MOQ_UOM_ID,
        PLANT_ID,
        INCO_TERM_ID,
        SHIPPING_POINT_ID,
        CURRENCY_ID,
        NUMBER_OF_DECIMALS_ID,
        SALES_PERSON_ID,
        FREIGHT_VALID_FROM_ID,
        FREIGHT_VALID_TO_ID,
        FREIGHT_UOM_ID,
        FREIGHT_TERM_ID
]

@Field final List<String> REQUIRED_FREIGHT_INPUTS = [
        FREIGHT_AMOUNT_ID,
        MEANS_OF_TRANSPORTATION_ID,
        MODE_OF_TRANSPORTATION_ID
]

@Field final List<String> REQUIRED_PRICE_TYPE_INPUTS_1 = [
        PF_CONFIGURATOR_INDEX_NUMBER_ID,
        PF_CONFIGURATOR_REFERENCE_PERIOD_ID,
        PF_CONFIGURATOR_ADDER_ID,
        PF_CONFIGURATOR_ADDER_UOM_ID,
        PF_CONFIGURATOR_RECALCULATION_DATE_ID,
        PF_CONFIGURATOR_RECALCULATION_PERIOD_ID,
        PRICE_ID,
        DELIVERED_PRICE_ID
]

@Field final List<String> REQUIRED_PRICE_TYPE_INPUTS_2 = [
        PRICE_ID,
        DELIVERED_PRICE_ID,
        PRICING_UOM_ID,
        PRICE_VALID_FROM_ID,
        PRICE_VALID_TO_ID,
        PRICE_LIST_ID
]

@Field final List<String> REQUIRED_PRICE_TYPE_INPUTS_3 = [
        PRICE_LIST_ID
]

@Field final List<String> NOT_REQUIRED_PRICE_TYPE_INPUTS_4 = [
        PRICE_ID,
        COMPETITOR_PRICE_ID,
        DELIVERED_PRICE_ID,
        PRICING_UOM_ID,
        PRICE_VALID_FROM_ID,
        PRICE_VALID_TO_ID,
        PER_ID,
        CURRENCY_ID,
        NUMBER_OF_DECIMALS_ID
]

@Field final List<String> REQUIRED_DISTINCT_FCA_INPUTS = [
        FREIGHT_TERM_ID
]

@Field final List<String> REQUIRED_FREIGHT_ESTIMATE_INPUTS = [
        FREIGHT_AMOUNT_ID,
        FREIGHT_VALID_FROM_ID,
        FREIGHT_VALID_TO_ID,
        FREIGHT_UOM_ID
]

// PRICE COMPLETED
@Field final String PRICE_COMPLETED_PRICE_ID = "PRICE"
@Field final String PRICE_COMPLETED_DELIVERED_PRICE_ID = "DELIVERED_PRICE"
@Field final String PRICE_COMPLETED_ADDER_ID = "ADDDER"