package com.amap_service.amap_service_api

import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.facebook.react.bridge.*

/**
 * Created by sujialong on 2018/10/4.
 */
class AMapServiceModule(private val reactContext: ReactApplicationContext): ReactContextBaseJavaModule(reactContext),
        PoiSearch.OnPoiSearchListener, Inputtips.InputtipsListener, GeocodeSearch.OnGeocodeSearchListener {

    private var query: PoiSearch.Query = PoiSearch.Query("", "", "");// Poi查询条件类
    private var radius: Int = 3000;// Poi搜索半径
    private var searchPoiPromis: Promise? = null;// Poi搜索回调
    private var inputtipsPromis: Promise? = null;// 关键字输入搜索回调
    private var geocoderPromis: Promise? = null;// 逆编码输入搜索回调
    private var geocoderSearch: GeocodeSearch? = null; //逆编码

    override fun getName(): String {
        return "AMapService"
    }

    @ReactMethod
    fun searchQuery(lp: ReadableMap, keyWord: String="", currentPage: Int=0, pageSize: Int=20,
                    type: String="", city: String="", prm: Promise) {
        searchPoiPromis = prm;
        val newKeyWord = keyWord.toString().trim();
        query = PoiSearch.Query(newKeyWord, type, city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(pageSize)// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage)// 设置查第一页

        if (lp != null) {
            val lp = LatLonPoint(lp.getDouble("latitude"), lp.getDouble("longitude"));
            val poiSearch: PoiSearch = PoiSearch(reactContext, query)
            poiSearch.setOnPoiSearchListener(this)
            poiSearch.setBound(PoiSearch.SearchBound(lp, radius, true))//
            // 设置搜索区域为以lp点为圆心，其周围3000米范围
            poiSearch.searchPOIAsyn()// 异步搜索
        }
    }

    override fun onPoiSearched(result: PoiResult?, rcode: Int) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            val map = Arguments.createMap();
            val poiItemAry = Arguments.createArray();
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery() == query) {// 是否是同一条
                    val poiItems = result.getPois()// 取得第一页的poiitem数据，页数从数字0开始
                    val suggestionCities = result
                            .getSearchSuggestionCitys()// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size > 0) {
                        for (obj: PoiItem in poiItems) {
                            val attr = Arguments.createMap();
                            val latLonPoint = obj.latLonPoint;
                            attr.putString("adName", obj.adName);
                            attr.putString("adCode", obj.adCode);
                            attr.putString("businessArea", obj.businessArea);
                            attr.putString("cityCode", obj.cityCode);
                            attr.putString("cityName", obj.cityName);
                            attr.putString("direction", obj.direction);
                            attr.putString("email", obj.email);
                            attr.putString("parkingType", obj.parkingType);
                            attr.putString("poiId", obj.poiId);
                            attr.putString("postcode", obj.postcode);
                            attr.putString("provinceCode", obj.provinceCode);
                            attr.putString("provinceName", obj.provinceName);
                            attr.putString("shopID", obj.shopID);
                            attr.putString("snippet", obj.snippet);
                            attr.putString("tel", obj.tel);
                            attr.putString("title", obj.title);
                            attr.putString("typeCode", obj.typeCode);
                            attr.putString("typeDes", obj.typeDes);
                            attr.putString("website", obj.website);
                            attr.putInt("distance", obj.distance);
                            attr.putDouble("latitude", latLonPoint.latitude);
                            attr.putDouble("longitude", latLonPoint.longitude);
                            poiItemAry.pushMap(attr);
                        }
                    }
                }
            }
            map.putInt("code", rcode);
            map.putArray("poiList", poiItemAry)
            searchPoiPromis?.resolve(map);
        } else {
            searchPoiPromis?.reject("$rcode", "周边搜索失败")
        }
    }

    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @ReactMethod
    fun inputtipsQuery(keyWord: String="",city:String="", isCityLimit: Boolean=true, type: String="", prm: Promise) {
        inputtipsPromis = prm;
        val newText = keyWord.toString().trim();
        val inputquery = InputtipsQuery(newText, city)
        val inputTips = Inputtips(reactContext, inputquery)
        inputquery.setCityLimit(isCityLimit);//是否限制在当前城市
        inputquery.setType(type); //搜索类型
        inputTips.setInputtipsListener(this)
        inputTips.requestInputtipsAsyn()
    }

    override fun onGetInputtips(tipList: List<Tip>, rCode: Int) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {// 正确返回
            val map = Arguments.createMap();
            val poiItemAry = Arguments.createArray();
            for (obj: Tip in tipList) {
                val attr = Arguments.createMap();
                val latLonPoint = obj.point;
                attr.putString("adCode", obj.adcode);
                attr.putString("address", obj.address);
                attr.putString("district", obj.district);
                attr.putString("name", obj.name);
                attr.putString("title", obj.name);
                attr.putString("poiId", obj.poiID);
                attr.putString("typeCode", obj.typeCode);
                attr.putDouble("latitude", latLonPoint.latitude);
                attr.putDouble("longitude", latLonPoint.longitude);
                poiItemAry.pushMap(attr);
            }
            map.putInt("code", rCode);
            map.putArray("inputtipsList", poiItemAry)
            inputtipsPromis?.resolve(map);
        } else {
            inputtipsPromis?.reject(rCode.toString(), "输入关键字搜索失败")
        }
    }

    @ReactMethod
    fun getGeocode(lp: ReadableMap, radius: Float=100f, prm: Promise) {
        geocoderPromis = prm;
        if(geocoderSearch == null) {
            geocoderSearch = GeocodeSearch(reactContext);
            geocoderSearch?.setOnGeocodeSearchListener(this);
        }
        val latLonPoint = LatLonPoint(lp.getDouble("latitude"), lp.getDouble("longitude"));
        val query = RegeocodeQuery(latLonPoint, radius,
                GeocodeSearch.AMAP)// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch?.getFromLocationAsyn(query)// 设置异步逆地理编码请求
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRegeocodeSearched(result: RegeocodeResult?, rCode: Int) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            val map = Arguments.createMap();
            val attrMap = Arguments.createMap();
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                val regeocodeAddres = result.regeocodeAddress;
                val regeocodeQuery = result.regeocodeQuery;
                val point = regeocodeQuery.point;
                attrMap.putString("formatAddress", regeocodeAddres.formatAddress);
                attrMap.putString("adCode", regeocodeAddres.adCode);
                attrMap.putString("building", regeocodeAddres.building);
                attrMap.putString("city", regeocodeAddres.city);
                attrMap.putString("cityCode", regeocodeAddres.cityCode);
                attrMap.putString("district", regeocodeAddres.district);
                attrMap.putString("neighborhood", regeocodeAddres.neighborhood);
                attrMap.putString("province", regeocodeAddres.province);
                attrMap.putString("towncode", regeocodeAddres.towncode);
                attrMap.putDouble("latitude", point.latitude);
                attrMap.putDouble("longitude", point.longitude);
            }
            map.putInt("code", rCode);
            map.putMap("regeocodeAddres", attrMap);
            geocoderPromis?.resolve(map)
        } else {
            geocoderPromis?.reject(rCode.toString(), "逆编码搜索失败")
        }
    }

}