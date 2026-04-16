import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("IsSoldToOnly")
api.local.isSoldToOnly = store.getFirstInput().getValue()

return store