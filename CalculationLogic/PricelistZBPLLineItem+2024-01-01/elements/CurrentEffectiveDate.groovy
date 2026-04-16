import java.time.LocalDate

def validFrom = out.ZBPLMerged?.ValidFrom

if (validFrom instanceof LocalDate) return validFrom?.toString()

return validFrom