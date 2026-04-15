import net.pricefx.common.api.InputType

def today = new Date().format("yyyy-MM-dd")

def ce = api.createConfiguratorEntry(InputType.DATEUSERENTRY,"PricingDateInput")
ce.getFirstInput().setLabel("Pricing Date")
ce.getFirstInput().setRequired(true)
if(!ce.getFirstInput().getValue() || api.local.TemplateChange) {
    ce.getFirstInput().setValue(today)
}

return ce