if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || out.FindCustomerShipTo?.size() != 1) return

def shipTo = out.FindCustomerShipTo?.find()?.split(" - ")?.getAt(0)?.trim()
if (!shipTo) return

def fields = ["customerId", "attribute4", "attribute5", "attribute6", "attribute7", "attribute8", "attribute12"]
def filter = Filter.equal("customerId", shipTo)

def customerMasterData = api.stream("C", null, fields, filter)?.withCloseable {
    it.collectEntries {
        [
                Industry: it.attribute12,
                Address : it.attribute8,
                City    : it.attribute5,
                State   : it.attribute7,
                Zip     : it.attribute6,
                Country : it.attribute4,
        ]
    }
}

api.global.shipToData = customerMasterData

return null