if (api.isInputGenerationExecution()) return

def quoteId = api.isDebugMode() ? "1698.Q" : dist?.calcItem?.Key2

loader = api.isDebugMode() ? [] : dist.dataLoader

List lineIds = api.getCalculableLineItemCollection(quoteId)?.lineItems?.lineId ?: []

for (lineId in lineIds) {
    loader.addRow([
            "LineID": lineId
    ])
}

api.trace("loader", loader)