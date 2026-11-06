import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { api } from '../api'
import { useAuth } from './AuthContext'

const CartContext = createContext(null)
const EMPTY = { items: [], totalAmount: 0 }

export function CartProvider({ children }) {
  const { user } = useAuth()
  const loggedIn = Boolean(user)
  const [cart, setCart] = useState(EMPTY)
  const [status, setStatus] = useState('idle') // idle | loading | ready | error
  const [error, setError] = useState(null)

  const refresh = useCallback(async () => {
    setStatus('loading')
    setError(null)
    try {
      setCart(await api.getCart())
      setStatus('ready')
    } catch (e) {
      setError(e)
      setStatus('error')
    }
  }, [])

  useEffect(() => {
    if (loggedIn) refresh()
    else {
      setCart(EMPTY)
      setStatus('idle')
    }
  }, [loggedIn, refresh])

  const add = useCallback(
    async (goodsId, quantity = 1) => {
      await api.addToCart(goodsId, quantity)
      await refresh()
    },
    [refresh],
  )
  const setQuantity = useCallback(
    async (cartItemId, quantity) => {
      await api.updateCartItem(cartItemId, quantity)
      await refresh()
    },
    [refresh],
  )
  const remove = useCallback(
    async (cartItemId) => {
      await api.removeCartItem(cartItemId)
      await refresh()
    },
    [refresh],
  )

  const value = useMemo(
    () => ({ ...cart, status, error, refresh, add, setQuantity, remove }),
    [cart, status, error, refresh, add, setQuantity, remove],
  )
  return <CartContext.Provider value={value}>{children}</CartContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useCart() {
  return useContext(CartContext)
}
