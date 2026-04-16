
if (api.isInputGenerationExecution()) return

def quotes = out.GetQuotes

quotes?.each{
    def calcItem = api.getCalculableLineItemCollection(it)

    libs.QuoteLibrary.Emails.defineDealEmailSubjectAndBody(calcItem)
}