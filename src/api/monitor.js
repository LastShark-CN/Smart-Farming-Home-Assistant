import request from '../utils/request'

export function getMonitorDataList(params) {
  return request({
    url: '/monitor/data/list',
    method: 'get',
    params
  })
}

export function getMonitorDataDetail(id) {
  return request({
    url: `/monitor/data/${id}`,
    method: 'get'
  })
}

export function createMonitorData(data) {
  return request({
    url: '/monitor/data',
    method: 'post',
    data
  })
}

export function deleteMonitorData(id) {
  return request({
    url: `/monitor/data/${id}`,
    method: 'delete'
  })
}

export function getChartDaily(params) {
  return request({
    url: '/monitor/chart/daily',
    method: 'get',
    params
  })
}

export function getChartHourly(params) {
  return request({
    url: '/monitor/chart/hourly',
    method: 'get',
    params
  })
}

export function getChartRealtime() {
  return request({
    url: '/monitor/chart/realtime',
    method: 'get'
  })
}
