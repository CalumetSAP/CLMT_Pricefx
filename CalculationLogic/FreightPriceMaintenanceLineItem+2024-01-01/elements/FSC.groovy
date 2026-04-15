def average = out.AverageLinehaulRate
if (!average) return null

def assessorialsMap = api.global.assessorials as Map
def fscMap = assessorialsMap["FSC"] ?: [:]
def vehicleGroup = out.MeansOfTransportation as String

def miles = out.Miles ?: BigDecimal.ZERO
def vehicleFSC = fscMap[vehicleGroup] ?: fscMap["*"]
def fsc = vehicleFSC?.Rate ?: BigDecimal.ZERO

return miles * fsc