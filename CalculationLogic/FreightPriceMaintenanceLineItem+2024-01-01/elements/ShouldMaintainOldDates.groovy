import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy")

def validFrom = sdf.parse(api.local.validFrom as String)

return validFrom && validFrom > out.CalculateNewPriceValidFrom