import request from '../utils/request'

export function getEnvironmentData() {
  return request({
    url: '/monitor/environment',
    method: 'get'
  })
}

export function getCropProductionData() {
  return request({
    url: '/statistics/yield',
    method: 'get'
  })
}

export function getPlantingAreaData() {
  return request({
    url: '/statistics/area',
    method: 'get'
  })
}

export function getEnvironmentHistory(params) {
  return request({
    url: '/monitor/history',
    method: 'get',
    params
  })
}
