import request from '../utils/request'

export function createPlantRecord(data) {
  return request({
    url: '/plant',
    method: 'post',
    data
  })
}

export function getPlantRecords(params) {
  return request({
    url: '/plant/list',
    method: 'get',
    params
  })
}

export function getPlantRecord(id) {
  return request({
    url: `/plant/${id}`,
    method: 'get'
  })
}

export function updatePlantRecord(id, data) {
  return request({
    url: `/plant/${id}`,
    method: 'put',
    data
  })
}

export function deletePlantRecord(id) {
  return request({
    url: `/plant/${id}`,
    method: 'delete'
  })
}

export function getCropOptions() {
  return request({
    url: '/crop/options',
    method: 'get'
  })
}
