def kms = out.RouteDistanceKM
def conversionFactor = api.global.kmToMilesConversionFactor

if (kms == null || conversionFactor == null) return null

return libs.SharedLib.RoundingUtils.round(kms * conversionFactor, 0)