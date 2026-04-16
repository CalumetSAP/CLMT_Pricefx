import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ShippingPoints")
api.local.shippingPoints = store.getFirstInput().getValue()

return store