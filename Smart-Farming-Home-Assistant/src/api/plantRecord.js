import request from '../utils/request'

export function createPlantRecord(data) {
  return request({
    url: '/api/plant-records',
    method: 'post',
    data
  })
}

export function getPlantRecords(params) {
  return request({
    url: '/api/plant-records',
    method: 'get',
    params
  })
}

export function getPlantRecord(id) {
  return request({
    url: `/api/plant-records/${id}`,
    method: 'get'
  })
}

export function updatePlantRecord(id, data) {
  return request({
    url: `/api/plant-records/${id}`,
    method: 'put',
    data
  })
}

export function deletePlantRecord(id) {
  return request({
    url: `/api/plant-records/${id}`,
    method: 'delete'
  })
}

export function getCropOptions() {
  return request({
    url: '/api/crops/options',
    method: 'get'
  })
}