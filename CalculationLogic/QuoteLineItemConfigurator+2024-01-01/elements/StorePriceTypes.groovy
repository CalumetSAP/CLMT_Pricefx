import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("PriceTypes")
api.local.priceTypes = store.getFirstInput().getValue()

return store