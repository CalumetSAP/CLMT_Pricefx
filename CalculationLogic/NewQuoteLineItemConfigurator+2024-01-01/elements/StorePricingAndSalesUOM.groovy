import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("PricingAndSalesUOM")
api.local.uomOptions = store.getFirstInput().getValue()

return store