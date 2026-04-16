import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("CostPX")
api.local.costPX = store.getFirstInput().getValue()

return store