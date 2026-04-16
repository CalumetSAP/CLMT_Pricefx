import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("C4CUOM")
api.local.c4cUOM = store.getFirstInput().getValue()

return store