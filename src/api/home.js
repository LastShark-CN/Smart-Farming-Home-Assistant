import request from '../utils/request'

export function getWeather() {
  return request({
    url: '/home/weather',
    method: 'get'
  })
}

export function getCompanyInfo() {
  return request({
    url: '/home/company',
    method: 'get'
  })
}

export function getWelcomeMessage() {
  return request({
    url: '/home/welcome',
    method: 'get'
  })
}
