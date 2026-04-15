import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("IndexNumber")
api.local.indexNumber = store.getFirstInput().getValue()

return store