import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ReadOnly")
api.local.readOnly = store.getFirstInput().getValue()

return store