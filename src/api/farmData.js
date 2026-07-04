import request from '../utils/request'

export function getEnvironmentData() {
  return request({
    url: '/api/monitor/environment',
    method: 'get'
  })
}

export function getCropProductionData() {
  return request({
    url: '/api/statistics/production',
    method: 'get'
  })
}

export function getPlantingAreaData() {
  return request({
    url: '/api/statistics/area',
    method: 'get'
  })
}

export function getEnvironmentHistory(params) {
  return request({
    url: '/api/monitor/history',
    method: 'get',
    params
  })
}