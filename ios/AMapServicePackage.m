//
//  AMapServicePackage.m
//  Amap_service
//
//  Created by Arno on 2018/10/5.
//  Copyright © 2018年 Facebook. All rights reserved.
//
#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(AMapService, NSObject) //RCT_EXTERN_MODULE将模块导出到Reac-Native

  RCT_EXTERN_METHOD(
    searchQuery:(NSDictionary *)lp
    :(NSString *)keyword
    :(NSInteger *)currentPage
    :(NSInteger *)pageSize
    :(NSString *)type
    :(NSString *)city
    :(RCTPromiseResolveBlock)resolve
    :(RCTPromiseRejectBlock)reject
  )

  RCT_EXTERN_METHOD(
    inputtipsQuery:(NSString *)keyWord
    :(NSString *)city
    :(BOOL *)isCityLimit
    :(NSString *)type
    :(RCTPromiseResolveBlock)resolve
    :(RCTPromiseRejectBlock)reject
  )

  RCT_EXTERN_METHOD(
    getGeocode:(NSDictionary *)lp
    :(NSInteger *)radius
    :(RCTPromiseResolveBlock)resolve
    :(RCTPromiseRejectBlock)reject
  )

@end
