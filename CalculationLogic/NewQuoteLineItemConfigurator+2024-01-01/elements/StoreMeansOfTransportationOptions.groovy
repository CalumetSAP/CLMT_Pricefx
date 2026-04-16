import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("MeansOfTransportationOptions")
api.local.meansOfTransportationOptions = store.getFirstInput().getValue()

return store