import java.time.LocalDate

def validTo = out.ZBPLMerged?.ValidTo

if (validTo instanceof LocalDate) return validTo?.toString()

return validTo