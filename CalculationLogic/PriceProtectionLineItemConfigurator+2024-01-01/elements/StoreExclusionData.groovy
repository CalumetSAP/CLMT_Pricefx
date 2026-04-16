import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ExclusionData")
api.local.exclusionData = store.getFirstInput().getValue()

return store