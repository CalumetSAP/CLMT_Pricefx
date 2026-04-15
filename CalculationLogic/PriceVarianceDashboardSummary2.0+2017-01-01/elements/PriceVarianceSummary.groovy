import net.pricefx.common.api.FieldFormatType
import net.pricefx.server.dto.calculation.ResultMatrix

import java.math.RoundingMode

if (api.isSyntaxCheck()) return
if (api.global.pricePointField == null) return

def opportunities = [
        "10th": api.local.opportunity10,
        "90th": api.local.opportunity90,
        "75th": api.local.opportunity75,
        "50th": api.local.opportunity50,
        "25th": api.local.opportunity25,
]

def columnLabels = [
        "like_product"              : Acelerator.getSummaryMapping("LikeProduct")?.label,
        "pricepoint"                : api.global.pricePointLabel,
]

Acelerator.getSummaryMappings()?.each{
    if(it?.element != "qty" && it?.element != "LikeProduct"){
        def mapToAdd = [:]
        if(it.element == 'OpportunityAt'){
            mapToAdd.put('selectedOpportunity', it.label)
        }else{
            mapToAdd.put(it.element, it.label)
        }
        columnLabels << mapToAdd
    }
}

api.global.columnLabels = columnLabels

ResultMatrix summary = api.newMatrix(columnLabels.collect({ column -> column.value }))
summary.setEnableClientFilter(true)

api.global.selectedOpportunity = opportunities[api.global.opportunityBasis]//Opportunity At

def holder = []

for (likeProduct in  api.global.uniqueGeneralProductNameList?.sort()){//api.global.likeProductsList?.sort()) {
    def row = [:]

    row[columnLabels.like_product] = likeProduct
    // row[columnLabels.pricepoint] = api.formatNumber(".00", api.local.sum[likeProduct] ?: 0.0)
    if(api.global.numericPricePoint){
        row[columnLabels.pricepoint] = libs.SharedLib.RoundingUtils.round(api.local.sum[likeProduct]?.toBigDecimal() ?: 0.0, 0)
    }else{
        row[columnLabels.pricepoint] = (api.local.sum[likeProduct]?.toBigDecimal() ?: 0.0) / (api.local.sum_percent_basis[likeProduct]?.toBigDecimal() ?: 0.0)
    }

    Acelerator.getSummaryMappings()?.each{
        if(it?.element != "qty" && it?.element != "LikeProduct"){
            if(it.element == 'OpportunityAt'){
                row[columnLabels.selectedOpportunity] = api.global.selectedOpportunity[likeProduct] ?: BigDecimal.ZERO
            }else{
//                if(it.field == 'Numeric'){
//                    row[columnLabels[it.element]] = api.local[it.element][likeProduct] ?: BigDecimal.ZERO
//                    summary.setColumnFormat(columnLabels[it.element], FieldFormatType.NUMERIC)
//                }else{
                    row[columnLabels[it.element]] = api.local[it.element][likeProduct]
//                }
            }
        }
    }

//    row[columnLabels.variation] = api.local.variation[likeProduct]

    if(api.global.selectedOpportunity)
        row[columnLabels.selectedOpportunity] = api.global.selectedOpportunity[likeProduct]?.toDouble()?.round(0)

    row[columnLabels.min_price] = api.local.min_price[likeProduct]?.toDouble()
    row[columnLabels.max_price] = api.local.max_price[likeProduct]?.toDouble()
    row[columnLabels.price_th90] = api.local.price_th90[likeProduct]?.toDouble()
    row[columnLabels.price_th75] = api.local.price_th75[likeProduct]?.toDouble()
    row[columnLabels.price_th50] = api.local.price_th50[likeProduct]?.toDouble()
    row[columnLabels.price_th25] = api.local.price_th25[likeProduct]?.toDouble()
    row[columnLabels.price_th10] = api.local.price_th10[likeProduct]?.toDouble()
//    row[columnLabels.count_sold_tos] = api.local.count_sold_tos[likeProduct]
//    row[columnLabels.count_sites] = api.local.count_sites[likeProduct]

    holder.add(row)
   // summary.addRow(row)
}

holder?.sort({ b, a -> a[columnLabels.selectedOpportunity] <=> b[columnLabels.selectedOpportunity] })

for (item in holder) {
    summary.addRow(item)
}

summary.setColumnFormat(columnLabels.selectedOpportunity, FieldFormatType.NUMERIC)
//summary.setColumnFormat(columnLabels.count_sold_tos, FieldFormatType.INTEGER)
//summary.setColumnFormat(columnLabels.count_sites, FieldFormatType.INTEGER)
summary.setColumnFormat(columnLabels.min_price, FieldFormatType.NUMERIC)
summary.setColumnFormat(columnLabels.max_price, FieldFormatType.NUMERIC)
summary.setColumnFormat(columnLabels.price_th90, FieldFormatType.NUMERIC)
summary.setColumnFormat(columnLabels.price_th75, FieldFormatType.NUMERIC)
summary.setColumnFormat(columnLabels.price_th50, FieldFormatType.NUMERIC)
summary.setColumnFormat(columnLabels.price_th25, FieldFormatType.NUMERIC)
summary.setColumnFormat(columnLabels.price_th10, FieldFormatType.NUMERIC)
if(api.global.numericPricePoint){
    summary.setColumnFormat(columnLabels.pricepoint, FieldFormatType.NUMERIC)
}else{
    summary.setColumnFormat(columnLabels.pricepoint, FieldFormatType.PERCENT)
}

summary.onRowSelection().triggerEvent(api.dashboardWideEvent("PriceVarianceSummaryLikeProductChanged"))
        .withColValueAsEventDataAttr(columnLabels.like_product, "like_product")

return summary