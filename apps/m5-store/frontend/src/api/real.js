import { request, loadCsrf, clearCsrf } from './http'

// 명세로 확인된 엔드포인트: GET /api/auth/csrf, GET /api/goods, GET /api/cart,
// POST /api/cart/items(응답만), POST /api/orders, GET /api/orders, GET /api/orders/{id}
// 아래에서 "확인 필요"로 표시한 항목은 명세가 없어 가정한 값입니다. README 참고.
const PATHS = {
  goods: '/api/goods',
  cart: '/api/cart',
  cartItems: '/api/cart/items', // 요청 본문 {goodsId, quantity}는 확인 필요
  cartItem: (id) => `/api/cart/items/${id}`, // 수량 변경·삭제 URL 확인 필요
  orders: '/api/orders',
  order: (id) => `/api/orders/${id}`,
  login: '/api/auth/login', // 확인 필요
  logout: '/api/auth/logout', // 확인 필요
  signup: '/api/auth/signup', // 확인 필요
}

export const realApi = {
  async getGoods() {
    const first = await request('GET', PATHS.goods)
    let goods = first?.goods ?? []
    const totalPages = first?.totalPages ?? 1
    // 다음 페이지 파라미터(?page=)는 확인 필요
    for (let page = 1; page < totalPages; page += 1) {
      const next = await request('GET', `${PATHS.goods}?page=${page}`)
      goods = goods.concat(next?.goods ?? [])
    }
    return goods
  },

  // 세션 확인용: 로그인 상태면 200, 아니면 401/403
  async getCart() {
    const body = await request('GET', PATHS.cart)
    return { items: body?.items ?? [], totalAmount: body?.totalAmount ?? 0 }
  },

  async addToCart(goodsId, quantity = 1) {
    return request('POST', PATHS.cartItems, { goodsId, quantity })
  },
  async updateCartItem(cartItemId, quantity) {
    return request('PATCH', PATHS.cartItem(cartItemId), { quantity })
  },
  async removeCartItem(cartItemId) {
    return request('DELETE', PATHS.cartItem(cartItemId))
  },

  async signup({ loginId, password }) {
    return request('POST', PATHS.signup, { loginId, password })
  },
  async login({ loginId, password }) {
    const body = await request('POST', PATHS.login, { loginId, password })
    // 로그인 성공 후에는 새 세션의 CSRF 토큰을 다시 받습니다.
    await loadCsrf()
    return body
  },
  async logout() {
    try {
      await request('POST', PATHS.logout)
    } finally {
      clearCsrf()
    }
    await loadCsrf().catch(() => {})
  },

  async createOrder({ cartItemIds, recipientName, phone, postalCode, address, addressDetail }) {
    return request('POST', PATHS.orders, {
      cartItemIds: cartItemIds.map(Number),
      recipientName,
      phone,
      postalCode,
      address,
      addressDetail,
    })
  },
  async listOrders(page = 0) {
    const body = await request('GET', page ? `${PATHS.orders}?page=${page}` : PATHS.orders)
    return {
      orders: body?.orders ?? [],
      page: body?.page ?? 0,
      totalPages: body?.totalPages ?? 1,
      totalElements: body?.totalElements ?? 0,
    }
  },
  async getOrder(orderId) {
    return request('GET', PATHS.order(orderId))
  },
}
