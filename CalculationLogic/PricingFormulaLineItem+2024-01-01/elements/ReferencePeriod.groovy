return api.attributedResult(out.LoadQuotes?.ReferencePeriodValue)
        .withManualOverrideValueOptions(out.LoadReferencePeriodOptions.keySet() as List)
        .withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())