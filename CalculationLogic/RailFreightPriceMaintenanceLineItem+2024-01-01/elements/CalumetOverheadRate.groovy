if (out.HaulCharges == null) return null

def assessorialsMap = api.global.assessorials as Map
def rrovhdMap = assessorialsMap["RROVHD"] ?: [:]
def vehicleGroup = out.MeansOfTransportation as String

def rate = (rrovhdMap[vehicleGroup] ?: rrovhdMap["*"])?.Rate

api.local.calumetOverheadRate = rate

return rate