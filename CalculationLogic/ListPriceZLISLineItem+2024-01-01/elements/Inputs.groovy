if(api.isSyntaxCheck()){
    api.inlineConfigurator("Inputs", "ListPriceZLISConfigurator")
}else{
    return api.getParameter("Inputs")
}
