import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ShipToLines")
api.local.shipToLines = store.getFirstInput().getValue()

return store