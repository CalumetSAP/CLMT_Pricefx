import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("GlobalUOMTable")
api.local.globalUOMTable = store.getFirstInput().getValue()

return store