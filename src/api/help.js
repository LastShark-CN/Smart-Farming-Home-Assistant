import request from '../utils/request'

export function getFaqList(params) {
  return request({
    url: '/help/faq',
    method: 'get',
    params
  })
}

export function getFaqCategories() {
  return request({
    url: '/help/faq/categories',
    method: 'get'
  })
}

export function getFaqDetail(id) {
  return request({
    url: `/help/faq/${id}`,
    method: 'get'
  })
}

export function createFaq(data) {
  return request({
    url: '/help/faq',
    method: 'post',
    data
  })
}

export function updateFaq(id, data) {
  return request({
    url: `/help/faq/${id}`,
    method: 'put',
    data
  })
}

export function deleteFaq(id) {
  return request({
    url: `/help/faq/${id}`,
    method: 'delete'
  })
}

export function getStaffList() {
  return request({
    url: '/help/staff',
    method: 'get'
  })
}

export function getStaffDetail(id) {
  return request({
    url: `/help/staff/${id}`,
    method: 'get'
  })
}

export function createStaff(data) {
  return request({
    url: '/help/staff',
    method: 'post',
    data
  })
}

export function updateStaff(id, data) {
  return request({
    url: `/help/staff/${id}`,
    method: 'put',
    data
  })
}

export function deleteStaff(id) {
  return request({
    url: `/help/staff/${id}`,
    method: 'delete'
  })
}

export function getFaqCarousel() {
  return request({
    url: '/help/carousel',
    method: 'get'
  })
}
