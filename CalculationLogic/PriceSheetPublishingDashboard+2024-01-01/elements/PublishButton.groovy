import java.text.SimpleDateFormat

def controller = api.newController()

def configurator = out.InlineConfigurator
def template = configurator."PublishingTemplateInput"
def pricingDate = configurator."PricingDateInput"
def pricelists = configurator."PriclistsInput"
def terms = configurator."TermsInput"

controller.addHTML("<h5>Template</h5>")

SimpleDateFormat sdfOutput = new SimpleDateFormat('yyyyddMM')
def today =  sdfOutput.format(new Date())
def filename = template + "_" + today
def buttonTitle = "Download Excel"

if(out.LoadPricing){
    def parameters = [:]
    parameters['template']          = template
    parameters['pricingDate']       = pricingDate
    parameters['pricelists']        = pricelists
    parameters['terms']             = terms

    controller.addDownloadButton(buttonTitle,
            """/formulamanager.executeformula/${template}PublishingTemplate?templateName=${template}&output=xls&fileName=${filename}""",
            api.jsonEncode(parameters))
} else {
    controller.addHTML("<h5>Nothing to process</h5>")
}

return controller