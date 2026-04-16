import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("FreightUOM")
api.local.freightUOMOptions = store.getFirstInput().getValue()

return store