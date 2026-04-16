if (!out.NumberOfDecimals) return

libs.SharedLib.RoundingUtils.round(out.MergeQuoteAndZCSP.Price, out.NumberOfDecimals)