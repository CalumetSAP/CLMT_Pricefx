if (out.HaulCharges == null) return null

def assessorialsMap = api.global.assessorials as Map
def fscMap = assessorialsMap["FSC"] ?: [:]
def vehicleGroup = out.MeansOfTransportation as String

def kms = out.RouteDistanceKM ?: BigDecimal.ZERO
def vehicleFSC = fscMap[vehicleGroup] ?: fscMap["*"]
def fsc = vehicleFSC?.Rate ?: BigDecimal.ZERO

return kms * fsc