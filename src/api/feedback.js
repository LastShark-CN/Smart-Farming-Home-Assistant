import request from '../utils/request'

export function getFeedbackList(params) {
  return request({
    url: '/feedback/list',
    method: 'get',
    params
  })
}

export function getMyFeedback(params) {
  return request({
    url: '/feedback/my',
    method: 'get',
    params
  })
}

export function getFeedbackDetail(id) {
  return request({
    url: `/feedback/${id}`,
    method: 'get'
  })
}

export function createFeedback(data) {
  return request({
    url: '/feedback',
    method: 'post',
    data
  })
}

export function updateFeedback(id, data) {
  return request({
    url: `/feedback/${id}`,
    method: 'put',
    data
  })
}

export function replyFeedback(id, reply) {
  return request({
    url: `/feedback/${id}/reply`,
    method: 'post',
    data: { reply }
  })
}

export function deleteFeedback(id) {
  return request({
    url: `/feedback/${id}`,
    method: 'delete'
  })
}

export function getServiceInfo() {
  return request({
    url: '/feedback/service-info',
    method: 'get'
  })
}

export function getFeedbackTypes() {
  return request({
    url: '/feedback/types',
    method: 'get'
  })
}
