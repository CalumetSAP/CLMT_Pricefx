import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("RecalculationPeriod")
api.local.recalculationPeriodOptions = store.getFirstInput().getValue()

return store