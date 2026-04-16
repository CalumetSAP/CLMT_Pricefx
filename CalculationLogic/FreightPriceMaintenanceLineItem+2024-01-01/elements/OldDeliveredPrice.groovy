def value = out.LoadQuotes.DeliveredPrice
value = out.NumberOfDecimals && value ? libs.SharedLib.RoundingUtils.round(value, out.NumberOfDecimals) : value

return value