return api.attributedResult(out.LoadQuotes?.AdderUOM)
        .withManualOverrideValueOptions(out.LoadUOMOptions as List)
        .withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())