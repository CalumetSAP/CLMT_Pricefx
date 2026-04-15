import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("Currency")
api.local.currency = store.getFirstInput().getValue()

return store