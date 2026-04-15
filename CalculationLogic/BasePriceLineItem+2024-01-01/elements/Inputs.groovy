if(api.isSyntaxCheck()){
    api.inlineConfigurator("Inputs", "BasePriceConfigurator")
}else{
    return api.getParameter("Inputs")
}
