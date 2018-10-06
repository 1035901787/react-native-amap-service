//
//  AMapService.swift
//  Amap_service
//
//  Created by Arno on 2018/10/5.
//  Copyright © 2018年 Facebook. All rights reserved.
//

@objc(AMapService)
class AMapService:NSObject, AMapSearchDelegate {
  
  var search: AMapSearchAPI!
  var radius: Int = 3000 // Poi搜索半径
  var searchQueryPrmResolve: RCTPromiseResolveBlock! //周边搜索promise成功回调
  var searchQueryPrmReject: RCTPromiseRejectBlock! //周边搜索promise失败回调
  var inputtipsQueryPrmResolve: RCTPromiseResolveBlock! //输入搜索promise成功回调
  var inputtipsQueryPrmReject: RCTPromiseRejectBlock! //输入搜索promise失败回调
  var geocoderPrmResolve: RCTPromiseResolveBlock! //逆编码promise成功回调
  var geocoderPrmReject: RCTPromiseRejectBlock! //逆编码promise失败回调
  
  
  override init() {
    super.init();
  }
  
  func initSearch() {
    if(search == nil){
      search = AMapSearchAPI()
      search.delegate = self
    }
  }
  
  @objc func searchQuery(_ lp: Dictionary<String, NSNumber>, _ keyword: String, _ currentPage: Int,
     _ pageSize: Int, _ type: String="", _ city: String, _ resolve:@escaping RCTPromiseResolveBlock,
     _ reject: @escaping RCTPromiseRejectBlock) {
    
    searchQueryPrmResolve = resolve;
    searchQueryPrmReject = reject;
    initSearch();
    let request = AMapPOIAroundSearchRequest()
//    request.tableID = TableID
    
    request.location = AMapGeoPoint.location(withLatitude: CGFloat(lp["latitude"]!), longitude: CGFloat(lp["longitude"]!))
    request.keywords = keyword
    request.city = city
    request.radius = radius
    request.requireExtension = true
    request.types = type
    
    search.aMapPOIAroundSearch(request)
  }
  
  func onPOISearchDone(_ request: AMapPOISearchBaseRequest!, response: AMapPOISearchResponse!) {
    
    var poiList = Array<Dictionary<String, Any>>();
    if response.count > 0 {
      for aPOI in response.pois {
        var attr = Dictionary<String, Any>();
        attr["adCode"] = aPOI.adcode;
        attr["adName"] = aPOI.district;
        attr["address"] = aPOI.address;
        attr["businessArea"] = aPOI.businessArea;
        attr["cityCode"] = aPOI.citycode;
        attr["cityName"] = aPOI.city;
        attr["direction"] = aPOI.direction;
        attr["email"] = aPOI.email;
        attr["parkingType"] = aPOI.parkingType;
        attr["poiId"] = aPOI.uid;
        attr["postcode"] = aPOI.postcode;
        attr["provinceCode"] = aPOI.pcode;
        attr["provinceName"] = aPOI.province;
        attr["shopID"] = aPOI.shopID;
        attr["snippet"] = "";
        attr["tel"] = aPOI.tel;
        attr["title"] = aPOI.name;
        attr["typeCode"] = aPOI.typecode;
        attr["typeDes"] = aPOI.type;
        attr["website"] = aPOI.website;
        attr["distance"] = aPOI.distance;
        attr["latitude"] = aPOI.location.latitude;
        attr["longitude"] = aPOI.location.longitude;
        poiList.append(attr);
      }
    }
    let res = [ "code": 1000, "poiList": poiList] as [String : Any];
    searchQueryPrmResolve(res);
  }
  
  @objc func inputtipsQuery(_ keyWord: String, _ city:String, _ isCityLimit: Bool, _ type: String,
    _ resolve:@escaping RCTPromiseResolveBlock,  _ reject: @escaping RCTPromiseRejectBlock) {
    
      inputtipsQueryPrmResolve = resolve;
      inputtipsQueryPrmReject = reject;
      initSearch();
      let request = AMapInputTipsSearchRequest()
      request.keywords = keyWord
      request.city = city
      request.cityLimit = isCityLimit
      request.types = type
      search.aMapInputTipsSearch(request)
  }
  
  func onInputTipsSearchDone(_ request: AMapInputTipsSearchRequest!, response: AMapInputTipsSearchResponse!) {
    
    var poiList = Array<Dictionary<String, Any>>()
    if response.count > 0 {
      for aTip in response.tips {
        var attr = Dictionary<String, Any>();
        attr["adCode"] = aTip.adcode;
        attr["address"] = aTip.address;
        attr["district"] = aTip.district;
        attr["name"] = aTip.name;
        attr["title"] = aTip.name;
        attr["poiId"] = aTip.uid;
        attr["typeCode"] = aTip.typecode;
        attr["latitude"] = aTip.location.latitude;
        attr["longitude"] = aTip.location.longitude;
        poiList.append(attr)
      }
    }
    let res = [ "code": 1000, "inputtipsList": poiList] as [String : Any];
    inputtipsQueryPrmResolve(res);
  }
  
  @objc func getGeocode(_ lp: Dictionary<String, NSNumber>, _ radius: Int,
    _ resolve:@escaping RCTPromiseResolveBlock,  _ reject: @escaping RCTPromiseRejectBlock) {
    
    geocoderPrmResolve = resolve;
    geocoderPrmReject = reject;
    initSearch();
    let request = AMapReGeocodeSearchRequest();
    request.location = AMapGeoPoint.location(withLatitude: CGFloat(lp["latitude"]!), longitude: CGFloat(lp["longitude"]!));
    request.requireExtension = true;
    request.radius = radius;
    search.aMapReGoecodeSearch(request);
    
  }
  
  func onReGeocodeSearchDone(_ request: AMapReGeocodeSearchRequest!, response: AMapReGeocodeSearchResponse!) {
    
    var attrs = Dictionary<String, Any>();
    if (response.regeocode != nil) {
      let regeocode = response.regeocode;
      let addressComponen =  response.regeocode.addressComponent;
      let location = request.location;
      attrs["formatAddress"] = regeocode?.formattedAddress;
      attrs["adCode"] = addressComponen?.adcode;
      attrs["building"] = addressComponen?.building;
      attrs["city"] = addressComponen?.city;
      attrs["cityCode"] = addressComponen?.citycode;
      attrs["building"] = addressComponen?.building;
      attrs["district"] = addressComponen?.district;
      attrs["neighborhood"] = addressComponen?.neighborhood;
      attrs["province"] = addressComponen?.province;
      attrs["towncode"] = addressComponen?.towncode;
      attrs["latitude"] = location?.latitude;
      attrs["longitude"] = location?.longitude;
    }
    
    let res = [ "code": 1000, "regeocodeAddres": attrs] as [String : Any];
    geocoderPrmResolve(res);
  }
  
  //搜索检索失败
  func aMapSearchRequest(_ request: Any!, didFailWithError error: Error!) {

    if(inputtipsQueryPrmReject != nil) {
      inputtipsQueryPrmReject("-1", "Error:\(error)", error);
    }
    if(searchQueryPrmReject != nil) {
      searchQueryPrmReject("-1", "Error:\(error)", error);
    }
    if(geocoderPrmReject != nil) {
      geocoderPrmReject("-1", "Error:\(error)", error);
    }
    
  }
  
}
