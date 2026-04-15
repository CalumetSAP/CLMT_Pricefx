if (api.isInputGenerationExecution()) return

def newQuotes = out.FindNewQuotes
def existingQuotes = out.FindExistingQuotes
def divisionMap

def completeDivision
newQuotes.each { quote ->
    completeDivision = quote?.division ? divisionMap?.get(quote?.division) : null
    api.update("Q", [
            "uniqueName"     : quote?.uniqueName,
            "additionalInfo3": completeDivision
    ])
}

existingQuotes.each { quote ->
    completeDivision = quote?.division ? divisionMap?.get(quote?.division) : "-"
    api.update("Q", [
            "uniqueName"                   : quote?.uniqueName,
            "attributeExtension___Division": completeDivision
    ])
}

return null