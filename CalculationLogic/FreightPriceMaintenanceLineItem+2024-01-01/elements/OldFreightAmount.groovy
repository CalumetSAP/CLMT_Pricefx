def value = out.LoadQuotes.FreightAmount
value = value ? libs.SharedLib.RoundingUtils.round(value, 2) : value

return value