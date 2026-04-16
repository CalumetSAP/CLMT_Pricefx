import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("PriceUOM")
api.local.priceUOM = store.getFirstInput().getValue()

return store