import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ReferencePeriod")
api.local.referencePeriod = store.getFirstInput().getValue()

return store