import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("PackageDifferential")
api.local.packageDifferential = store.getFirstInput().getValue()

return store