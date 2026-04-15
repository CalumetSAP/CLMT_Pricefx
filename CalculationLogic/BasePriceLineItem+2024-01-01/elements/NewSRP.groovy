def srpPercent = out.SRPPercent?.toBigDecimal()
if(srpPercent != null && srpPercent != 1 && out.NewListPrice?.toBigDecimal()){
    return out.NewListPrice?.toBigDecimal() / (1 - srpPercent)
}