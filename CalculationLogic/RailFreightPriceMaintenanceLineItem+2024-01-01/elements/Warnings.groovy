import java.text.SimpleDateFormat

List<String> warnings = []

if (!api.global.countries.contains(out.DestinationCountry)) {
    warnings.add("Please check Ship to Country (it is not matching with FreightBusinessRulesCountries CPT options)")
}

def fixedAdder = out.FixedAdder
def oldAdder = out.OldAdder
def newAdder = out.NewAdder

if (fixedAdder == "Y" && oldAdder != newAdder) {
    warnings.add("Firm Adder Protection: New Adder was changed")
}

def sdf = new SimpleDateFormat("yyyy-MM-dd")
def validTo = out.NewFreightValidTo ? sdf.format(out.NewFreightValidTo) : null
def freightAgreementExpirationDate = out.LoadRailRateUploadPX.FreightAgreementExpirationDate?.toString()
if (validTo && freightAgreementExpirationDate && validTo > freightAgreementExpirationDate) {
    warnings.add("Freight Agreement Expiration happens before New Freight Valid To")
}

warnings.addAll(api.local.conversionAlerts as List)
warnings.addAll(api.local.manualOverrideWarnings as List)

if(warnings){
    String warningMsg = warnings.join("; ")
    api.yellowAlert(warningMsg)
    return warningMsg
}

return null