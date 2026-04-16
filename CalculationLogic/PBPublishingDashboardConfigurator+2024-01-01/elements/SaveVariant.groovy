//import net.pricefx.server.dto.calculation.DashboardController
//
//DashboardController controller = api.newController()
//
//def tableName = "VariantsPricePublishingDashboard"
//
//def pp = api.findLookupTable(tableName)
//
//def VariantCPTColumns = [
//            TableId         : "lookupTableId",
//            TableName       : "lookupTableName",
//            Variant         : "key1",
//            SalesOrg        : "attribute1",
//            Division        : "attribute2",
//            Label           : "attribute3",
//            MasterParent    : "attribute4",
//            SoldTo          : "attribute5",
//            ShipTo          : "attribute6",
//            ProductHierarchy: "attribute7",
//            Brand           : "attribute8",
//            Products        : "attribute9",
//            Contract        : "attribute10",
//            ContractLine    : "attribute11",
//            Display         : "attribute12",
//            Email           : "attribute13",
//            ToEmails        : "attribute14",
//            ShowAdders      : "attribute15"
//]
//
//def ppRow = [
//        (VariantCPTColumns?.TableId)	        : pp.id,
//        (VariantCPTColumns?.TableName)			: pp.uniqueName,
//        (VariantCPTColumns?.Variant)            : out.VariantName?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.SalesOrg)           : out.SalesOrg?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.Division)           : out.Division?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.Label)              : out.Label?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.MasterParent)       : out.MasterParent?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.SoldTo)             : out.SoldTo?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.ShipTo)             : out.ShipTo?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.ProductHierarchy)   : out.ProductHierarchy?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.Brand)              : out.Brand?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.Products)           : out.Products?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.Contract)           : out.Contract?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.ContractLine)       : out.ContractLine?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.Display)            : out.Display?.getFirstInput()?.getValue(),
//        (VariantCPTColumns?.Email)              : "",
//        (VariantCPTColumns?.ToEmails)           : out.ToEmails?.getFirstInput()?.getValue(),
//]
//
//def ret = [data: ppRow]
//
//controller.addBackendCall("Save Variant", "lookuptablemanager.add/${pp.id}", api.jsonEncode(ret)?.toString())
//
//return controller