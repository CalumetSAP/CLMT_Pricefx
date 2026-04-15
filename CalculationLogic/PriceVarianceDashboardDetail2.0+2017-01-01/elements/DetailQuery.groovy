import net.pricefx.common.api.FieldFormatType
import net.pricefx.formulaengine.DatamartContext
import net.pricefx.formulaengine.scripting.Matrix2D

if (api.isSyntaxCheck()) return

// Args
api.global.likeProduct = api.input("like_product")
api.global.year = api.input("year")
api.global.period = api.input("period")
api.global.opportunityBasis = api.input("opportunity_basis")
api.global.opportunity_basis_label = api.input("opportunity_basis_label")
api.global.pricePoint = api.input("price_point")
api.global.pricePointPercentBasisField = api.input("pricePointPercentBasisField")
api.global.selectedPercentile = api.input("selected_percentile")
api.global.fiscalYear = api.input("year")
api.global.baselinePeriod = api.input("period")
api.global.currency = api.input("currency")
def genericFilter = api.filterFromMap(api.input("genericFilter"))

//Filters
api.global.filters = api.input("filters")

//PricePoint Selected
api.global.pricePointField = api.input("pricePointField")
api.global.pricePointLabel = api.input("pricePointLabel")
api.global.numericPricePoint = api.input("numericPricePoint")

//calculated percentile
api.global.percentile90 = api.input("percentile90")
api.global.percentile75 = api.input("percentile75")
api.global.percentile50 = api.input("percentile50")
api.global.percentile25 = api.input("percentile25")
api.global.percentile10 = api.input("percentile10")

String datamartName = api.findLookupTableValues("VarianceGeneralRules").collect { it.attribute1 }?.first()
api.local.datamart = datamartName

DatamartContext dmCtx = api.getDatamartContext()
def dm = dmCtx.getDatamart(datamartName)
def query = dmCtx.newQuery(dm, true)

query.identity {
    select(Acelerator.getSummaryMapping("LikeProduct")?.field, "like_product")

    Acelerator.getDetailMappings()?.each{
        if(it.field && !it.field.contains("_divide_by_pricePoint")) select(it.field, it.element)
    }

    select("SUM(" + api.global.pricePointField +")", "total") //8
    select("" + api.global.pricePointField , "PricePoint") //9
    select(Acelerator.getSummaryMapping("qty")?.field, "Qty") //10
    select(api.global.pricePointPercentBasisField, "price_point_percent_basis")

//    select(Acelerator.getElement("MarginPercent")?.field, "MarginPercent") // 14
//    select(Acelerator.getElement("NetPrice")?.field, "NetPice") // 15

    //Periods and slice with Datamart
    //Filters
    List<String> years = new ArrayList<String>()
    List<String> periods = new ArrayList<String>()
    if ( api.global.fiscalYear instanceof Integer){
        years.add(api.global.fiscalYear)
    }else{
        years = api.global.fiscalYear
    }

    if ( api.global.baselinePeriod instanceof String){
        periods.add(api.global.baselinePeriod)
    }else{
        periods = api.global.baselinePeriod
    }

    def periodsWithYear = api.findLookupTableValues("VarianceParameters").find{
        it.name == "periodsWithYear"
    }?.attribute1 == "Y" ? true : false
    def periodsList = Acelerator.getPeriodValuesFromKeyListV2(periods, years, periodsWithYear)
    Filter periodsSliceFilter = Acelerator.createFilterForDatamartSlice(
            Acelerator.getFilter("Period2")?.field, periodsList[0],
            Acelerator.getFilter("Period1")?.field, periodsList[1]
    )

    DatamartContext.DataSlice dmSlice = dmCtx.newDatamartSlice()
    dmSlice.addFilter(periodsSliceFilter)

    query.where(dmSlice)

    if (api.global.fiscalYear) where(Filter.in(Acelerator.getFilter("Year")?.field, api.global.fiscalYear))

    where(Filter.equal(Acelerator.getSummaryMapping("LikeProduct")?.field, api.global.likeProduct))
    where(Filter.greaterThan(Acelerator.getSummaryMapping("qty")?.field, 0))
    where(Filter.isNotNull(Acelerator.getSummaryMapping("LikeProduct")?.field))
    where(Filter.greaterThan(api.global.pricePointField, 0))
    where(Filter.isNotNull(api.global.pricePointField))

    where(genericFilter)

    Acelerator.getDefaultFilters()?.each{
        if(it?.field){
            where(Acelerator.makeFilter( it ))
        }
    }

    Acelerator.getFilters()?.each { element ->
        if(element.field && !'Y'.equalsIgnoreCase(element.hide)){
            def filterValue = api.global.filters[element.field]
            if( filterValue != null && (filterValue as List && filterValue.size() > 0) ){
                if(element.multiselect == "Yes"){
                    where(Filter.in(element.field, filterValue))
                } else {
                    where(Filter.equal(element.field, filterValue))
                }
            }
        }
    }

    orderBy(Acelerator.getSummaryMapping("LikeProduct")?.field)
}

if (api.global.currency) query.setOptions(["currency": api.global.currency])

def sql = """ SELECT * FROM T1 """
Matrix2D data = dmCtx.executeSqlQuery(sql, query)

def columnLabels = [
        "price_unit"                : api.global.pricePointLabel?.endsWithAny("%", "\$") ? api.global.pricePointLabel[0..-2]?.trim() : api.global.pricePointLabel,//PricePoint
        "price_increase"            : "Pricepoint Increase %",
        "opportunity_basis"         : api.global.opportunity_basis_label,
        "divide_by_pricePoint"      : api.global.pricePointLabel + (api.global.numericPricePoint
                                            ? Acelerator.getDetailMapping("qty_divide_by_pricePoint")?.label
                                            : Acelerator.getDetailMapping("percent_divide_by_pricePoint")?.label),//PricePointPerUnit
        "Qty"                       : Acelerator.getSummaryMapping("qty")?.label,
        //"GrossMargin"               : Acelerator.getElement("MarginPercent")?.label
]

Acelerator.getDetailMappings()?.each{
    if((it.field && !it.element.contains("_divide_by_pricePoint")) || it.element == "Opportunity") columnLabels << [ (it.element) : it.label ]
}

def detail = api.newMatrix(columnLabels.collect({ column -> column.value }))
detail.setEnableClientFilter(true)

if (data) {
    for (myrow = 0; myrow < data?.getRowCount(); ++myrow) {
        def row = [:]
        def pricePoint = data.getValue(myrow, data.getColumn("total".toLowerCase()))?.toBigDecimal()
        api.local.pricePoint = pricePoint

        def percentile = api.global.selectedPercentile[api.global.likeProduct]?.toBigDecimal()

      	def divide_by_pricePoint = data.getValue(myrow, data.getColumn("PricePoint".toLowerCase())) && data.getValue(myrow, data.getColumn("Qty".toLowerCase()))
                ? data.getValue(myrow, data.getColumn("PricePoint".toLowerCase()))?.toBigDecimal() /
                (
                        api.global.numericPricePoint ? data.getValue(myrow, data.getColumn("Qty".toLowerCase()))?.toBigDecimal()
                                : data.getValue(myrow, data.getColumn("price_point_percent_basis".toLowerCase()))?.toBigDecimal()
                ) : 0

      	def priceIncreace = divide_by_pricePoint > 0 ? (percentile > divide_by_pricePoint
            ? ( api.global.numericPricePoint ? ((percentile - divide_by_pricePoint) / divide_by_pricePoint) : (percentile - divide_by_pricePoint))
            : 0) : 0

      	row[columnLabels.price_unit] = pricePoint ?: 0.0
        row[columnLabels.opportunity_basis] = percentile ?: 0.0
        row[columnLabels.price_increase] = priceIncreace
        row[columnLabels.Qty] = data.getValue(myrow, data.getColumn("Qty".toLowerCase()))?.toBigDecimal()
        row[columnLabels.divide_by_pricePoint] = divide_by_pricePoint

        def oportunity
        if(api.global.numericPricePoint){
            oportunity = divide_by_pricePoint < percentile ? (percentile - divide_by_pricePoint) * data.getValue(myrow, data.getColumn("Qty".toLowerCase()))?.toBigDecimal() : 0
        }else{
            oportunity = divide_by_pricePoint < percentile ? (pricePoint * priceIncreace)?.toBigDecimal() : 0
        }

        row[columnLabels.Opportunity] = oportunity ?: 0.0

        Acelerator.getDetailMappings()?.each{
            if(it.field && it.element == "MarginPercent") {
                row[columnLabels.get(it.element)] = data.getValue(myrow, data.getColumn(it.element.toLowerCase()))  / data.getValue(myrow, data.getColumn("price_point_percent_basis".toLowerCase()))?.toBigDecimal()
            } else if((it.field && !it.element.contains("_divide_by_pricePoint"))) {
                row[columnLabels.get(it.element)] = data.getValue(myrow, data.getColumn(it.element.toLowerCase()))
            }
        }

        detail.addRow(row)
    }
}

detail.setColumnFormat(columnLabels.price_unit, FieldFormatType.NUMERIC)
detail.setColumnFormat(columnLabels.price_increase, FieldFormatType.PERCENT)
detail.setColumnFormat(columnLabels.Qty, FieldFormatType.NUMERIC)
detail.setColumnFormat(columnLabels.divide_by_pricePoint, api.global.numericPricePoint ? FieldFormatType.NUMERIC : FieldFormatType.PERCENT)
detail.setColumnFormat(columnLabels.Opportunity, FieldFormatType.NUMERIC)
detail.setColumnFormat(columnLabels.opportunity_basis, FieldFormatType.NUMERIC)
//detail.setColumnFormat(columnLabels.GrossMargin, FieldFormatType.PERCENT)

Acelerator.getDetailMappings()?.each{
    if(it.field && it.element == "MarginPercent") {
        detail.setColumnFormat(columnLabels[it.element], FieldFormatType.PERCENT)
    }
}

return detail
