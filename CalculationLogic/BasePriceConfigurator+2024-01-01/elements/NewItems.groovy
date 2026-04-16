def entry = api.createConfiguratorEntry()

def param = api.inputBuilderFactory().createProductGroupEntry("ProductsInput")
        .setLabel("New Items")
        .setRequired(false)
        .setReadOnly(false)
        .buildContextParameter()
entry.createParameter(param)

return entry