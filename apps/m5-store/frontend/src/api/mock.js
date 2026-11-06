import { ApiError } from './http'

// 클라우드 미리보기용 모의 데이터입니다. 실제 상품·주문이 아닙니다.
// 이 모듈은 VITE_USE_MOCK=true 일 때만 사용되며, 실제 API 실패 시 자동 전환되지 않습니다.
const GOODS = [
  { goodsId: 3, name: 'OTTer 키링', description: '가방과 열쇠에 달 수 있는 OTTer 키링', price: 7000, imageKey: null },
  { goodsId: 2, name: 'OTTer 에코백', description: '가볍게 사용할 수 있는 캔버스 에코백', price: 18000, imageKey: null },
  { goodsId: 1, name: 'OTTer 머그컵', description: 'OTTer 로고가 들어간 데일리 머그컵', price: 15000, imageKey: null },
]

const wait = (ms = 180) => new Promise((resolve) => setTimeout(resolve, ms))

let loggedIn = false
let nextCartId = 1
let nextOrderId = 1
let cart = []
let orders = []

const totalOf = (items) => items.reduce((sum, item) => sum + item.subtotal, 0)
const withSubtotal = (item) => ({ ...item, subtotal: item.unitPrice * item.quantity })

function requireLogin() {
  if (!loggedIn) throw new ApiError('로그인이 필요합니다.', { status: 401 })
}

export const mockApi = {
  async getGoods() {
    await wait()
    return GOODS.map((g) => ({ ...g }))
  },
  async getCart() {
    await wait(80)
    requireLogin()
    return { items: cart.map((i) => ({ ...i })), totalAmount: totalOf(cart) }
  },
  async addToCart(goodsId, quantity = 1) {
    await wait()
    requireLogin()
    const goods = GOODS.find((g) => g.goodsId === goodsId)
    if (!goods) throw new ApiError('상품을 찾을 수 없습니다.', { status: 404 })
    const existing = cart.find((i) => i.goodsId === goodsId)
    if (existing) {
      existing.quantity += quantity
      existing.subtotal = existing.unitPrice * existing.quantity
      return { ...existing }
    }
    const item = withSubtotal({
      cartItemId: nextCartId++,
      goodsId,
      goodsName: goods.name,
      imageKey: goods.imageKey,
      unitPrice: goods.price,
      quantity,
      active: true,
    })
    cart.push(item)
    return { ...item }
  },
  async updateCartItem(cartItemId, quantity) {
    await wait(100)
    requireLogin()
    const item = cart.find((i) => i.cartItemId === cartItemId)
    if (!item) throw new ApiError('장바구니 항목을 찾을 수 없습니다.', { status: 404 })
    item.quantity = quantity
    item.subtotal = item.unitPrice * quantity
    return { ...item }
  },
  async removeCartItem(cartItemId) {
    await wait(100)
    requireLogin()
    cart = cart.filter((i) => i.cartItemId !== cartItemId)
    return null
  },
  async signup({ loginId }) {
    await wait()
    if (!loginId) throw new ApiError('아이디를 입력해 주세요.', { status: 400 })
    return null
  },
  async login({ loginId, password }) {
    await wait()
    if (!loginId || !password) throw new ApiError('아이디와 비밀번호를 입력해 주세요.', { status: 400 })
    loggedIn = true
    return null
  },
  async logout() {
    await wait(80)
    loggedIn = false
    cart = []
  },
  async createOrder({ cartItemIds, recipientName, phone, postalCode, address, addressDetail }) {
    await wait(300)
    requireLogin()
    const picked = cart.filter((i) => cartItemIds.includes(i.cartItemId) && i.active)
    if (picked.length === 0 || picked.length !== cartItemIds.length) {
      throw new ApiError('주문할 수 없는 장바구니 항목이 포함되어 있습니다.', { status: 400 })
    }
    const order = {
      orderId: nextOrderId++,
      totalAmount: totalOf(picked),
      recipientName,
      phone,
      postalCode,
      address,
      addressDetail,
      createdAt: new Date().toISOString(),
      items: picked.map((i, idx) => ({
        orderItemId: idx + 1,
        goodsName: i.goodsName,
        unitPrice: i.unitPrice,
        quantity: i.quantity,
        subtotal: i.subtotal,
      })),
    }
    orders.unshift(order)
    cart = cart.filter((i) => !cartItemIds.includes(i.cartItemId))
    return { orderId: order.orderId }
  },
  async listOrders() {
    await wait()
    requireLogin()
    return {
      orders: orders.map(({ orderId, totalAmount, createdAt }) => ({ orderId, totalAmount, createdAt })),
      page: 0,
      totalPages: 1,
      totalElements: orders.length,
    }
  },
  async getOrder(orderId) {
    await wait()
    requireLogin()
    const order = orders.find((o) => o.orderId === Number(orderId))
    if (!order) throw new ApiError('주문을 찾을 수 없습니다.', { status: 404 })
    return { ...order }
  },
}
