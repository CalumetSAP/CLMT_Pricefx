def jobberPercent = out.JobberPercent?.toBigDecimal()
if(jobberPercent != null && jobberPercent != 1 && out.NewListPrice?.toBigDecimal()){
    return out.NewListPrice?.toBigDecimal() / (1 - jobberPercent)
}