import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("Plant")
api.local.plantOptions = store.getFirstInput().getValue()

return store