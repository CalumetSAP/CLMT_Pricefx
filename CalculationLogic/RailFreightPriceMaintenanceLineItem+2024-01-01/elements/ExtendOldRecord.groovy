Boolean extendFreightOldRecord = out.OldFreightValidTo != null && out.NewFreightValidFrom != null && out.OldFreightValidTo < out.NewFreightValidFrom-1
Boolean extendPriceOldRecord = out.OldPriceValidTo != null && out.NewPriceValidFrom != null && out.OldPriceValidTo < out.NewPriceValidFrom-1

if (extendFreightOldRecord && extendPriceOldRecord) return "Price and Freight"

if (extendFreightOldRecord) return "Freight"

if (extendPriceOldRecord) return "Price"