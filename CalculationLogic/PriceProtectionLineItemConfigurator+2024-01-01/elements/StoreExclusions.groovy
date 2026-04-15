import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("Exclusions")
api.local.exclusions = store.getFirstInput().getValue()

return store