import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("CompetitiveSituation")
api.local.competitiveSituation = store.getFirstInput().getValue()

return store