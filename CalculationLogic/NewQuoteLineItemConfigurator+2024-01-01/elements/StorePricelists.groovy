import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("Pricelists")
api.local.pricelists = store.getFirstInput().getValue()

return store