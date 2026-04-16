import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ShippingPointNames")
api.local.shippingPointNames = store.getFirstInput().getValue()

return store