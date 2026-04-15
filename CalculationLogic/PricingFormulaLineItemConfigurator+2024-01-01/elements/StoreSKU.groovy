import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("sku")
api.local.sku = store.getFirstInput().getValue()

return store