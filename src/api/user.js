import request from '../utils/request'

export function updateUserProfile(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

export function updateLoginBackground(url) {
  return request({
    url: '/user/background/login',
    method: 'put',
    data: { url }
  })
}

export function updateHomeBackground(url) {
  return request({
    url: '/user/background/home',
    method: 'put',
    data: { url }
  })
}

export function getBackgroundSettings() {
  return request({
    url: '/user/background',
    method: 'get'
  })
}
