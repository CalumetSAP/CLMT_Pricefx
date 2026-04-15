import net.pricefx.common.api.pa.DataType
import net.pricefx.formulaengine.DatamartContext
import net.pricefx.formulaengine.DatamartQueryResult
import net.pricefx.formulaengine.TableContext

if (api.isSyntaxCheck()) return
if (api.global.pricePointField == null) return

api.global.tableCtx = api.getTableContext()

//Setting filters into api.global
api.global.fiscalYear = out.FiscalYearFilter
api.global.baselinePeriod = out.BaselinePeriodFilter
api.global.opportunityBasis = api.global.selectedOpp
api.global.pricePoint = out.PricePointFilter
api.global.currency = out.CurrencyFilter
api.global.filters = [:]
Filter genericFilter = api.getElement("GenericFilter")
api.global.genericFilter = out.GenericFilter

Acelerator.getFilters()?.each{ element ->
    if(element.field && !'Y'.equalsIgnoreCase(element.hide)){
        api.global.filters[element?.field] = api.input(element?.field)
    }
}

//Main variables (context)
DatamartContext datamartCtx = api.getDatamartContext()

def salesDM = datamartCtx.getDatamart(api.local.datamart)
DatamartContext.Query query = datamartCtx.newQuery(salesDM, true)

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

//use the dataslice to add filters with OR
DatamartContext.DataSlice dmSliceFilteredByPeriods = datamartCtx.newDatamartSlice()
dmSliceFilteredByPeriods.addFilter(periodsSliceFilter)

query.identity {
    select(Acelerator.getSummaryMapping("LikeProduct")?.field, "like_product")//0
    select("SUM(" + api.global.pricePointField +")", "price_point")//1
    select("SUM(" + api.global.pricePointPercentBasisField +")", "price_point_percent_basis")//1
    select(Acelerator.getSummaryMapping("qty")?.field, "invoice_base_qty") //2
    select(Acelerator.getSummaryMapping("sold_to")?.field, "sold_account_number") //3
    select(Acelerator.getSummaryMapping("shipTo")?.field, "site_name") //4
    select(Acelerator.getDetailMapping("Transaction_Id")?.field, "transaction_id") // TEST TTTTTTTT

    where(dmSliceFilteredByPeriods)
    where(genericFilter)
    where(Filter.greaterThan(api.global.pricePointField, 0))

    if (api.global.fiscalYear) where(Filter.in(Acelerator.getFilter("Year")?.field, api.global.fiscalYear))

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

DatamartQueryResult result = datamartCtx.executeQuery(query)
def values = result.data?.getColumnValues(0)?.unique()
values.removeAll([null])
api.global.uniqueGeneralProductNameList = values

def tableFields = ["like_product": DataType.STRING,
                   "price_point": DataType.NUMBER,
                   "invoice_base_qty": DataType.NUMBER,
                   "sold_account_number": DataType.STRING,
                   "site_name": DataType.STRING,
                   "price_point_percent_basis": DataType.NUMBER,
                   "transaction_id": DataType.STRING]

def indexFields = ["like_product"]
TableContext tableContext = api.global.tableCtx
tableContext.createTable("transactions", tableFields, indexFields)

def rowsAsListOfMaps = result.getData().toResultMatrix().getEntries().collect { row ->
    [
            "like_product": row["like_product"],
            "price_point": row["price_point"],
            "invoice_base_qty": row["invoice_base_qty"],
            "sold_account_number": row["sold_account_number"],
            "site_name": row["site_name"],
            "price_point_percent_basis": row["price_point_percent_basis"],
            "transaction_id": row["transaction_id"]
    ]
}
api.logInfo("FERCAMM rowsAsListOfMaps.size()", rowsAsListOfMaps.size())

tableContext.loadRows("transactions", rowsAsListOfMaps)

