import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("MovementTiming")
api.local.movementTimingOptions = store.getFirstInput().getValue()

return store