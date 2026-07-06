function generateRandomValue(min, max, decimals = 1) {
  return Number((Math.random() * (max - min) + min).toFixed(decimals))
}

function generateHistoryData(hours = 24) {
  const data = []
  const now = Date.now()
  for (let i = hours; i >= 0; i--) {
    const time = new Date(now - i * 3600000)
    data.push({
      time: `${time.getHours().toString().padStart(2, '0')}:00`,
      temperature: generateRandomValue(20, 32),
      humidity: generateRandomValue(40, 85),
      co2: generateRandomValue(300, 800)
    })
  }
  return data
}

export const environmentData = {
  code: 200,
  message: 'success',
  data: {
    temperature: {
      value: generateRandomValue(22, 28),
      unit: '°C',
      status: 'normal',
      range: { min: 15, max: 35, optimal: { min: 20, max: 28 } }
    },
    humidity: {
      value: generateRandomValue(55, 75),
      unit: '%',
      status: 'normal',
      range: { min: 30, max: 90, optimal: { min: 50, max: 80 } }
    },
    co2: {
      value: generateRandomValue(400, 650),
      unit: 'ppm',
      status: 'normal',
      range: { min: 200, max: 1000, optimal: { min: 350, max: 600 } }
    },
    soilPh: {
      value: generateRandomValue(5.5, 7.5),
      unit: '',
      status: 'normal',
      range: { min: 4.0, max: 9.0, optimal: { min: 5.5, max: 7.5 } }
    }
  }
}

export const productionData = {
  code: 200,
  message: 'success',
  data: {
    totalProduction: 125800,
    unit: 'kg',
    trend: [
      { month: '1月', value: 8500 },
      { month: '2月', value: 9200 },
      { month: '3月', value: 11500 },
      { month: '4月', value: 13800 },
      { month: '5月', value: 15200 },
      { month: '6月', value: 16800 },
      { month: '7月', value: 14500 },
      { month: '8月', value: 12800 },
      { month: '9月', value: 10500 },
      { month: '10月', value: 9200 },
      { month: '11月', value: 8500 },
      { month: '12月', value: 6800 }
    ],
    crops: [
      { name: '小麦', production: 45200, percentage: 36 },
      { name: '玉米', production: 38500, percentage: 31 },
      { name: '水稻', production: 26800, percentage: 21 },
      { name: '蔬菜', production: 15300, percentage: 12 }
    ]
  }
}

export const areaData = {
  code: 200,
  message: 'success',
  data: {
    totalArea: 2500,
    unit: '亩',
    crops: [
      { name: '小麦', area: 800, color: '#5470c6' },
      { name: '玉米', area: 750, color: '#91cc75' },
      { name: '水稻', area: 550, color: '#fac858' },
      { name: '蔬菜', area: 250, color: '#ee6666' },
      { name: '果树', area: 150, color: '#73c0de' }
    ],
    growthStage: [
      { stage: '播种期', percentage: 15 },
      { stage: '生长期', percentage: 45 },
      { stage: '成熟期', percentage: 25 },
      { stage: '收获期', percentage: 15 }
    ]
  }
}

export const historyData = {
  code: 200,
  message: 'success',
  data: generateHistoryData(24)
}