import net.pricefx.formulaengine.DatamartContext
import net.pricefx.formulaengine.scripting.Matrix2D
if (api.isSyntaxCheck()) return

def genericFilter = api.filterFromMap(api.input("genericFilter"))

int binsCount = 8

def perInchPriceExpression 	= null
def perInchPriceDescription = null
if (api.global.pricePointField) {
  if(api.global.numericPricePoint){
    perInchPriceDescription = api.global.pricePointLabel + "/ Unit"
    perInchPriceExpression = "SUM(" +api.global.pricePointField + ")/SUM(${Acelerator.getSummaryMapping("qty")?.field})"
  }else{
    perInchPriceDescription = api.global.pricePointLabel
    perInchPriceExpression = "SUM(" +api.global.pricePointField + ")/SUM(${Acelerator.getDetailMapping("percent_divide_by_pricePoint")?.field})*100"
  }
}

def serieCompleta = []
def xAxis = []
def yAxis = []
def zAxis = []

//QUERY
if (api.global.likeProduct != null) {
  def dmCtx = api.getDatamartContext()

  def NAanalyticsDM = dmCtx.getTable(api.local.datamart)
  def query  = dmCtx.newQuery(NAanalyticsDM,true)

  query.identity {
    select(Acelerator.getChartMapping("Histogram_y_axis")?.field, "y_axis")

    select(api.global.pricePointField, 'pricePoint')
    select(perInchPriceExpression, "pricePointPerInch")  //x
    select("SUM(" + api.global.pricePointField + ")", "SumPricePoint")  //z
    orderBy('pricePointPerInch')

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

    where(genericFilter)

    if (api.global.fiscalYear) where(Filter.in(Acelerator.getFilter("Year")?.field, api.global.fiscalYear))

    where(Filter.equal(Acelerator.getSummaryMapping("LikeProduct")?.field, api.global.likeProduct))
    where(Filter.greaterThan(Acelerator.getSummaryMapping("qty")?.field, 0))
    where(Filter.isNotNull(Acelerator.getSummaryMapping("LikeProduct")?.field))
    where(Filter.greaterThan(api.global.pricePointField, 0))
    where(Filter.isNotNull(api.global.pricePointField))

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

  def data2 = seriesResultset.collect{
    [
      "pricepoint" : it.pricepointperinch?.setScale(5, BigDecimal.ROUND_DOWN),
      "y_axis" : it.y_axis,
      "sumpricepoint" : it.sumpricepoint
    ]
  }.sort{x,y ->
    x.pricepoint <=> y.pricepoint
  }

  if(data2.size() > 0) {

    BigDecimal  firstValue = data2?.first().pricepoint
    BigDecimal  lastValue = data2?.last().pricepoint

    double diff = ((lastValue - firstValue)/binsCount)//?.setScale(5, BigDecimal.ROUND_DOWN)

    BigDecimal fromValue = firstValue.setScale(2, BigDecimal.ROUND_DOWN)
    BigDecimal toValue
    def i
    def siteCount = 0
    BigDecimal amountSum = 0
    for (i = 0; i< binsCount; i++) {
      toValue = (new BigDecimal(fromValue + diff)).setScale(2, BigDecimal.ROUND_UP)
      siteCount = 0
      amountSum = 0
      data2?.each {
        if (((it.pricepoint >= fromValue) || (i == 0 && it.pricepoint >= firstValue))
            && ((it.pricepoint <= toValue) || (i == binsCount-1 && it.pricepoint <= lastValue))) {
          siteCount++
            amountSum += it.sumpricepoint
        }
      }
      serieCompleta << [
        "from" : fromValue.setScale(2, BigDecimal.ROUND_UP),
        "to" : toValue.setScale(2, BigDecimal.ROUND_UP),
        "siteCount" : siteCount,
        "amountSum" : (amountSum > 0) ? amountSum?.setScale(2, BigDecimal.ROUND_HALF_DOWN) : null
      ]
      fromValue = toValue.setScale(2, BigDecimal.ROUND_DOWN)

      if(!diff){
        break
      }
    }
    xAxis = serieCompleta?.collect{
      it.from + " → " + it.to
    }
    yAxis = serieCompleta?.collect{
      it.siteCount

    }
    zAxis = serieCompleta?.collect{
      it.amountSum
    }

  }
  //logger("[MAQ] serieCompleta", serieCompleta)
}

def definition = [
  chart: [
    type: 'column',
    height: 900,
    marginTop: 25
  ],
  title: [text: ''],
  xAxis: [
    [
      categories: xAxis,
      crosshair: true,
      title: [ text: perInchPriceDescription ],
      alignTicks: true,
      opposite: false
    ]
  ],
  yAxis: [
    [
      title: [ text: '∑'+ (api.global.pricePointLabel?.endsWithAny("%", "\$") ? api.global.pricePointLabel[0..-2]?.trim() : api.global.pricePointLabel)  + " " + (api.global.currency ?:"")],
      opposite: true
    ],
    [
      title: [ text: "Price Variance - Histogram" ]

    ]
  ],
  tooltip: [
    headerFormat: '<span style="font-size:12px">'+perInchPriceDescription+': {point.key}</span><table>',
    pointFormat: '<tr><td>{series.name}: {point.y} </td>' +
    '</tr>',
    footerFormat: '</table>',
    shared: true,
    useHTML: true,
    followPointer: true
  ],
  plotOptions: [
    series: [
      connectNulls: true
    ],
    column: [
      pointPadding: 0.001,
      /* borderWidth: 1, */
      groupPadding: 0,
      shadow: false
    ]
  ],
  series: [
    [
      name:

      Acelerator.getChartMappings()?.inject("") { elements, it ->
        if(it.element == "Histogram_y_axis") {
          elements += it.label
        }
        return elements
      }
      ,
      type: "column",
      data: yAxis,
      yAxis: 1,
    ],
    [
      type: "line",
      name: "∑"+ (api.global.pricePointLabel?.endsWithAny("%", "\$") ? api.global.pricePointLabel[0..-2]?.trim() : api.global.pricePointLabel) + " " + (api.global.currency ?: ""),
      data: zAxis,
      color: "black",
    ]
  ]
]


def histogram = api.buildHighchart(definition)

return histogram
