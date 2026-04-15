import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("Division")
api.local.division = store.getFirstInput().getValue()

return store