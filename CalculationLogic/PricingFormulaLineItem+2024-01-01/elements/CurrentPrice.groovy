if (!out.NumberOfDecimals) return

libs.SharedLib.RoundingUtils.round(out.LoadQuotes.Price, out.NumberOfDecimals)