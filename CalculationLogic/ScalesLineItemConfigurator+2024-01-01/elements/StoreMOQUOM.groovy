import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("MOQUOM")
api.local.MOQUOM = store.getFirstInput().getValue()

return store