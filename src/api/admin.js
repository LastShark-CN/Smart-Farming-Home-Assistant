import request from '../utils/request'

export function getAdminUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

export function createAdminUser(data) {
  return request({
    url: '/admin/users',
    method: 'post',
    data
  })
}

export function updateAdminUser(id, data) {
  return request({
    url: `/admin/users/${id}`,
    method: 'put',
    data
  })
}

export function deleteAdminUser(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

export function updateAdminUserStatus(id, status) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}
