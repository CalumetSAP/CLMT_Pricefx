import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("SoldToIndustry")
api.local.soldToIndustry = store.getFirstInput().getValue()

return store