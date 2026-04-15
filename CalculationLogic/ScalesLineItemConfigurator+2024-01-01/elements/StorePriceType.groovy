import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("PriceType")
api.local.priceType = store.getFirstInput().getValue()

return store