
def customers = out.FindCustomers
def salesOrg = out.SalesOrg?.getFirstInput()?.getValue()
def division = out.Division?.getFirstInput()?.getValue()
def masterParents = out.FindMasterParent
//def soldTos = out.FindSoldTo

if(salesOrg) masterParents = masterParents.findAll{it.attribute2 in salesOrg}
if(division) masterParents = masterParents.findAll{it.attribute4 == division}

masterParents = masterParents.collectEntries {it -> [(it.attribute7): it.attribute7 + " - " + customers?.get(it.attribute7)?.name]}

String variantMasterParent = api.global.selectedVariant?.MasterParent

def entry = libs.BdpLib.UserInputs.createInputOption(
        libs.DashboardConstantsLibrary.PricePublishing.MASTER_PARENT_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.MASTER_PARENT_INPUT_LABEL,
        false,
        false,
        null,
        masterParents
)
if((api.global.variantChanged && variantMasterParent) || (!entry.getFirstInput().getValue() && variantMasterParent)) {
    entry.getFirstInput().setValue(variantMasterParent)
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry