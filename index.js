import {
  NativeModules,
  Platform
} from 'react-native';

const AMapService = NativeModules.AMapService;

const searchQuery = ({location, keyWord='', currentPage=0, pageSize=20, city='', type=''}) => {
  return AMapService.searchQuery(location, keyWord, currentPage, pageSize, type, city);
}

const inputtipsQuery = ({keyWord='', city='', isCityLimit='true', type=''}) => {
  return AMapService.inputtipsQuery(keyWord, city, isCityLimit, type);
}

const getGeocode = ({location, radius=100}) => {
  return AMapService.getGeocode(location, radius);
}

export default {
  searchQuery,
  inputtipsQuery,
  getGeocode
}