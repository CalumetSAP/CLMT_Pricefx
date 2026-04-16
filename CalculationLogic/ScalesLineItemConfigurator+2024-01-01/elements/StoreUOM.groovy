import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("UOM")
api.local.UOM = store.getFirstInput().getValue()

return store