import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def required = api.local.isSalesGroup as boolean
def readOnly = !required

def dropdownOptions = getDropdownOptions(api.local.product?.MaterialPackageStyle as String)
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.SALES_SHIPPING_METHOD_ID,
            lineItemConstants.SALES_SHIPPING_METHOD_LABEL,
            required,
            readOnly,
            dropdownOptions.Options as List,
            dropdownOptions.Default,
            false
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.SALES_SHIPPING_METHOD_ID,
            InputType.HIDDEN,
            lineItemConstants.SALES_SHIPPING_METHOD_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry

def getDropdownOptions(String materialPackageStyle) {
    def options = ["Packaged", "Bulk Truck", "Bulk Rail"]
    def packagedValues = ["DRUMS", "TOTES", "CASES", "OTHER PACKAGED"]
    def bulkValues = ["BULK"]
    def defaultValue = null

    if (packagedValues.contains(materialPackageStyle?.toUpperCase())) defaultValue = "Packaged"
    if (bulkValues.contains(materialPackageStyle?.toUpperCase())) options.remove("Packaged")

    return [
            Options: options,
            Default: defaultValue,
    ]
}