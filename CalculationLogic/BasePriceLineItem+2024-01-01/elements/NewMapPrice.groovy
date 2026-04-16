def mapPercent = out.MAPPercent?.toBigDecimal()
if(mapPercent != null && mapPercent != 1 && out.NewListPrice?.toBigDecimal()){
    return out.NewListPrice?.toBigDecimal() / (1 - mapPercent)
}