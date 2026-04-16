import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("IncotermOptions")
api.local.incotermOptions = store.getFirstInput().getValue()

return store