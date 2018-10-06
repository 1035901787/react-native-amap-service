package com.amap_service.amap_service_api

import android.view.View
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ReactShadowNode
import com.facebook.react.uimanager.ViewManager

/**
 * Created by sujialong on 2018/10/4.
 */
class AMapServicePackage: ReactPackage{

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        return listOf(
                AMapServiceModule(reactContext)
        )
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return listOf()
    }

}