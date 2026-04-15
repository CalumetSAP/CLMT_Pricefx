return api.attributedResult(out.LoadQuoteScales?.getAt(0)?.ScaleUOM ?: "")
        .withManualOverrideValueOptions(out.LoadUOMOptions as List)
        .withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())