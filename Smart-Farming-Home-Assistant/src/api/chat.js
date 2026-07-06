import request from '../utils/request'

export function sendMessage(data) {
  return request({
    url: '/api/chat/send',
    method: 'post',
    data
  })
}

export function getChatHistory() {
  return request({
    url: '/api/chat/history',
    method: 'get'
  })
}

export function clearChatHistory() {
  return request({
    url: '/api/chat/clear',
    method: 'post'
  })
}