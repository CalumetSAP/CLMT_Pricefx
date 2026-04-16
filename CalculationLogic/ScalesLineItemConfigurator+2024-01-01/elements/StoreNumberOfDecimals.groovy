import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("NumberOfDecimals")
api.local.numberOfDecimals = store.getFirstInput().getValue() ?: 2

return store