def scaleQtyAux, priceAux
def scales = []

for (int i = 0; i < 5; i++) {
    scaleQtyAux = out.LoadQuoteScales?.getAt(i)?.ScaleQty
    priceAux = out.LoadQuoteScales?.getAt(i)?.Price
    if (scaleQtyAux && priceAux) {
        scales.add([
                scaleQty: scaleQtyAux,
                price: priceAux
        ])
    }
}

scales.sort { it.scaleQty }
return scales.collect { "${it.scaleQty}=${it.price}" }.join("|")
