import request from '../utils/request'

export function getDeviceList(params) {
  return request({
    url: '/device',
    method: 'get',
    params
  })
}

export function getDevicePage(params) {
  return request({
    url: '/device/page',
    method: 'get',
    params
  })
}

export function getDeviceDetail(id) {
  return request({
    url: `/device/${id}`,
    method: 'get'
  })
}

export function createDevice(data) {
  return request({
    url: '/device',
    method: 'post',
    data
  })
}

export function updateDevice(id, data) {
  return request({
    url: `/device/${id}`,
    method: 'put',
    data
  })
}

export function deleteDevice(id) {
  return request({
    url: `/device/${id}`,
    method: 'delete'
  })
}

export function getDeviceTypeList() {
  return request({
    url: '/device/type',
    method: 'get'
  })
}

export function getDeviceTypeDetail(id) {
  return request({
    url: `/device/type/${id}`,
    method: 'get'
  })
}

export function createDeviceType(data) {
  return request({
    url: '/device/type',
    method: 'post',
    data
  })
}

export function updateDeviceType(id, data) {
  return request({
    url: `/device/type/${id}`,
    method: 'put',
    data
  })
}

export function deleteDeviceType(id) {
  return request({
    url: `/device/type/${id}`,
    method: 'delete'
  })
}
