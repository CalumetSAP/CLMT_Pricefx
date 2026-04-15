import net.pricefx.formulaengine.DatamartContext
import net.pricefx.formulaengine.scripting.Matrix2D
import net.pricefx.common.api.chart.AuxLineColor

if (api.isSyntaxCheck()) return

def genericFilter = api.filterFromMap(api.input("genericFilter"))

def perInchPriceExpression 	= null
def perInchPriceDesc = null
if (api.global.pricePointField) {
	if(api.global.numericPricePoint){
		perInchPriceDesc = api.global.pricePointLabel + "/ Unit"
		perInchPriceExpression = "SUM(" +api.global.pricePointField + ")/SUM(${Acelerator.getChartMapping("Scatter_volume")?.field})"
	}else{
		perInchPriceDesc = api.global.pricePointLabel
		perInchPriceExpression = "SUM(" +api.global.pricePointField + ")/SUM(${Acelerator.getDetailMapping("percent_divide_by_pricePoint")?.field})"
	}
}

def listSeries = []
def markers = []

if (api.global.likeProduct  != null) {

//QUERY
	def dmCtx = api.getDatamartContext()
	def NAanalyticsDM = dmCtx.getTable(api.local.datamart)
	def query = dmCtx.newQuery(NAanalyticsDM, true)

	query.identity {
		select(Acelerator.getChartMapping("Scatter_AggregationBy")?.field, "aggregationBy")
		select(Acelerator.getChartMapping("Scatter_BandBy")?.field, "bandBy")
		select(perInchPriceExpression, "y_axis")
		select(Acelerator.getChartMapping("Scatter_volume")?.field, "x_axis")

		def additionalmeasure1 = Acelerator.getChartMapping("Scatter_additionalmeasure1")
		if(additionalmeasure1.field){
			select(additionalmeasure1.field, "Scatter_additionalmeasure1")
		}
		def additionalmeasure2 = Acelerator.getChartMapping("Scatter_additionalmeasure2")
		if(additionalmeasure2.field){
			select(additionalmeasure2.field, "Scatter_additionalmeasure2")
		}

		orderBy('x_axis')

		//Periods and slice with Datamart
		//Filters
		List<String> years = new ArrayList<String>()
		List<String> periods = new ArrayList<String>()
		if (api.global.fiscalYear instanceof Integer) {
			years.add(api.global.fiscalYear)
		} else {
			years = api.global.fiscalYear
		}

		if (api.global.baselinePeriod instanceof String) {
			periods.add(api.global.baselinePeriod)
		} else {
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

		if (api.global.currency) query.setOptions(["currency": api.global.currency])

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
	}

	def sql = """ SELECT * FROM T1 """
	Matrix2D seriesResultset = dmCtx.executeSqlQuery(sql, query)

	//Get all additional fields with format type column
	def additionalFieldsFormat = Acelerator.getChartMappings()?.inject([:]) { elements, it ->
		if(it.element?.contains("Scatter") && it.field && it.fieldType) {
			elements << [ (it.element.replace("Scatter_", "").toLowerCase()) : it.fieldType ]
		}
		return elements
	}

	def data = seriesResultset.collect {
		def row = [
				aggregationby: it.get("aggregationby"),
				x            : it.get("x_axis"),
				y            : it.get("y_axis"),
				bandby     : it.get('bandby')
		]

		additionalFieldsFormat.each { field, type ->
			def value = it.get(field)
			if (value) {
				switch(type) {
					case "Percent":
						row[field] = api.formatNumber("###,##0.00", value?.toBigDecimal().multiply(100) / it.get("x_axis")?.toBigDecimal()) + "%"
						break
					case "Money":
						row[field] = "\$" + api.formatNumber("###,##0.00", value  / it.get("x_axis")?.toBigDecimal())
						break
					default:
						row[field] = value
						break
				}
			}
		}

		return row
	}

	//logger("[MAQ] data", api.jsonEncode(data))
	def mapSeries = [:]
	def listForSiteName
	data.each {
		listForSiteName = (mapSeries.containsKey(it.bandby)) ? mapSeries[it.bandby] : []
		listForSiteName << it
		mapSeries[it.bandby] = listForSiteName
	}

	listSeries = mapSeries.collect {
		[
				name  : it.key,
				type  : 'scatter',
				marker: [radius: 3, symbol: 'circle', states: [hover: [enabled: true, lineColor: 'rgb(100,100,100)']]],
				data  : it.value
		]
	}

//logger("[MAQ] listSeries", api.jsonEncode(listSeries))
	def rowCount = seriesResultset?.getRowCount()
	def xMinVal = seriesResultset?.getRowValues(0)?.'x_axis'
	def xMaxVal = seriesResultset?.getRowValues(rowCount - 1)?.'x_axis'
	xMaxVal = xMaxVal == xMinVal ? xMaxVal+1 : xMaxVal
//	xMinVal = xMinVal == 1 ? xMinVal : xMinVal-1
//	xMaxVal = xMaxVal+1

	def auxLineColorMap = [
			(Acelerator.getSummaryMapping("price_th90")?.label): "GREEN",
			(Acelerator.getSummaryMapping("price_th75")?.label): "BLUE",
			(Acelerator.getSummaryMapping("price_th50")?.label): "GRAY",
			(Acelerator.getSummaryMapping("price_th25")?.label): "YELLOW",
			(Acelerator.getSummaryMapping("price_th10")?.label): "RED"]

	def percentilePricing = [
			(Acelerator.getSummaryMapping("price_th90")?.label): String.valueOf(api.global.percentile90.get(api.global.likeProduct)),
			(Acelerator.getSummaryMapping("price_th75")?.label): String.valueOf(api.global.percentile75.get(api.global.likeProduct)),
			(Acelerator.getSummaryMapping("price_th50")?.label): String.valueOf(api.global.percentile50.get(api.global.likeProduct)),
			(Acelerator.getSummaryMapping("price_th25")?.label): String.valueOf(api.global.percentile25.get(api.global.likeProduct)),
			(Acelerator.getSummaryMapping("price_th10")?.label): String.valueOf(api.global.percentile10.get(api.global.likeProduct))
	]

	def auxLineAttributesList = []
	percentilePricing.each { key, value ->
		def auxLineMap = [:]
		auxLineMap.label = key
		auxLineMap.color = AuxLineColor.valueOf(auxLineColorMap?.getAt(key))
		auxLineMap.yIntercept = value
		auxLineAttributesList.add(auxLineMap)
	}
	markers = auxLineAttributesList.collect {
		[
				name     : it.label,
				color    : it.color,
				type     : 'line',
				lineWidth: 2,
				data     : [
						[xMinVal, it.yIntercept.toDouble()],
						[x         : xMaxVal,
						 y         : it.yIntercept.toDouble(),
						 dataLabels: [enabled: false]
						]
				],
				tooltip  : [
						useHTML      : true,
						headerFormat : '<table>',
						pointFormat  : '<tr><th>' + it.label + ':' + api.formatNumber("#,##0.00", it.yIntercept.toDouble()) +'</th></tr>',
						footerFormat : '</table>',
						followPointer: false
				]
		]
	}

}//end if likeProduct != null

def definition = [
		chart      : [
				type    : 'scatter',
				zoomType: 'xy',
				height: 900,
				marginTop: 50
//				marginBottom: 0
		],
		title      : [text: ''],
		yAxis      : [
				title      : [text: perInchPriceDesc],
				//min: 0,
				startOnTick: true,
				endOnTick  : true,
				labels     : [
						format: '{value}'
				],
		],
		xAxis      : [title: [text: "${Acelerator.getChartMapping("Scatter_volume")?.label}"], gridLineWidth: 0,],
		legend     : [
				enabled      : true,
				verticalAlign: 'top'
		],
		plotOptions: [
				scatter: [
						marker : [radius: 3, states: [hover: [enabled: true, lineColor: 'rgb(100,100,100)']]],
						states : [hover: [marker: [enabled: false]]],
						cursor : "pointer",
						showInLegend: false
				],
				series : [
						turboThreshold: 0,
						animation     : false,
						showInLegend  : true
				]
		],
		series: [

				*markers,
				*listSeries
		],
		tooltip: [
				useHTML      : true,
				headerFormat : '<table>',
				pointFormat  : "∑${Acelerator.getChartMapping("Scatter_volume")?.label}: {point.x:,.2f} <br>" +
						"${perInchPriceDesc}: {point.y:,.2f} <br>" +
						Acelerator.getChartMappings()?.inject("") { elements, it ->
							if(it.element?.contains("Scatter") && it.element != "Scatter_volume" && it.field) {
								elements += it.label + ": {point." + it.element.replace("Scatter_", "").toLowerCase() + "} </br>"
							}
							return elements
						}
				,

				footerFormat : '</table>',
				followPointer: true
		],
]
def x = api.buildHighchart(definition)
return x
