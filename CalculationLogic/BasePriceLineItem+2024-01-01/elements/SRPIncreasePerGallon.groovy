if(out.NumberOfGallon?.toBigDecimal() && out.NewSRP?.toBigDecimal() != null && out.SuggRetailPrice?.toBigDecimal() != null)
    return (out.NewSRP - out.SuggRetailPrice) / out.NumberOfGallon