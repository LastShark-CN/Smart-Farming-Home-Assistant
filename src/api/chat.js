import request from '../utils/request'

export function sendMessage(data) {
  return request({
    url: '/chat/send',
    method: 'post',
    data
  })
}

export function getChatHistory(params) {
  return request({
    url: '/chat/history',
    method: 'get',
    params
  })
}

export function clearChatHistory() {
  return request({
    url: '/chat/clear',
    method: 'delete'
  })
}
