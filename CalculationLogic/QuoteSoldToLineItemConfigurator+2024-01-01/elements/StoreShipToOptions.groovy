import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ShipToOptions")
api.local.shipToOptions = store.getFirstInput().getValue()

return store