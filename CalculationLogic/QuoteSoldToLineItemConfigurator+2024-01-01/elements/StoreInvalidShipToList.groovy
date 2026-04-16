import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("InvalidShipToList")
api.local.invalidShipToList = store.getFirstInput().getValue()

return store