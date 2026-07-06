import request from '../utils/request'

export function getControlLogs(params) {
  return request({
    url: '/device/control/logs',
    method: 'get',
    params
  })
}

export function getMyControlLogs(params) {
  return request({
    url: '/device/control/logs/my',
    method: 'get',
    params
  })
}

export function getControlLogDetail(id) {
  return request({
    url: `/device/control/logs/${id}`,
    method: 'get'
  })
}

export function controlDeviceAction(data) {
  return request({
    url: '/device/control/action',
    method: 'post',
    data
  })
}

export function turnOnDevice(deviceId) {
  return request({
    url: `/device/control/turn-on/${deviceId}`,
    method: 'post'
  })
}

export function turnOffDevice(deviceId) {
  return request({
    url: `/device/control/turn-off/${deviceId}`,
    method: 'post'
  })
}

export function toggleDevice(deviceId) {
  return request({
    url: `/device/control/toggle/${deviceId}`,
    method: 'post'
  })
}

export function deleteControlLog(id) {
  return request({
    url: `/device/control/logs/${id}`,
    method: 'delete'
  })
}
