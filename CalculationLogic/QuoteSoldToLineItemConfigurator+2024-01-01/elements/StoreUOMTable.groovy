import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("UOMTable")
api.local.uomTable = store.getFirstInput().getValue()

return store