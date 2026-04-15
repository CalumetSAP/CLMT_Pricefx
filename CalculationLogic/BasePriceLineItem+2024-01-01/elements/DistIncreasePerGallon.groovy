if(out.NumberOfGallon?.toBigDecimal() && out.NewListPrice?.toBigDecimal() != null && out.CurrentListPrice?.toBigDecimal() != null){
    return out.NewListPrice?.toBigDecimal() - out.CurrentListPrice?.toBigDecimal() / out.NumberOfGallon?.toBigDecimal()
}