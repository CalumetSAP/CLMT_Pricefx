def value = out.LoadQuotes.Price
value = out.NumberOfDecimals && value ? libs.SharedLib.RoundingUtils.round(value, out.NumberOfDecimals) : value

return value