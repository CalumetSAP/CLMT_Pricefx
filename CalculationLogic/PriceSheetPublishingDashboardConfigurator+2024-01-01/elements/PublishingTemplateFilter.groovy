import net.pricefx.common.api.InputType

def fields = ['name', 'attribute1']
api.local.templates = api.findLookupTableValues("PriceSheetPublishingTemplates", fields, null).collect{ it }.unique()

def ce = api.createConfiguratorEntry(InputType.OPTION,"PublishingTemplateInput")

ce.getFirstInput().setLabel("Price Sheet Publishing Template")
ce.getFirstInput().setRequired(true)

Map templates = [:]
api.local.templates.each { template ->
    templates[template.name] = template.attribute1
}
def templatesNames = templates?.keySet()?.toList()

if(templatesNames?.size() > 0){
    ce.getFirstInput().setValueOptions(templatesNames)
    ce.getFirstInput().setConfigParameter("labels", templates)
}

return ce