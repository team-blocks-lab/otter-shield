import { useCallback, useEffect, useState } from 'react'
import { api } from '../api'

export function useOrder(orderId) {
  const [state, setState] = useState({ order: null, status: 'loading', error: null })
  const load = useCallback(() => {
    let alive = true
    setState({ order: null, status: 'loading', error: null })
    api
      .getOrder(orderId)
      .then((order) => alive && setState({ order, status: 'ready', error: null }))
      .catch((error) => alive && setState({ order: null, status: 'error', error }))
    return () => {
      alive = false
    }
  }, [orderId])
  useEffect(() => load(), [load])
  return { ...state, reload: load }
}
