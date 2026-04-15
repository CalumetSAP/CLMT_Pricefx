/**
 * Aborts the calculation if te required field is empty.
 */
def configurator = out.InlineConfigurator

if(!configurator  || (out.InlineConfigurator && !configurator."PublishingTemplateInput")){
    api.abortCalculation()
}