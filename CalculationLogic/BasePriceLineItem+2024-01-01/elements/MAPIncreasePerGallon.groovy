if(out.NumberOfGallon?.toBigDecimal() && out.NewMapPrice?.toBigDecimal() != null && out.MapPrice?.toBigDecimal() != null)
    return (out.NewMapPrice - out.MapPrice) / out.NumberOfGallon