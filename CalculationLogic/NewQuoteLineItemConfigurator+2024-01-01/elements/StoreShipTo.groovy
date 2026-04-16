import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ShipTo")
api.local.shipToOptions = store.getFirstInput().getValue()

return store